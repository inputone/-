// ==================== 全局配置 ====================
const notyf = new Notyf({
    duration: 2000,
    position: { x: 'right', y: 'top' }
});

// ==================== 状态管理 ====================
const state = {
    allPosts: [],                // 所有帖子缓存
    currentPage: 1,              // 当前页码
    postsPerPage: 3,             // 每页显示5条
    commentDisplayCount: new Map() // 每个帖子显示的评论数
};

// ==================== 工具函数 ====================
const utils = {
    // 显示主界面
    showMain: (username) => {
        document.getElementById('currentUser').textContent = username;
        document.getElementById('authBox').classList.add('hidden');
        document.getElementById('mainBox').classList.remove('hidden');
        post.loadPosts(); // 加载帖子
    },

    // 更新分页控件
    updatePagination: () => {
        const totalPages = Math.ceil(state.allPosts.length / state.postsPerPage) || 1;
        document.getElementById('pageIndicator').textContent = `第 ${state.currentPage} / ${totalPages} 页`;
        document.getElementById('prevPageBtn').disabled = state.currentPage <= 1;
        document.getElementById('nextPageBtn').disabled = state.currentPage >= totalPages;
    },

    // 重置分页状态
    resetPagination: () => {
        state.currentPage = 1;
        state.commentDisplayCount.clear();
    }
};

// ==================== 认证模块 ====================
const auth = {
    login: () => {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        fetch('/user/login', {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`
        })
            .then(res => res.text())
            .then(msg => {
                if (msg.includes('成功')) {
                    notyf.success(msg);
                    utils.showMain(username);
                } else {
                    notyf.error(msg);
                }
            })
            .catch(() => notyf.error('登录请求失败'));
    },

    register: () => {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        fetch('/user/register', {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`
        })
            .then(res => res.text())
            .then(msg => {
                msg.includes('成功') ? notyf.success(msg) : notyf.error(msg);
            })
            .catch(() => notyf.error('注册请求失败'));
    },

    logout: () => {
        fetch('/user/logout', { credentials: 'include' })
            .then(res => res.text())
            .then(msg => {
                notyf.success(msg);
                setTimeout(() => location.reload(), 1500);
            })
            .catch(() => notyf.error('退出失败'));
    }
};

// ==================== 帖子模块 ====================
const post = {
    // 发布帖子
    publish: () => {
        const content = document.getElementById('postContent').value;
        if (!content.trim()) {
            notyf.error('内容不能为空');
            return;
        }

        fetch('/post/publish', {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `content=${encodeURIComponent(content)}`
        })
            .then(res => res.text())
            .then(msg => {
                notyf.success(msg);
                document.getElementById('postContent').value = '';
                post.loadPosts(); // 重新加载
            })
            .catch(() => notyf.error('发布失败'));
    },

    // 加载所有帖子
    loadPosts: () => {
        fetch('/post/list', { credentials: 'include' })
            .then(res => res.json())
            .then(data => {
                state.allPosts = data || [];
                utils.resetPagination();
                post.renderCurrentPage();
                utils.updatePagination();
            })
            .catch(() => notyf.error('加载帖子失败'));
    },

    // 渲染当前页
    renderCurrentPage: () => {
        const start = (state.currentPage - 1) * state.postsPerPage;
        const end = start + state.postsPerPage;
        const postsToShow = state.allPosts.slice(start, end);

        let html = '';
        postsToShow.forEach(post => {
            html += `
                <div class="post-card" id="post-card-${post.id}">
                    <div class="post-content">${post.content}</div>
                    <button onclick="comment.loadComments(${post.id})">💬 查看评论</button>
                    <div class="comment-section" id="comments-${post.id}"></div>
                    <div class="comment-input-area">
                        <input type="text" id="comment-${post.id}" placeholder="写下你的评论...">
                        <button onclick="comment.publish(${post.id})">发表评论</button>
                    </div>
                </div>
            `;
        });

        document.getElementById('postList').innerHTML = html;

        // 加载每个帖子的评论
        postsToShow.forEach(post => {
            const shownCount = state.commentDisplayCount.get(post.id) || 2;
            comment.loadComments(post.id, shownCount);
        });
    }
};

// ==================== 评论模块 ====================
const comment = {
    // 加载评论
    loadComments: (postId, displayLimit = 2) => {
        fetch(`/comment/post/${postId}`, { credentials: 'include' })
            .then(res => res.json())
            .then(allComments => {
                const limited = allComments.slice(0, displayLimit);
                const hasMore = allComments.length > displayLimit;

                let commentsHtml = limited.map(c =>
                    `<div class="comment">💬 ${c.content}</div>`
                ).join('');

                if (hasMore) {
                    commentsHtml += `<div><button class="more-btn" onclick="comment.showMore(${postId})">+ 查看更多评论 (${allComments.length - displayLimit}条)</button></div>`;
                }

                document.getElementById(`comments-${postId}`).innerHTML = commentsHtml;
                state.commentDisplayCount.set(postId, displayLimit);
            })
            .catch(() => {
                document.getElementById(`comments-${postId}`).innerHTML = '<div class="comment">加载评论失败</div>';
            });
    },

    // 显示更多评论
    showMore: (postId) => {
        const currentShown = state.commentDisplayCount.get(postId) || 2;
        const newLimit = currentShown + 3;
        state.commentDisplayCount.set(postId, newLimit);
        comment.loadComments(postId, newLimit);
    },

    // 发布评论
    publish: (postId) => {
        const content = document.getElementById(`comment-${postId}`).value;
        if (!content.trim()) {
            notyf.error('评论不能为空');
            return;
        }

        fetch('/comment', {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `postId=${postId}&content=${encodeURIComponent(content)}`
        })
            .then(res => res.text())
            .then(msg => {
                notyf.success(msg);
                document.getElementById(`comment-${postId}`).value = '';
                // 保持当前显示数量重新加载
                const currentShown = state.commentDisplayCount.get(postId) || 2;
                comment.loadComments(postId, currentShown);
            })
            .catch(() => notyf.error('评论失败'));
    }
};

// ==================== 分页模块 ====================
const pagination = {
    currentPage: state.currentPage,

    changePage: (newPage) => {
        const totalPages = Math.ceil(state.allPosts.length / state.postsPerPage) || 1;
        if (newPage < 1 || newPage > totalPages) return;

        state.currentPage = newPage;
        pagination.currentPage = newPage;
        post.renderCurrentPage();
        utils.updatePagination();
    }
};

// ==================== 暴露全局变量 ====================
window.auth = auth;
window.post = post;
window.comment = comment;
window.pagination = pagination;