// API エンドポイント
const API_BASE = '/api';
const API_TASKS = `${API_BASE}/tasks`;
const API_STATS = `${API_BASE}/stats`;
const API_CATEGORIES = `${API_BASE}/categories`;

// DOM要素の取得
const taskForm = document.getElementById('task-form');
const editForm = document.getElementById('edit-form');
const editModal = document.getElementById('edit-modal');
const tasksListElement = document.getElementById('tasks-list');
const noTasksElement = document.getElementById('no-tasks');
const searchInput = document.getElementById('search-input');
const searchBtn = document.getElementById('search-btn');
const statusFilter = document.getElementById('status-filter');
const priorityFilter = document.getElementById('priority-filter');
const categoryFilter = document.getElementById('category-filter');
const clearFiltersBtn = document.getElementById('clear-filters');

// アプリケーション状態
let tasks = [];
let currentFilters = {
    search: '',
    status: 'all',
    priority: 'all',
    category: 'all'
};

// 初期化
document.addEventListener('DOMContentLoaded', async () => {
    await loadTasks();
    await loadStatistics();
    await loadCategories();
    setupEventListeners();
    setDefaultDueDateTime(); // 期限日のデフォルト値を設定
});

// イベントリスナーの設定
function setupEventListeners() {
    // フォーム送信
    taskForm.addEventListener('submit', handleTaskSubmit);
    editForm.addEventListener('submit', handleEditSubmit);
    
    // 検索
    searchInput.addEventListener('input', debounce(handleSearch, 300));
    searchBtn.addEventListener('click', handleSearch);
    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleSearch();
        }
    });
    
    // フィルター
    statusFilter.addEventListener('change', handleFilterChange);
    priorityFilter.addEventListener('change', handleFilterChange);
    categoryFilter.addEventListener('change', handleFilterChange);
    clearFiltersBtn.addEventListener('click', clearFilters);
    
    // 期限日入力フィールドのフォーカスイベント
    const dueDateInput = document.getElementById('task-due-date');
    const editDueDateInput = document.getElementById('edit-task-due-date');
    
    if (dueDateInput) {
        dueDateInput.addEventListener('focus', () => {
            if (!dueDateInput.value) {
                const now = new Date();
                // 現在の日付で23:59を設定（時差を考慮）
                const year = now.getFullYear();
                const month = String(now.getMonth() + 1).padStart(2, '0');
                const day = String(now.getDate()).padStart(2, '0');
                dueDateInput.value = `${year}-${month}-${day}T23:59`;
            }
        });
    }
    
    if (editDueDateInput) {
        editDueDateInput.addEventListener('focus', () => {
            if (!editDueDateInput.value) {
                const now = new Date();
                // 現在の日付で23:59を設定（時差を考慮）
                const year = now.getFullYear();
                const month = String(now.getMonth() + 1).padStart(2, '0');
                const day = String(now.getDate()).padStart(2, '0');
                editDueDateInput.value = `${year}-${month}-${day}T23:59`;
            }
        });
    }
    
    // モーダル
    document.querySelector('.modal-close').addEventListener('click', closeModal);
    document.querySelector('.modal-cancel').addEventListener('click', closeModal);
    editModal.addEventListener('click', (e) => {
        if (e.target === editModal) {
            closeModal();
        }
    });
    
    // ESCキーでモーダルを閉じる
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && editModal.style.display === 'block') {
            closeModal();
        }
    });
}

// デバウンス関数
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// タスク作成フォーム送信
async function handleTaskSubmit(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const taskData = {
        title: formData.get('title'),
        description: formData.get('description') || '',
        priority: formData.get('priority'),
        category: formData.get('category') || '一般',
        tags: formData.get('tags') || '',
        dueDate: formData.get('dueDate') || null
    };
    
    try {
        const response = await fetch(API_TASKS, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskData)
        });
        
        if (response.ok) {
            const newTask = await response.json();
            showNotification('タスクが作成されました', 'success');
            taskForm.reset();
            setDefaultDueDateTime(); // フォームリセット後にデフォルト値を再設定
            await loadTasks();
            await loadStatistics();
            await loadCategories();
        } else {
            throw new Error('タスクの作成に失敗しました');
        }
    } catch (error) {
        console.error('Error creating task:', error);
        showNotification('タスクの作成に失敗しました', 'error');
    }
}

