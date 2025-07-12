package com.taskmanager.dto;

import com.taskmanager.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * タスク作成・更新要求用のDTO
 */
public class TaskRequest {
    
    @NotBlank(message = "タイトルは必須です")
    @Size(max = 200, message = "タイトルは200文字以内で入力してください")
    private String title;
    
    @Size(max = 1000, message = "説明は1000文字以内で入力してください")
    private String description;
    
    private Task.Priority priority = Task.Priority.MEDIUM;
    
    @Size(max = 100, message = "カテゴリは100文字以内で入力してください")
    private String category = "一般";
    
    @Size(max = 500, message = "タグは500文字以内で入力してください")
    private String tags;
    
    private LocalDateTime dueDate;
    
    private Boolean completed = false;
    
    // デフォルトコンストラクタ
    public TaskRequest() {}
    
    // Getter / Setter メソッド
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Task.Priority getPriority() { return priority; }
    public void setPriority(Task.Priority priority) { this.priority = priority; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
    
    /**
     * DTOからEntityに変換
     */
    public Task toEntity() {
        Task task = new Task();
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setPriority(this.priority);
        task.setCategory(this.category);
        task.setTags(this.tags);
        task.setDueDate(this.dueDate);
        if (this.completed != null) {
            task.setCompleted(this.completed);
        }
        return task;
    }
    
    /**
     * 既存のEntityを更新
     */
    public void updateEntity(Task task) {
        if (this.title != null) {
            task.setTitle(this.title);
        }
        if (this.description != null) {
            task.setDescription(this.description);
        }
        if (this.priority != null) {
            task.setPriority(this.priority);
        }
        if (this.category != null) {
            task.setCategory(this.category);
        }
        if (this.tags != null) {
            task.setTags(this.tags);
        }
        if (this.dueDate != null) {
            task.setDueDate(this.dueDate);
        }
        if (this.completed != null) {
            task.setCompleted(this.completed);
        }
    }
    
    @Override
    public String toString() {
        return "TaskRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", category='" + category + '\'' +
                ", tags='" + tags + '\'' +
                ", dueDate=" + dueDate +
                ", completed=" + completed +
                '}';
    }
} 