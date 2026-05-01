const BASE_URL = "";

const { createApp, reactive, computed, onMounted } = Vue;

const state = reactive({
  initializing: true,
  loggedIn: false,
  loginView: "login",
  tab: "posts",
  user: null,
  posts: [],
  favorites: [],
  total: 0,
  totalPages: 1,
  page: 1,
  size: 10,
  waitAuditUsers: [],
  loading: false,
  adminLoading: false,
  toast: { show: false, msg: "", type: "info" },
  likedPosts: {},
  favoritedPosts: {},
  expandedComments: {},
  comments: {},
  newComments: {},
  aiReplies: {},
  aiLoading: {},
  showUserMenu: false,
  publishModal: { show: false, content: "" },
  modal: { show: false, message: "" },
  loginForm: { username: "", password: "" },
  registerForm: { username: "", password: "" },
  theme: localStorage.getItem("theme") || "light",
  debounceTimers: {},
});

const visiblePages = computed(() => {
  const pages = [];
  const total = state.totalPages;
  const current = state.page;
  if (total <= 7) {
    for (let i = 1; i <= total; i++) pages.push(i);
  } else {
    pages.push(1);
    if (current > 3) pages.push("...");
    for (
      let i = Math.max(2, current - 1);
      i <= Math.min(total - 1, current + 1);
      i++
    ) {
      pages.push(i);
    }
    if (current < total - 2) pages.push("...");
    pages.push(total);
  }
  return pages;
});

function showToast(msg, type = "info") {
  state.toast = { show: true, msg, type };
  setTimeout(() => {
    state.toast.show = false;
  }, 3000);
}

function formatTime(time) {
  if (!time) return "";
  const d = new Date(time);
  const now = new Date();
  const diff = now - d;
  if (diff < 60000) return "刚刚";
  if (diff < 3600000) return Math.floor(diff / 60000) + " 分钟前";
  if (diff < 86400000) return Math.floor(diff / 3600000) + " 小时前";
  if (diff < 604800000) return Math.floor(diff / 86400000) + " 天前";
  return d.toLocaleDateString("zh-CN", {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
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
      state.loggedIn = true;
      loadPosts();
    } else {
      state.loggedIn = false;
    }
  } catch {
    state.loggedIn = false;
  } finally {
    state.initializing = false;
  }
}

async function handleLogin() {
  if (!state.loginForm.username || !state.loginForm.password) {
    showToast("请输入用户名和密码", "warning");
    return;
  }
  const data = await api(
    "POST",
    "/user/login",
    `username=${state.loginForm.username}&password=${state.loginForm.password}`,
  );
  if (data.code === 200) {
    showToast("登录成功", "success");
    state.loginForm.password = "";
    setTimeout(() => {
      checkLogin();
    }, 300);
  }
}

async function handleRegister() {
  if (!state.registerForm.username || !state.registerForm.password) {
    showToast("请输入用户名和密码", "warning");
    return;
  }
  if (state.registerForm.password.length < 6) {
    showToast("密码至少6位", "warning");
    return;
  }
  const data = await api(
    "POST",
    "/user/register",
    `username=${state.registerForm.username}&password=${state.registerForm.password}`,
  );
  if (data.code === 200) {
    showToast("注册成功，请等待管理员审核", "success");
    state.loginForm.username = state.registerForm.username;
    state.loginForm.password = "";
    state.registerForm.username = "";
    state.registerForm.password = "";
    setTimeout(() => {
      state.loginView = "login";
    }, 1500);
  }
}

async function handleLogout() {
  try {
    await api("GET", "/user/logout");
  } catch {}
  showToast("已退出登录", "success");
  setTimeout(() => {
    state.user = null;
    state.loggedIn = false;
    state.posts = [];
    state.favorites = [];
    state.waitAuditUsers = [];
    state.page = 1;
    state.tab = "posts";
    state.showUserMenu = false;
    state.likedPosts = {};
    state.favoritedPosts = {};
    state.expandedComments = {};
    state.comments = {};
    state.aiReplies = {};
  }, 300);
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
      state.totalPages = Math.ceil(state.total / state.size) || 1;
      loadLikeAndFavoriteStatus();
    }
  } catch {
    showToast("加载帖子失败", "error");
  }
  state.loading = false;
}

async function loadFavorites() {
  state.loading = true;
  try {
    const data = await api("GET", "/post/favorites");
    if (data.code === 200) {
      state.favorites = data.data || [];
    }
  } catch {
    showToast("加载收藏失败", "error");
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
    showToast("操作太频繁", "warning");
    return;
  }
  if (!state.user) {
    showToast("请先登录", "warning");
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
    const favPost = state.favorites.find((p) => p.id === postId);
    if (favPost) {
      favPost.likeCount = Math.max(
        0,
        (favPost.likeCount || 0) + (wasLiked ? -1 : 1),
      );
    }
    showToast(
      wasLiked ? "取消点赞" : "点赞成功",
      wasLiked ? "info" : "success",
    );
  }
}

async function toggleFavorite(postId) {
  if (!debounce(`favorite_${postId}`)) {
    showToast("操作太频繁", "warning");
    return;
  }
  if (!state.user) {
    showToast("请先登录", "warning");
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
    if (wasFavorited) {
      state.favorites = state.favorites.filter((p) => p.id !== postId);
      showToast("取消收藏", "info");
    } else {
      showToast("收藏成功", "success");
    }
  }
}

