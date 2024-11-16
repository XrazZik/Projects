package org.testprojects.task_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.testprojects.task_service.exception.TaskNotFoundException;
import org.testprojects.task_service.model.Task;
import org.testprojects.task_service.service.TaskCrudService;

import java.util.List;

@Slf4j
@RestController
public class TaskController {

    private final TaskCrudService taskCrudService;

    @Autowired
    public TaskController(TaskCrudService taskCrudService) {
        this.taskCrudService = taskCrudService;
    }

    @PostMapping(value = "/api/create_task")
    public ResponseEntity<?> createTask(@RequestBody Task task, @RequestParam String email) {
        try {
            log.info("Creating task: {} Creating email: {}", task, email);
            taskCrudService.create(task, email);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating task: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/api/task_list")
    @Cacheable(value = "taskListCache", unless = "#result == null")
    public ResponseEntity<?> getTaskList() {
        try {
            log.info("Retrieved all task list...");
            List<Task> allTasks = taskCrudService.findAll();
            return new ResponseEntity<>(allTasks, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting all task list: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/api/update_task/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        try {
            Task updatedTask = taskCrudService.update(id, taskDetails);
            log.info("Updated task: {}", id);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } catch (TaskNotFoundException e) {
            log.warn("Task not found with id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error updating task: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/api/delete/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            log.info("Deleting task with id: {}", id);
            taskCrudService.delete(id);
            log.info("Deleted task successfully: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TaskNotFoundException e) {
            log.warn("Task not found with id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error deleting task: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/api/find_for_id/{id}")
    public ResponseEntity<?> findTaskForId(@PathVariable Long id) {
        try {
            Task task = taskCrudService.findById(id);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskNotFoundException e) {
            log.warn("Task not found with id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error finding task for id: {}", id, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
