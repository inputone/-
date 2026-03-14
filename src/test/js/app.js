const BASE_URL = "";
let currentUser = null;

// ========== 核心新增：轻量级提示函数（替代alert） ==========
function showToast(msg, type = "info") {
    // 创建提示框元素
    const toast = document.createElement("div");
    // 设置样式（固定在右上角，2秒消失）
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 10px 20px;
        border-radius: 8px;
        color: white;
        font-size: 14px;
        z-index: 9999;
        opacity: 0;
        transition: opacity 0.3s ease;
    `;
    // 根据提示类型设置背景色
    if (type === "success") {
        toast.style.backgroundColor = "#52c41a"; // 成功-绿色
    } else if (type === "error") {
        toast.style.backgroundColor = "#ff4d4f"; // 错误-红色
    } else if (type === "warning") {
        toast.style.backgroundColor = "#faad14"; // 警告-黄色
    } else {
        toast.style.backgroundColor = "#1890ff"; // 普通-蓝色
    }
    // 设置提示文本
    toast.textContent = msg;
    // 添加到页面
    document.body.appendChild(toast);
    // 显示提示
    setTimeout(() => {
        toast.style.opacity = "1";
    }, 10);
    // 2秒后隐藏并移除
    setTimeout(() => {
        toast.style.opacity = "0";
        setTimeout(() => {
            document.body.removeChild(toast);
        }, 300);
    }, 2000);
}

// ========== 原有函数修改（替换所有alert） ==========
function api(url, options = {}) {
    return fetch(BASE_URL + url, {
        credentials: "include",
        ...options
    }).then(res => res.json())
        // 新增：捕获接口请求失败的提示
        .catch(err => {
            showToast("网络请求失败，请检查后端服务", "error");
            throw err;
        });
}

function checkLogin() {
    api("/user/me").then(res => {
        if (res.code === 200) {
            currentUser = res.data;
            showMain();
        }
    });
}

function showMain() {
    document.getElementById("authBox").classList.add("hidden");
    document.getElementById("mainBox").classList.remove("hidden");

    let text = currentUser.username;
    if (currentUser.status === 99) {
        text += " <span class='admin-tag'>管理员</span>";
    }
    document.getElementById("currentUser").innerHTML = text;

    loadPosts();
}

function login() {
    // 新增：空值校验
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    if (!username || !password) {
        showToast("用户名和密码不能为空", "warning");
        return;
    }

    api("/user/login", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `username=${username}&password=${password}`
    }).then(res => {
        // 替换alert为toast
        if (res.code === 200) {
            showToast(res.msg, "success");
            checkLogin();
        } else {
            showToast(res.msg, "error");
        }
    });
}

function register() {
    // 新增：空值校验
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    if (!username || !password) {
        showToast("用户名和密码不能为空", "warning");
        return;
    }

    api("/user/register", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `username=${username}&password=${password}`
    }).then(res => {
        // 替换alert为toast
        if (res.code === 200) {
            showToast(res.msg, "success");
        } else {
            showToast(res.msg, "error");
        }
    });
}

function logout() {
    api("/user/logout").then(res => {
        // 替换alert为toast
        showToast(res.msg, "success");
        // 延迟刷新，让提示显示完整
        setTimeout(() => {
            location.reload();
        }, 1000);
    }).catch(() => {
        // 接口调用失败也强制退出
        showToast("退出成功", "success");
        setTimeout(() => {
            location.reload();
        }, 1000);
    });
}

function publishPost() {
    // 新增：空值校验
    const content = document.getElementById("postContent").value;
    if (!content) {
        showToast("帖子内容不能为空", "warning");
        return;
    }

    api("/post/publish", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `content=${content}`
    }).then(res => {
        // 替换alert为toast
        if (res.code === 200) {
            showToast(res.msg, "success");
            document.getElementById("postContent").value = "";
            loadPosts();
        } else {
            showToast(res.msg, "error");
        }
    });
}

function loadPosts() {
    api("/post/list").then(res => {
        if (res.code !== 200) {
            showToast(res.msg, "error");
            return;
        }

        let html = "";
        res.data.forEach(post => {
            html += `
                <div class="card">
                    <div class="post-content">${post.content}</div>
                    ${renderPostActions(post)}
                    <div id="comments-${post.id}"></div>
                    <input id="comment-${post.id}" placeholder="写评论...">
                    <button onclick="publishComment(${post.id})">评论</button>
                </div>
            `;
        });
        document.getElementById("postList").innerHTML = html;
    });
}

function renderPostActions(post) {
    if (!currentUser) return "";

    let btn = "";

    if (currentUser.status === 99) {
        btn += `<button class="btn-link" onclick="adminDeletePost(${post.id})">删除</button>`;
    } else if (post.userId === currentUser.id) {
        btn += `<button class="btn-link" onclick="deletePost(${post.id})">删除</button>`;
    }

    btn += `<button class="btn-link" onclick="loadComments(${post.id})">查看评论</button>`;

    return btn;
}

function deletePost(id) {
    api(`/post/delete?id=${id}`, { method: "DELETE" })
        .then(res => {
            // 替换alert为toast
            if (res.code === 200) {
                showToast(res.msg, "success");
            } else {
                showToast(res.msg, "error");
            }
            loadPosts();
        });
}

function adminDeletePost(id) {
    api(`/admin/post/${id}`, { method: "DELETE" })
        .then(res => {
            // 替换alert为toast
            if (res.code === 200) {
                showToast(res.msg, "success");
            } else {
                showToast(res.msg, "error");
            }
            loadPosts();
        });
}

function publishComment(postId) {
    const content = document.getElementById(`comment-${postId}`).value;
    // 新增：空值校验
    if (!content) {
        showToast("评论内容不能为空", "warning");
        return;
    }

    api("/comment", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `postId=${postId}&content=${content}`
    }).then(res => {
        // 替换alert为toast
        if (res.code === 200) {
            showToast(res.msg, "success");
            document.getElementById(`comment-${postId}`).value = "";
            loadComments(postId);
        } else {
            showToast(res.msg, "error");
        }
    });
}

function loadComments(postId) {
    api(`/comment/post/${postId}`).then(res => {
        if (res.code !== 200) {
            showToast(res.msg, "error");
            return;
        }

        let html = "";
        res.data.forEach(c => {
            html += `
                <div class="comment">
                    ${c.content}
                    ${renderCommentActions(c, postId)}
                </div>
            `;
        });

        document.getElementById(`comments-${postId}`).innerHTML = html;
    });
}

function renderCommentActions(comment, postId) {
    if (!currentUser) return "";

    if (currentUser.status === 99) {
        return `<button class="btn-link" onclick="adminDeleteComment(${comment.id}, ${postId})">删除</button>`;
    }

    if (comment.userId === currentUser.id) {
        return `<button class="btn-link" onclick="deleteComment(${comment.id}, ${postId})">删除</button>`;
    }

    return "";
}

function deleteComment(id, postId) {
    api(`/comment/${id}`, { method: "DELETE" })
        .then(res => {
            // 替换alert为toast
            if (res.code === 200) {
                showToast(res.msg, "success");
            } else {
                showToast(res.msg, "error");
            }
            loadComments(postId);
        });
}

function adminDeleteComment(id, postId) {
    api(`/admin/comment/${id}`, { method: "DELETE" })
        .then(res => {
            // 替换alert为toast
            if (res.code === 200) {
                showToast(res.msg, "success");
            } else {
                showToast(res.msg, "error");
            }
            loadComments(postId);
        });
}

checkLogin();