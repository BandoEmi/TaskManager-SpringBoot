package com.taskmanager.service;

import com.taskmanager.entity.Task;
import com.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * タスク管理サービスクラス
 * ビジネスロジックとデータアクセスの仲介
 */
@Service
@Transactional
public class TaskService {
    
    private final TaskRepository taskRepository;
    
    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    /**
     * すべてのタスクを取得
     */
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    /**
     * IDでタスクを取得
     */
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    /**
     * 新しいタスクを作成
     */
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }
    
    /**
     * タスクを更新
     */
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }
    
    /**
     * タスクを削除
     */
    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * タスクの完了状態を切り替え
     */
    public Optional<Task> toggleTaskCompletion(Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setCompleted(!task.isCompleted());
            return Optional.of(taskRepository.save(task));
        }
        return Optional.empty();
    }
    
    /**
     * 完了状態でタスクを検索
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByCompleted(boolean completed) {
        return taskRepository.findByCompleted(completed);
    }
    
    /**
     * 優先度でタスクを検索
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority);
    }
    
    /**
     * カテゴリでタスクを検索
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByCategory(String category) {
        return taskRepository.findByCategory(category);
    }
    
    /**
     * キーワードでタスクを検索
     */
    @Transactional(readOnly = true)
    public List<Task> searchTasks(String keyword) {
        return taskRepository.findByKeyword(keyword);
    }
    
    /**
     * フィルター条件でタスクを検索
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByFilters(String status, String priority, String category) {
        // 期限切れフィルターの場合は専用メソッドを使用
        if ("overdue".equals(status)) {
            return getOverdueTasks();
        }
        
        Boolean completed = null;
        if ("completed".equals(status)) {
            completed = true;
        } else if ("incomplete".equals(status)) {
            completed = false;
        }
        
        Task.Priority priorityEnum = null;
        if (priority != null && !"all".equals(priority)) {
            try {
                priorityEnum = Task.Priority.valueOf(priority.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 無効な優先度は無視
            }
        }
        
        String categoryFilter = ("all".equals(category)) ? null : category;
        
        return taskRepository.findByFilters(completed, priorityEnum, categoryFilter);
    }
    
    /**
     * 期限切れタスクを取得
     */
    @Transactional(readOnly = true)
    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDateTime.now());
    }
    
    /**
     * 今日が期限のタスクを取得
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksDueToday() {
        return taskRepository.findTasksDueToday(LocalDateTime.now());
    }
    
    /**
     * すべてのカテゴリを取得
     */
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return taskRepository.findAllCategories();
    }
    
    /**
     * タスク統計情報を取得
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTaskStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 基本統計
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.findByCompleted(true).size();
        long incompleteTasks = taskRepository.findByCompleted(false).size();
        long overdueTasks = getOverdueTasks().size();
        
        stats.put("totalTasks", totalTasks);
        stats.put("completedTasks", completedTasks);
        stats.put("incompleteTasks", incompleteTasks);
        stats.put("overdueTasks", overdueTasks);
        
        // 完了率
        double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        stats.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
        
        // カテゴリ別統計
        Map<String, Long> categoryStats = new HashMap<>();
        List<Object[]> categoryResults = taskRepository.getTaskCountByCategory();
        for (Object[] result : categoryResults) {
            categoryStats.put((String) result[0], (Long) result[1]);
        }
        stats.put("categoryStats", categoryStats);
        
        // 優先度別統計
        Map<String, Long> priorityStats = new HashMap<>();
        List<Object[]> priorityResults = taskRepository.getTaskCountByPriority();
        for (Object[] result : priorityResults) {
            priorityStats.put(result[0].toString(), (Long) result[1]);
        }
        stats.put("priorityStats", priorityStats);
        
        return stats;
    }
    
    /**
     * タスクが存在するかチェック
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return taskRepository.existsById(id);
    }
} 