async function toggleComments(postId) {
  state.expandedComments[postId] = !state.expandedComments[postId];
  if (state.expandedComments[postId] && !state.comments[postId]) {
    await loadComments(postId);
    await loadAiReplies(postId);
  }
}

async function loadComments(postId) {
  try {
    const data = await api("GET", `/comment/post/${postId}`);
    if (data.code === 200) {
      state.comments[postId] = data.data || [];
    }
  } catch {}
}

async function loadAiReplies(postId) {
  try {
    const data = await api("GET", `/ai/reply/${postId}`);
    if (data.code === 200) {
      state.aiReplies[postId] = data.data || [];
    }
  } catch {}
}

async function submitComment(postId) {
  const content = state.newComments[postId];
  if (!content || !content.trim()) {
    showToast("请输入评论内容", "warning");
    return;
  }
  if (!debounce(`comment_${postId}`)) {
    showToast("操作太频繁", "warning");
    return;
  }
  const data = await api(
    "POST",
    "/comment",
    `postId=${postId}&content=${encodeURIComponent(content)}`,
  );
  if (data.code === 200) {
    showToast("评论成功", "success");
    state.newComments[postId] = "";
    await loadComments(postId);
    const post = state.posts.find((p) => p.id === postId);
    if (post) {
      post.commentCount = (post.commentCount || 0) + 1;
    }
  }
}

async function deleteComment(commentId, postId) {
  const data = await api("DELETE", `/comment/${commentId}`);
  if (data.code === 200) {
    showToast("删除成功", "success");
    state.comments[postId] = state.comments[postId].filter(
      (c) => c.id !== commentId,
    );
    const post = state.posts.find((p) => p.id === postId);
    if (post && post.commentCount > 0) {
      post.commentCount -= 1;
    }
  }
}

async function generateAiReply(postId) {
  if (!state.user) {
    showToast("请先登录", "warning");
    return;
  }
  if (!debounce(`ai_${postId}`)) {
    showToast("操作太频繁", "warning");
    return;
  }
  state.aiLoading[postId] = true;
  try {
    const data = await api("POST", `/ai/reply/${postId}`);
    if (data.code === 200) {
      showToast("AI 回复已生成", "success");
      await loadAiReplies(postId);
    }
  } catch {
    showToast("AI 回复生成失败", "error");
  }
  state.aiLoading[postId] = false;
}

function openPublishModal() {
  state.publishModal.show = true;
  state.publishModal.content = "";
}

function closePublishModal() {
  state.publishModal.show = false;
}

function updateCharCount() {}

async function handlePublish() {
  const content = state.publishModal.content;
  if (!content || !content.trim()) {
    showToast("请输入帖子内容", "warning");
    return;
  }
  if (content.length > 500) {
    showToast("帖子内容不能超过500字", "warning");
    return;
  }
  const data = await api(
    "POST",
    "/post/publish",
    `content=${encodeURIComponent(content)}`,
  );
  if (data.code === 200) {
    showToast("发布成功", "success");
    closePublishModal();
    state.page = 1;
    await loadPosts();
  }
}

async function deletePost(postId) {
  const data = await api("DELETE", `/post/delete?id=${postId}`);
  if (data.code === 200) {
    showToast("删除成功", "success");
    state.posts = state.posts.filter((p) => p.id !== postId);
  }
}

async function loadWaitAuditUsers() {
  state.adminLoading = true;
  try {
    const data = await api("GET", "/admin/waitAuditUserList");
    if (data.code === 200) {
      state.waitAuditUsers = data.data || [];
    }
  } catch {
    showToast("加载待审核用户失败", "error");
  }
  state.adminLoading = false;
}

async function auditUser(userId, status) {
  const data = await api(
    "POST",
    `/admin/auditUser?userId=${userId}&status=${status}`,
  );
  if (data.code === 200) {
    showToast(status === 1 ? "已通过审核" : "已封禁用户", "success");
    state.waitAuditUsers = state.waitAuditUsers.filter((u) => u.id !== userId);
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

function changePage(page) {
  if (page < 1 || page > state.totalPages) return;
  state.page = page;
  loadPosts();
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function closeModal() {
  state.modal.show = false;
}

function toggleTheme() {
  state.theme = state.theme === "light" ? "dark" : "light";
  document.documentElement.setAttribute("data-theme", state.theme);
  localStorage.setItem("theme", state.theme);
}

document.addEventListener("keydown", (e) => {
  if (e.key === "Escape") {
    if (state.publishModal.show) {
      closePublishModal();
    }
    if (state.modal.show) {
      closeModal();
    }
    state.showUserMenu = false;
  }
});

document.addEventListener("click", (e) => {
  if (
    state.showUserMenu &&
    !e.target.closest(".user-avatar-mini") &&
    !e.target.closest(".user-menu")
  ) {
    state.showUserMenu = false;
  }
});

createApp({
  setup() {
    onMounted(() => {
      const savedTheme = localStorage.getItem("theme") || "light";
      state.theme = savedTheme;
      document.documentElement.setAttribute("data-theme", savedTheme);
      checkLogin();
    });

    return { state, visiblePages, showToast, formatTime, toggleTheme };
  },
  methods: {
    handleLogin,
    handleRegister,
    handleLogout,
    handlePublish,
    openPublishModal,
    closePublishModal,
    updateCharCount,
    switchTab,
    changePage,
    closeModal,
    toggleLike,
    toggleFavorite,
    toggleComments,
    submitComment,
    deleteComment,
    deletePost,
    generateAiReply,
    auditUser,
  },
}).mount("#app");
