package org.testprojects.task_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.testprojects.task_service.exception.GeneralException;
import org.testprojects.task_service.exception.TaskNotFoundException;
import org.testprojects.task_service.model.Task;
import org.testprojects.task_service.repository.TaskDBRepository;

import java.util.List;

@Slf4j
@Service
public class TaskCrudService {

    private final TaskDBRepository taskDBRepository;
    private final  EmailService emailService;

    @Autowired
    public TaskCrudService(TaskDBRepository taskDBRepository, EmailService emailService) {
        this.taskDBRepository = taskDBRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void create(Task task, String email) {
        try {
            taskDBRepository.save(task);
            log.info("Task created successfully: {}", task);
            String subject = "Task created: " + task.getName();
            String body = String.format("Task Name: %s%nDescription: %s%nStatus: %s",
                    task.getName(), task.getDescription(), task.getStatus());
            emailService.sendEmail(email, subject, body);
        } catch (GeneralException e) {
            log.error("Task creation failed: {}", e.getMessage());
            throw new GeneralException("Error creating task: " + e.getMessage());
        }
    }

    public List<Task> findAll() {
        try {
            List<Task> tasks = taskDBRepository.findAll();
            log.info("Retrieved all tasks successfully: {}", tasks.size());
            return tasks;
        } catch (GeneralException e) {
            log.error("Task findAll failed: {}", e.getMessage());
            throw new GeneralException("Error finding tasks: " + e.getMessage());
        }
    }

    @Transactional
    public Task update(Long id, Task taskDetails) {
        try {
            log.info("Updating task with id: {} with details: {}", id, taskDetails);
            return taskDBRepository.findById(id)
                    .map(task -> {
                        task.setName(taskDetails.getName());
                        task.setDescription(taskDetails.getDescription());
                        task.setStatus(taskDetails.getStatus());
                        task.updateTimestamp();
                        log.info("Updated task: {}", task);
                        return taskDBRepository.save(task);
                    })
                    .orElseThrow(() -> {
                        log.warn("Task not found with id: {}", id);
                        return new TaskNotFoundException(id);
                    });
        } catch (TaskNotFoundException e) {
            log.warn("Error updating task: {}", e.getMessage(), e);
            throw e;
        } catch (GeneralException e) {
            log.error("Error updating task with id {}: {}", id, e.getMessage(), e);
            throw new GeneralException("Error updating task", e);
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            if (!taskDBRepository.existsById(id)) {
                log.warn("Task not found with id: {}", id);
                throw new TaskNotFoundException(id);
            }
            taskDBRepository.deleteById(id);
            log.info("Task deleted successfully: {}", id);
        } catch (TaskNotFoundException e) {
            log.warn("Error deleting task: {}", e.getMessage(), e);
            throw e;
        } catch (GeneralException e) {
            log.error("Error deleting task with id {}: {}", id, e.getMessage(), e);
        }
    }

    public Task findById(Long id) {
        try {
            log.info("Finding task with id : {}", id);
            return taskDBRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Task not found with id: {}", id);
                        return new TaskNotFoundException(id);
                    });
        } catch (GeneralException e) {
            log.error("Error finding task with id {}: {}", id, e.getMessage(), e);
            throw new GeneralException("Error finding task", e);
        }
    }
}
