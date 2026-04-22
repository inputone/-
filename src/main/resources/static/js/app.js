const BASE_URL = "";

const { createApp, reactive, computed, onMounted } = Vue;

const state = reactive({
  view: "login",
  tab: "posts",
  user: null,
  posts: [],
  total: 0,
  page: 1,
  size: 10,
  waitAuditUsers: [],
  loading: false,
  toast: { show: false, msg: "", type: "info" },
  postContent: "",
  loginUsername: "",
  loginPassword: "",
  registerUsername: "",
  registerPassword: "",
  showRegister: false,
  likedPosts: {},
  favoritedPosts: {},
  favoritePosts: [],
  theme: localStorage.getItem("theme") || "dark",
  expandedPosts: {},
  postComments: {},
  commentInputs: {},
  commentLoading: {},
  aiReplies: {},
  aiLoading: {},
  showUserMenu: false,
  publishModal: {
    show: false,
  },
  modal: {
    show: false,
    title: "",
    text: "",
    actionText: "确认",
    callback: null,
  },
  debounceTimers: {},
});

const totalPages = computed(() => Math.ceil(state.total / state.size) || 1);

function showToast(msg, type = "info") {
  state.toast = { show: true, msg, type };
  setTimeout(() => {
    state.toast.show = false;
  }, 2500);
}

