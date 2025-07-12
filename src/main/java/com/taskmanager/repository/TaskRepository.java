package com.taskmanager.repository;

import com.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * タスクリポジトリインターフェース
 * Spring Data JPAを使用したデータアクセス層
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // 完了状態で検索
    List<Task> findByCompleted(boolean completed);
    
    // 優先度で検索
    List<Task> findByPriority(Task.Priority priority);
    
    // カテゴリで検索
    List<Task> findByCategory(String category);
    
    // タイトルもしくは説明でキーワード検索（大文字小文字区別なし）
    @Query("SELECT t FROM Task t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> findByKeyword(@Param("keyword") String keyword);
    
    // 期限切れタスクを検索
    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.completed = false")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);
    
    // 今日が期限のタスクを検索
    @Query("SELECT t FROM Task t WHERE DATE(t.dueDate) = DATE(:today) AND t.completed = false")
    List<Task> findTasksDueToday(@Param("today") LocalDateTime today);
    
    // カテゴリ別の統計情報
    @Query("SELECT t.category, COUNT(t) FROM Task t GROUP BY t.category")
    List<Object[]> getTaskCountByCategory();
    
    // 優先度別の統計情報
    @Query("SELECT t.priority, COUNT(t) FROM Task t GROUP BY t.priority")
    List<Object[]> getTaskCountByPriority();
    
    // 完了状態別の統計情報
    @Query("SELECT t.completed, COUNT(t) FROM Task t GROUP BY t.completed")
    List<Object[]> getTaskCountByStatus();
    
    // すべてのカテゴリを取得
    @Query("SELECT DISTINCT t.category FROM Task t WHERE t.category IS NOT NULL ORDER BY t.category")
    List<String> findAllCategories();
    
    // 複合検索（フィルター機能用）
    @Query("SELECT t FROM Task t WHERE " +
           "(:completed IS NULL OR t.completed = :completed) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:category IS NULL OR t.category = :category) " +
           "ORDER BY t.priority DESC, t.createdAt DESC")
    List<Task> findByFilters(@Param("completed") Boolean completed,
                           @Param("priority") Task.Priority priority,
                           @Param("category") String category);
} 