// タスク編集フォーム送信
async function handleEditSubmit(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const taskId = parseInt(formData.get('id'));
    const taskData = {
        title: formData.get('title'),
        description: formData.get('description') || '',
        priority: formData.get('priority'),
        category: formData.get('category') || '一般',
        tags: formData.get('tags') || '',
        dueDate: formData.get('dueDate') || null,
        completed: formData.get('completed') === 'on'
    };
    
    try {
        const response = await fetch(`${API_TASKS}/${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskData)
        });
        
        if (response.ok) {
            showNotification('タスクが更新されました', 'success');
            closeModal();
            await loadTasks();
            await loadStatistics();
            await loadCategories();
        } else {
            throw new Error('タスクの更新に失敗しました');
        }
    } catch (error) {
        console.error('Error updating task:', error);
        showNotification('タスクの更新に失敗しました', 'error');
    }
}

// タスク一覧の読み込み
async function loadTasks() {
    try {
        let url = API_TASKS;
        const params = new URLSearchParams();
        
        if (currentFilters.search) {
            params.append('search', currentFilters.search);
        } else {
            if (currentFilters.status !== 'all') {
                params.append('status', currentFilters.status);
            }
            if (currentFilters.priority !== 'all') {
                params.append('priority', currentFilters.priority);
            }
            if (currentFilters.category !== 'all') {
                params.append('category', currentFilters.category);
            }
        }
        
        if (params.toString()) {
            url += '?' + params.toString();
        }
        
        const response = await fetch(url);
        if (response.ok) {
            tasks = await response.json();
            renderTasks();
        } else {
            throw new Error('タスクの読み込みに失敗しました');
        }
    } catch (error) {
        console.error('Error loading tasks:', error);
        showNotification('タスクの読み込みに失敗しました', 'error');
    }
}

// 統計情報の読み込み
async function loadStatistics() {
    try {
        const response = await fetch(API_STATS);
        if (response.ok) {
            const stats = await response.json();
            updateStatistics(stats);
        } else {
            throw new Error('統計情報の読み込みに失敗しました');
        }
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

// カテゴリ一覧の読み込み
async function loadCategories() {
    try {
        const response = await fetch(API_CATEGORIES);
        if (response.ok) {
            const categories = await response.json();
            updateCategoryFilter(categories);
        } else {
            throw new Error('カテゴリの読み込みに失敗しました');
        }
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

// タスクの表示
function renderTasks() {
    tasksListElement.innerHTML = '';
    
    if (tasks.length === 0) {
        noTasksElement.style.display = 'block';
        return;
    }
    
    noTasksElement.style.display = 'none';
    
    tasks.forEach(task => {
        const taskElement = createTaskElement(task);
        tasksListElement.appendChild(taskElement);
    });
}

// タスク要素の作成
function createTaskElement(task) {
    const taskCard = document.createElement('div');
    taskCard.className = `task-card ${task.completed ? 'completed' : ''} ${task.isOverdue ? 'overdue' : ''} priority-${task.priority.toLowerCase()}`;
    
    // 日時フォーマット
    const formatDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleString('ja-JP', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    };
    
    // タグの処理
    const tags = task.tags ? task.tags.split(',').map(tag => tag.trim()).filter(tag => tag) : [];
    
    taskCard.innerHTML = `
        <div class="task-header">
            <div class="task-title">${escapeHtml(task.title)}</div>
            <div class="task-actions">
                <button class="btn btn-secondary" onclick="toggleTaskCompletion(${task.id})">
                    <i class="fas fa-${task.completed ? 'undo' : 'check'}"></i>
                    ${task.completed ? '未完了' : '完了'}
                </button>
                <button class="btn btn-secondary" onclick="editTask(${task.id})">
                    <i class="fas fa-edit"></i> 編集
                </button>
                <button class="btn btn-danger" onclick="deleteTask(${task.id})">
                    <i class="fas fa-trash"></i> 削除
                </button>
            </div>
        </div>
        <div class="task-meta">
            <span class="priority-badge priority-${task.priority.toLowerCase()}">
                <i class="fas fa-flag"></i> ${task.priorityDisplay}
            </span>
            <span class="status-badge status-${task.completed ? 'completed' : (task.isOverdue ? 'overdue' : 'incomplete')}">
                <i class="fas fa-${task.completed ? 'check-circle' : (task.isOverdue ? 'exclamation-triangle' : 'clock')}"></i>
                ${task.completed ? '完了済み' : (task.isOverdue ? '期限切れ' : '未完了')}
            </span>
            <span>
                <i class="fas fa-folder"></i> ${escapeHtml(task.category)}
            </span>
            ${task.dueDate ? `
                <span${task.isOverdue ? ' style="color: #ff6b6b; font-weight: bold;"' : ''}>
                    <i class="fas fa-calendar"></i> ${formatDate(task.dueDate)}
                </span>
            ` : ''}
        </div>
        ${task.description ? `
            <div class="task-description">${escapeHtml(task.description)}</div>
        ` : ''}
        ${tags.length > 0 ? `
            <div class="task-tags">
                ${tags.map(tag => `<span class="tag">${escapeHtml(tag)}</span>`).join('')}
            </div>
        ` : ''}
    `;
    
    taskCard.classList.add('fade-in');
    return taskCard;
}

// タスクの完了状態切り替え
async function toggleTaskCompletion(taskId) {
    try {
        const response = await fetch(`${API_TASKS}/${taskId}/toggle`, {
            method: 'PATCH'
        });
        
        if (response.ok) {
            const updatedTask = await response.json();
            showNotification(`タスクを${updatedTask.completed ? '完了' : '未完了'}にしました`, 'success');
            await loadTasks();
            await loadStatistics();
        } else {
            throw new Error('タスクの更新に失敗しました');
        }
    } catch (error) {
        console.error('Error toggling task completion:', error);
        showNotification('タスクの更新に失敗しました', 'error');
    }
}

// タスク編集
function editTask(taskId) {
    const task = tasks.find(t => t.id === taskId);
    if (!task) return;
    
    // フォームに値を設定
    document.getElementById('edit-task-id').value = task.id;
    document.getElementById('edit-task-title').value = task.title;
    document.getElementById('edit-task-description').value = task.description || '';
    document.getElementById('edit-task-priority').value = task.priority;
    document.getElementById('edit-task-category').value = task.category;
    document.getElementById('edit-task-tags').value = task.tags || '';
    document.getElementById('edit-task-completed').checked = task.completed;
    
    // 日時の設定
    if (task.dueDate) {
        const date = new Date(task.dueDate);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        document.getElementById('edit-task-due-date').value = `${year}-${month}-${day}T${hours}:${minutes}`;
    } else {
        document.getElementById('edit-task-due-date').value = '';
    }
    
    // モーダルを表示
    editModal.style.display = 'block';
}

// タスク削除
async function deleteTask(taskId) {
    if (!confirm('このタスクを削除しますか？')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_TASKS}/${taskId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showNotification('タスクが削除されました', 'success');
            await loadTasks();
            await loadStatistics();
            await loadCategories();
        } else {
            throw new Error('タスクの削除に失敗しました');
        }
    } catch (error) {
        console.error('Error deleting task:', error);
        showNotification('タスクの削除に失敗しました', 'error');
    }
}

// 検索処理
function handleSearch() {
    currentFilters.search = searchInput.value.trim();
    loadTasks();
}

// フィルター変更処理
function handleFilterChange() {
    currentFilters.status = statusFilter.value;
    currentFilters.priority = priorityFilter.value;
    currentFilters.category = categoryFilter.value;
    currentFilters.search = ''; // フィルター使用時は検索をクリア
    searchInput.value = '';
    loadTasks();
}

// フィルタークリア
function clearFilters() {
    currentFilters = {
        search: '',
        status: 'all',
        priority: 'all',
        category: 'all'
    };
    
    searchInput.value = '';
    statusFilter.value = 'all';
    priorityFilter.value = 'all';
    categoryFilter.value = 'all';
    
    loadTasks();
}

// 統計情報の更新
function updateStatistics(stats) {
    document.getElementById('total-tasks').textContent = stats.totalTasks;
    document.getElementById('completed-tasks').textContent = stats.completedTasks;
    document.getElementById('incomplete-tasks').textContent = stats.incompleteTasks;
    document.getElementById('overdue-tasks').textContent = stats.overdueTasks;
}

// カテゴリフィルターの更新
function updateCategoryFilter(categories) {
    // 既存のオプション（"すべてのカテゴリ"以外）を削除
    const options = categoryFilter.querySelectorAll('option:not([value="all"])');
    options.forEach(option => option.remove());
    
    // 新しいカテゴリオプションを追加
    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category;
        option.textContent = category;
        categoryFilter.appendChild(option);
    });
}

// モーダルを閉じる
function closeModal() {
    editModal.style.display = 'none';
    editForm.reset();
}

// 通知表示
function showNotification(message, type = 'info') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification ${type}`;
    notification.classList.add('show');
    
    setTimeout(() => {
        notification.classList.remove('show');
    }, 3000);
}

// HTMLエスケープ関数
function escapeHtml(text) {
    if (!text) return '';
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

// グローバル関数として公開（HTML のonclick属性で使用）
window.toggleTaskCompletion = toggleTaskCompletion;
window.editTask = editTask;
window.deleteTask = deleteTask; 

// 期限日のデフォルト値を設定（23:59）
function setDefaultDueDateTime() {
    const dueDateInput = document.getElementById('task-due-date');
    const editDueDateInput = document.getElementById('edit-task-due-date');
    
    if (dueDateInput && !dueDateInput.value) {
        const now = new Date();
        // 現在の日付で23:59を設定（時差を考慮）
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        dueDateInput.value = `${year}-${month}-${day}T23:59`;
    }
} 