package com.example.todoappjavafx.controller;

import com.example.todoappjavafx.model.Task;
import com.example.todoappjavafx.repository.JsonTaskRepository;
import com.example.todoappjavafx.service.TaskService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

public class MainController {

    @FXML private ListView<Task> taskListView;
    @FXML private Button addTaskBtn;

    private final TaskService taskService = new TaskService(new JsonTaskRepository());
    private final ObservableList<Task> taskList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Load tasks
        taskList.setAll(taskService.getAllTasks());
        taskListView.setItems(taskList);

        // Set custom cell factory for better UI
        taskListView.setCellFactory(createTaskCellFactory());

        // Button Action
        addTaskBtn.setOnAction(e -> openTaskForm(null));
    }

    private Callback<ListView<Task>, ListCell<Task>> createTaskCellFactory() {
        return listView -> {
            ListCell<Task> cell = new ListCell<>() {
                @Override
                protected void updateItem(Task task, boolean empty) {
                    super.updateItem(task, empty);
                    if (empty || task == null) {
                        setText(null);
                        setContextMenu(null);
                    } else {
                        setText(formatTaskText(task));
                        setContextMenu(createContextMenu(task));
                        getStyleClass().remove("completed-task");
                        if (task.isCompleted()) getStyleClass().add("completed-task");
                    }
                }
            };

            // Double-click toggles completion
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Task t = cell.getItem();
                    taskService.toggleTaskCompletion(t.getId());
                    refreshList();
                }
            });

            return cell;
        };
    }

    private String formatTaskText(Task task) {
        return String.format("[%s] %s — %s (Due: %s)",
                task.getPriority().toString(),
                task.getTitle(),
                task.isCompleted() ? "✅ Done" : "⏳ Pending",
                task.getDueDate());
    }

    private ContextMenu createContextMenu(Task task) {
        MenuItem editItem = new MenuItem("✏️ Edit");
        MenuItem deleteItem = new MenuItem("🗑️ Delete");

        editItem.setOnAction(e -> openTaskForm(task));
        deleteItem.setOnAction(e -> {
            taskService.deleteTask(task.getId());
            refreshList();
        });

        return new ContextMenu(editItem, deleteItem);
    }

    private void openTaskForm(Task task) {
        // TODO: show TaskForm (we’ll do this in next step)
        System.out.println(task == null ? "Add new task" : "Edit task: " + task.getTitle());
    }

    private void refreshList() {
        taskList.setAll(taskService.getAllTasks());
    }
}