function formatTime(time) {
  if (!time) return "";
  const d = new Date(time);
  const now = new Date();
  const diff = now - d;
  if (diff < 60000) return "刚刚";
  if (diff < 3600000) return Math.floor(diff / 60000) + "分钟前";
  if (diff < 86400000) return Math.floor(diff / 3600000) + "小时前";
  return d.toLocaleDateString("zh-CN", {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str;
  return div.innerHTML;
}

function debounce(key, fn) {
  if (state.debounceTimers[key]) {
    return false;
  }
  state.debounceTimers[key] = setTimeout(() => {
    delete state.debounceTimers[key];
  }, 500);
  return true;
}

async function api(method, path, body) {
  const opts = { method, credentials: "include" };
  if (body) {
    opts.headers = { "Content-Type": "application/x-www-form-urlencoded" };
    opts.body = body;
  }
  try {
    const res = await fetch(BASE_URL + path, opts);
    const data = await res.json();
    if (data.code !== 200) {
      showToast(data.msg, "error");
    }
    return data;
  } catch (err) {
    showToast("网络请求失败，请检查后端服务", "error");
    throw err;
  }
}

async function checkLogin() {
  try {
    const data = await api("GET", "/user/me");
    if (data.code === 200 && data.data) {
      state.user = data.data;
      state.view = "main";
      loadPosts();
    } else {
      state.view = "login";
    }
  } catch {
    state.view = "login";
  }
}

async function login() {
  if (!state.loginUsername || !state.loginPassword) {
    showToast("用户名和密码不能为空", "warning");
    return;
  }
  const data = await api(
    "POST",
    "/user/login",
    `username=${state.loginUsername}&password=${state.loginPassword}`,
  );
  if (data.code === 200) {
    showToast(data.msg, "success");
    state.loginPassword = "";
    checkLogin();
  }
}

function switchToRegister() {
  state.showRegister = true;
  state.registerUsername = "";
  state.registerPassword = "";
}

function switchToLogin() {
  state.showRegister = false;
}

async function register() {
  if (!state.registerUsername || !state.registerPassword) {
    showToast("用户名和密码不能为空", "warning");
    return;
  }
  if (state.registerPassword.length < 6) {
    showToast("密码至少6位", "warning");
    return;
  }
  const data = await api(
    "POST",
    "/user/register",
    `username=${state.registerUsername}&password=${state.registerPassword}`,
  );
  if (data.code === 200) {
    showToast(data.msg, "success");
    state.loginUsername = state.registerUsername;
    state.loginPassword = "";
    state.registerUsername = "";
    state.registerPassword = "";
    state.showRegister = false;
  }
}

async function logout() {
  try {
    await api("GET", "/user/logout");
  } catch {}
  showToast("已退出", "success");
  setTimeout(() => {
    state.user = null;
    state.view = "login";
    state.posts = [];
    state.waitAuditUsers = [];
    state.page = 1;
    state.tab = "posts";
    state.showUserMenu = false;
  }, 500);
}

function toggleUserMenu() {
  state.showUserMenu = !state.showUserMenu;
}

function togglePublishModal() {
  state.publishModal.show = !state.publishModal.show;
  if (state.publishModal.show) {
    state.postContent = "";
  }
}

function closePublishModal() {
  state.publishModal.show = false;
}

async function loadPosts() {
  state.loading = true;
  try {
    const data = await api(
      "GET",
      `/post/page?page=${state.page}&size=${state.size}`,
    );
    if (data.code === 200) {
      state.posts = data.data.list;
      state.total = data.data.total;
      loadLikeAndFavoriteStatus();
    }
  } catch {
    showToast("加载帖子失败", "error");
  }
  state.loading = false;
}

async function loadLikeAndFavoriteStatus() {
  if (!state.user) return;
  for (const post of state.posts) {
    checkLiked(post.id);
    checkFavorited(post.id);
  }
}

async function checkLiked(postId) {
  try {
    const data = await api("GET", `/post/liked/${postId}`);
    if (data.code === 200) {
      state.likedPosts[postId] = data.data.liked;
    }
  } catch {}
}

async function checkFavorited(postId) {
  try {
    const data = await api("GET", `/post/favorited/${postId}`);
    if (data.code === 200) {
      state.favoritedPosts[postId] = data.data.favorited;
    }
  } catch {}
}

async function toggleLike(postId) {
  if (!debounce(`like_${postId}`)) {
    showToast("操作太频繁，请稍后再试", "warning");
    return;
  }
  const data = await api("POST", `/post/like/${postId}`);
  if (data.code === 200) {
    const wasLiked = state.likedPosts[postId];
    state.likedPosts[postId] = !wasLiked;
    const post = state.posts.find((p) => p.id === postId);
    if (post) {
      post.likeCount = Math.max(0, (post.likeCount || 0) + (wasLiked ? -1 : 1));
    }
    const favPost = state.favoritePosts.find((p) => p.id === postId);
    if (favPost) {
      favPost.likeCount = Math.max(
        0,
        (favPost.likeCount || 0) + (wasLiked ? -1 : 1),
      );
    }
  }
}

async function toggleFavorite(postId) {
  if (!debounce(`favorite_${postId}`)) {
    showToast("操作太频繁，请稍后再试", "warning");
    return;
  }
  const data = await api("POST", `/post/favorite/${postId}`);
  if (data.code === 200) {
    const wasFavorited = state.favoritedPosts[postId];
    state.favoritedPosts[postId] = !wasFavorited;
    const post = state.posts.find((p) => p.id === postId);
    if (post) {
      post.favoriteCount = Math.max(
        0,
        (post.favoriteCount || 0) + (wasFavorited ? -1 : 1),
      );
    }
    const favPost = state.favoritePosts.find((p) => p.id === postId);
    if (favPost) {
      favPost.favoriteCount = Math.max(
        0,
        (favPost.favoriteCount || 0) + (wasFavorited ? -1 : 1),
      );
    }
    showToast(wasFavorited ? "已取消收藏" : "已收藏", "success");
  }
}

async function loadFavorites() {
  state.loading = true;
  try {
    const data = await api("GET", "/post/favorites");
    if (data.code === 200) {
      state.favoritePosts = data.data;
      if (state.user) {
        for (const post of state.favoritePosts) {
          checkLiked(post.id);
          checkFavorited(post.id);
        }
      }
    }
  } catch {
    showToast("加载收藏列表失败", "error");
  }
  state.loading = false;
}

async function generateAiReply(postId) {
  if (!debounce(`ai_${postId}`)) {
    showToast("操作太频繁，请稍后再试", "warning");
    return;
  }
  state.aiLoading[postId] = true;
  try {
    const data = await api("POST", `/ai/reply/${postId}`);
    if (data.code === 200) {
      if (!state.aiReplies[postId]) {
        state.aiReplies[postId] = [];
      }
      state.aiReplies[postId].unshift(data.data);
      showToast("AI回复已生成", "success");
    }
  } catch {
    showToast("AI回复生成失败", "error");
  }
  state.aiLoading[postId] = false;
}

async function loadAiReplies(postId) {
  if (state.aiReplies[postId]) return;
  try {
    const data = await api("GET", `/ai/reply/${postId}`);
    if (data.code === 200) {
      state.aiReplies[postId] = data.data;
    }
  } catch {}
}

async function publishPost() {
  if (!state.postContent) {
    showToast("帖子内容不能为空", "warning");
    return;
  }
  const data = await api(
    "POST",
    "/post/publish",
    `content=${encodeURIComponent(state.postContent)}`,
  );
  if (data.code === 200) {
    showToast(data.msg, "success");
    state.postContent = "";
    closePublishModal();
    state.page = 1;
    loadPosts();
  }
}

function confirmDeletePost(post) {
  state.modal = {
    show: true,
    title: "删除帖子",
    text: "确定要删除这条帖子吗？此操作不可恢复。",
    actionText: "删除",
    callback: () => deletePost(post.id),
  };
}

async function deletePost(postId) {
  const data = await api("DELETE", `/post/delete?id=${postId}`);
  if (data.code === 200) {
    showToast(data.msg, "success");
    loadPosts();
  }
  closeModal();
}

async function adminDeletePost(postId) {
  const data = await api("DELETE", `/admin/post/${postId}`);
  if (data.code === 200) {
    showToast(data.msg, "success");
    loadPosts();
  }
}

async function toggleComments(post) {
  const postId = post.id;
  if (state.expandedPosts[postId]) {
    state.expandedPosts[postId] = false;
  } else {
    state.expandedPosts[postId] = true;
    if (!state.postComments[postId]) {
      loadComments(postId);
    }
    if (!state.aiReplies[postId]) {
      loadAiReplies(postId);
    }
  }
}

async function loadComments(postId) {
  state.commentLoading[postId] = true;
  try {
    const data = await api("GET", `/comment/post/${postId}`);
    if (data.code === 200) {
      state.postComments[postId] = data.data;
    }
  } catch {
    showToast("加载评论失败", "error");
  }
  state.commentLoading[postId] = false;
}

async function publishComment(postId) {
  const content = state.commentInputs[postId];
  if (!content) {
    showToast("评论内容不能为空", "warning");
    return;
  }
  const data = await api(
    "POST",
    "/comment",
    `postId=${postId}&content=${encodeURIComponent(content)}`,
  );
  if (data.code === 200) {
    showToast(data.msg, "success");
    state.commentInputs[postId] = "";
    loadComments(postId);
  }
}

function confirmDeleteComment(comment) {
  state.modal = {
    show: true,
    title: "删除评论",
    text: "确定要删除这条评论吗？此操作不可恢复。",
    actionText: "删除",
    callback: () => deleteComment(comment.id),
  };
}

async function deleteComment(commentId) {
  const data = await api("DELETE", `/comment/${commentId}`);
  if (data.code === 200) {
    showToast(data.msg, "success");
    for (const postId in state.postComments) {
      loadComments(parseInt(postId));
    }
  }
  closeModal();
}

async function adminDeleteComment(commentId) {
  const data = await api("DELETE", `/admin/comment/${commentId}`);
  if (data.code === 200) {
    showToast(data.msg, "success");
    for (const postId in state.postComments) {
      loadComments(parseInt(postId));
    }
  }
}

async function loadWaitAuditUsers() {
  state.loading = true;
  try {
    const data = await api("GET", "/admin/waitAuditUserList");
    if (data.code === 200) {
      state.waitAuditUsers = data.data;
    }
  } catch {
    showToast("加载待审核用户失败", "error");
  }
  state.loading = false;
}

async function auditUser(username, status) {
  const data = await api(
    "POST",
    "/admin/auditUser",
    `username=${username}&status=${status}`,
  );
  if (data.code === 200) {
    showToast(data.msg, "success");
    loadWaitAuditUsers();
  }
}

function switchTab(tab) {
  state.tab = tab;
  if (tab === "posts") {
    loadPosts();
  } else if (tab === "favorites") {
    loadFavorites();
  } else if (tab === "admin") {
    loadWaitAuditUsers();
  }
}

function goToPage(p) {
  if (p < 1 || p > totalPages.value) return;
  state.page = p;
  loadPosts();
}

function getPageNumbers() {
  const pages = [];
  const total = totalPages.value;
  const current = state.page;
  if (total <= 7) {
    for (let i = 1; i <= total; i++) pages.push(i);
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) pages.push(i);
      pages.push("...");
      pages.push(total);
    } else if (current >= total - 3) {
      pages.push(1);
      pages.push("...");
      for (let i = total - 4; i <= total; i++) pages.push(i);
    } else {
      pages.push(1);
      pages.push("...");
      for (let i = current - 1; i <= current + 1; i++) pages.push(i);
      pages.push("...");
      pages.push(total);
    }
  }
  return pages;
}

