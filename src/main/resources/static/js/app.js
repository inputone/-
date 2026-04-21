const BASE_URL = "";

const { createApp, reactive, computed, onMounted, nextTick } = Vue;

const state = reactive({
  view: "login",
  tab: "posts",
  user: null,
  posts: [],
  total: 0,
  page: 1,
  size: 10,
  currentPost: null,
  comments: [],
  waitAuditUsers: [],
  loading: false,
  toast: null,
  postContent: "",
  commentContent: "",
  loginUsername: "",
  loginPassword: "",
  registerUsername: "",
  registerPassword: "",
  likedPosts: {},
  favoritedPosts: {},
  favoritePosts: [],
});

const totalPages = computed(() => Math.ceil(state.total / state.size) || 1);

function showToast(msg, type = "info") {
  state.toast = { msg, type };
  setTimeout(() => {
    state.toast = null;
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

async function api(method, path, body) {
  const opts = { method, credentials: "include" };
  if (body) {
    opts.headers = { "Content-Type": "application/x-www-form-urlencoded" };
    opts.body = body;
  }
  try {
    const res = await fetch(BASE_URL + path, opts);
    return res.json();
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
    checkLogin();
  } else {
    showToast(data.msg, "error");
  }
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
  } else {
    showToast(data.msg, "error");
  }
}

async function logout() {
  try {
    const data = await api("GET", "/user/logout");
    showToast(data.msg, "success");
  } catch {
    showToast("退出成功", "success");
  }
  setTimeout(() => {
    state.user = null;
    state.view = "login";
    state.posts = [];
    state.comments = [];
    state.waitAuditUsers = [];
    state.currentPost = null;
    state.page = 1;
  }, 800);
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
    } else {
      showToast(data.msg, "error");
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
  const data = await api("POST", `/post/like/${postId}`);
  if (data.code === 200) {
    const wasLiked = state.likedPosts[postId];
    state.likedPosts[postId] = !wasLiked;
    const post = state.posts.find((p) => p.id === postId);
    if (post) {
      post.likeCount = (post.likeCount || 0) + (wasLiked ? -1 : 1);
    }
    if (state.currentPost && state.currentPost.id === postId) {
      state.currentPost.likeCount =
        (state.currentPost.likeCount || 0) + (wasLiked ? -1 : 1);
    }
  } else {
    showToast(data.msg, "error");
  }
}

async function toggleFavorite(postId) {
  const data = await api("POST", `/post/favorite/${postId}`);
  if (data.code === 200) {
    const wasFavorited = state.favoritedPosts[postId];
    state.favoritedPosts[postId] = !wasFavorited;
    const post = state.posts.find((p) => p.id === postId);
    if (post) {
      post.favoriteCount = (post.favoriteCount || 0) + (wasFavorited ? -1 : 1);
    }
    if (state.currentPost && state.currentPost.id === postId) {
      state.currentPost.favoriteCount =
        (state.currentPost.favoriteCount || 0) + (wasFavorited ? -1 : 1);
    }
    showToast(wasFavorited ? "已取消收藏" : "已收藏", "success");
  } else {
    showToast(data.msg, "error");
  }
}

async function loadFavorites() {
  state.loading = true;
  try {
    const data = await api("GET", "/post/favorites");
    if (data.code === 200) {
      state.favoritePosts = data.data;
    } else {
      showToast(data.msg, "error");
    }
  } catch {
    showToast("加载收藏列表失败", "error");
  }
  state.loading = false;
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
    state.page = 1;
    loadPosts();
  } else {
    showToast(data.msg, "error");
  }
}

async function deletePost(postId) {
  const data = await api("DELETE", `/post/delete?id=${postId}`);
  if (data.code === 200) {
    showToast(data.msg, "success");
    loadPosts();
  } else {
    showToast(data.msg, "error");
  }
}

async function adminDeletePost(postId) {
  const data = await api("DELETE", `/admin/post/${postId}`);
  if (data.code === 200) {
    showToast(data.msg, "success");
    loadPosts();
  } else {
    showToast(data.msg, "error");
  }
}

function openPost(post) {
  state.currentPost = post;
  loadComments(post.id);
}

function closeComment() {
  state.currentPost = null;
  state.comments = [];
}

async function loadComments(postId) {
  state.loading = true;
  try {
    const data = await api("GET", `/comment/post/${postId}`);
    if (data.code === 200) {
      state.comments = data.data;
    } else {
      showToast(data.msg, "error");
    }
  } catch {
    showToast("加载评论失败", "error");
  }
  state.loading = false;
}

async function publishComment() {
  if (!state.commentContent) {
    showToast("评论内容不能为空", "warning");
    return;
  }
  const data = await api(
    "POST",
    "/comment",
    `postId=${state.currentPost.id}&content=${encodeURIComponent(state.commentContent)}`,
  );
  if (data.code === 200) {
    showToast(data.msg, "success");
    state.commentContent = "";
    loadComments(state.currentPost.id);
  } else {
    showToast(data.msg, "error");
  }
}

async function deleteComment(commentId) {
  const data = await api("DELETE", `/comment/${commentId}`);
  if (data.code === 200) {
    showToast(data.msg, "success");
    loadComments(state.currentPost.id);
  } else {
    showToast(data.msg, "error");
  }
}

async function adminDeleteComment(commentId) {
  const data = await api("DELETE", `/admin/comment/${commentId}`);
  if (data.code === 200) {
    showToast(data.msg, "success");
    loadComments(state.currentPost.id);
  } else {
    showToast(data.msg, "error");
  }
}

async function loadWaitAuditUsers() {
  state.loading = true;
  try {
    const data = await api("GET", "/admin/waitAuditUserList");
    if (data.code === 200) {
      state.waitAuditUsers = data.data;
    } else {
      showToast(data.msg, "error");
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
  } else {
    showToast(data.msg, "error");
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

const app = createApp({
  setup() {
    onMounted(() => {
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
      logout,
      publishPost,
      deletePost,
      adminDeletePost,
      openPost,
      closeComment,
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
    };
  },
});

app.mount("#app");
