package com.taskmanager.controller;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.entity.Task;
import com.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "タスク管理", description = "タスクのCRUD操作を行うAPI")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    @Operation(summary = "タスク一覧取得", description = "条件に応じてタスク一覧を取得します")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "正常にタスク一覧を取得")
    })
    public List<Task> getTasks(
            @Parameter(description = "検索キーワード") @RequestParam(required = false) String search,
            @Parameter(description = "完了状態フィルター") @RequestParam(required = false) String status,
            @Parameter(description = "優先度フィルター") @RequestParam(required = false) String priority,
            @Parameter(description = "カテゴリフィルター") @RequestParam(required = false) String category) {
        
        if (search != null && !search.trim().isEmpty()) {
            return taskService.searchTasks(search);
        } else {
            return taskService.getTasksByFilters(status, priority, category);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "タスク詳細取得", description = "指定されたIDのタスク詳細を取得します")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "正常にタスク詳細を取得"),
        @ApiResponse(responseCode = "404", description = "指定されたタスクが見つかりません")
    })
    public ResponseEntity<Task> getTask(
            @Parameter(description = "タスクID") @PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        if (task.isPresent()) {
            return ResponseEntity.ok(task.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "タスク作成", description = "新しいタスクを作成します")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "正常にタスクを作成"),
        @ApiResponse(responseCode = "400", description = "リクエストデータが不正です")
    })
    public Task createTask(
            @Parameter(description = "作成するタスクの情報") @Valid @RequestBody TaskRequest taskRequest) {
        return taskService.createTask(taskRequest.toEntity());
    }

    @PutMapping("/{id}")
    @Operation(summary = "タスク更新", description = "指定されたIDのタスクを更新します")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "正常にタスクを更新"),
        @ApiResponse(responseCode = "400", description = "リクエストデータが不正です"),
        @ApiResponse(responseCode = "404", description = "指定されたタスクが見つかりません")
    })
    public ResponseEntity<Task> updateTask(
            @Parameter(description = "更新するタスクのID") @PathVariable Long id,
            @Parameter(description = "更新するタスクの情報") @Valid @RequestBody TaskRequest taskRequest) {
        Optional<Task> existingTask = taskService.getTaskById(id);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            taskRequest.updateEntity(task);
            Task updatedTask = taskService.updateTask(task);
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "タスク完了状態切り替え", description = "指定されたIDのタスクの完了状態を切り替えます")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "正常に完了状態を切り替え"),
        @ApiResponse(responseCode = "404", description = "指定されたタスクが見つかりません")
    })
    public ResponseEntity<Task> toggleTaskCompletion(
            @Parameter(description = "切り替えるタスクのID") @PathVariable Long id) {
        Optional<Task> task = taskService.toggleTaskCompletion(id);
        if (task.isPresent()) {
            return ResponseEntity.ok(task.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "タスク削除", description = "指定されたIDのタスクを削除します")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "正常にタスクを削除"),
        @ApiResponse(responseCode = "404", description = "指定されたタスクが見つかりません")
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "削除するタスクのID") @PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

/**
 * 統計情報 API コントローラー
 */
@RestController
@RequestMapping("/api/stats")
@Tag(name = "統計情報", description = "タスクの統計情報を取得するAPI")
class StatsController {
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping
    @Operation(summary = "タスク統計情報取得", description = "全体的なタスク統計情報を取得します")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "正常に統計情報を取得")
    })
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = taskService.getTaskStatistics();
        return ResponseEntity.ok(stats);
    }
}

/**
 * カテゴリ API コントローラー
 */
@RestController
@RequestMapping("/api/categories")
@Tag(name = "カテゴリ管理", description = "タスクカテゴリを管理するAPI")
class CategoriesController {
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping
    @Operation(summary = "カテゴリ一覧取得", description = "すべてのタスクカテゴリを取得します")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "正常にカテゴリ一覧を取得")
    })
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = taskService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
} 