function canDeletePost(post) {
  if (!state.user) return false;
  if (state.user.status === 99) return true;
  return post.userId === state.user.id;
}

function canDeleteComment(comment) {
  if (!state.user) return false;
  if (state.user.status === 99) return true;
  return comment.userId === state.user.id;
}

function closeModal() {
  state.modal.show = false;
}

function confirmAction() {
  if (state.modal.callback) {
    state.modal.callback();
  }
  closeModal();
}

function toggleTheme() {
  state.theme = state.theme === "dark" ? "light" : "dark";
  localStorage.setItem("theme", state.theme);
  document.documentElement.setAttribute("data-theme", state.theme);
}

function initTheme() {
  const saved = localStorage.getItem("theme") || "dark";
  state.theme = saved;
  document.documentElement.setAttribute("data-theme", saved);
}

const app = createApp({
  setup() {
    onMounted(() => {
      initTheme();
      checkLogin();
    });

    return {
      state,
      totalPages,
      showToast,
      formatTime,
      escapeHtml,
      login,
      register,
      switchToRegister,
      switchToLogin,
      logout,
      toggleUserMenu,
      togglePublishModal,
      closePublishModal,
      publishPost,
      deletePost,
      adminDeletePost,
      toggleComments,
      publishComment,
      deleteComment,
      adminDeleteComment,
      auditUser,
      switchTab,
      goToPage,
      getPageNumbers,
      canDeletePost,
      canDeleteComment,
      toggleLike,
      toggleFavorite,
      loadFavorites,
      toggleTheme,
      confirmDeletePost,
      confirmDeleteComment,
      closeModal,
      confirmAction,
      generateAiReply,
      loadAiReplies,
    };
  },
});

app.mount("#app");
