package org.testprojects.task_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testprojects.task_service.exception.TaskNotFoundException;
import org.testprojects.task_service.model.Task;
import org.testprojects.task_service.repository.TaskDBRepository;
import org.testprojects.task_service.util.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TaskCrudServiceTest {

    @Mock
    private TaskDBRepository taskDBRepository;

    @InjectMocks
    private TaskCrudService taskCrudService;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask() {
        Task task = new Task(1L, "Test Task", "Description", TaskStatus.ACTIVE);
        when(taskDBRepository.save(task)).thenReturn(task);

        String email = emailService.sendEmail("Xrazzik@gmail.com", "Hello!", "Hello");

        taskCrudService.create(task, email);

        verify(taskDBRepository, times(1)).save(task);
    }

    @Test
    void testFindAllTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1L, "Task 1", "Description 1", TaskStatus.ACTIVE));
        tasks.add(new Task(2L, "Task 2", "Description 2", TaskStatus.COMPLETED));
        when(taskDBRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskCrudService.findAll();

        assertEquals(2, result.size());
        verify(taskDBRepository, times(1)).findAll();
    }

    @Test
    void testFindTaskById_NotFound() {
        when(taskDBRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskCrudService.findById(1L));

        verify(taskDBRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateTask_Success() {
        Task existingTask = new Task(1L, "Old Task", "Old Description", TaskStatus.ACTIVE);
        Task updatedTask = new Task(1L, "Updated Task", "Updated Description", TaskStatus.COMPLETED);
        when(taskDBRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskDBRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = taskCrudService.update(1L, updatedTask);

        assertEquals("Updated Task", result.getName());
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
        verify(taskDBRepository, times(1)).findById(1L);
        verify(taskDBRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTask_NotFound() {
        Task updatedTask = new Task(1L, "Updated Task", "Updated Description", TaskStatus.COMPLETED);
        when(taskDBRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskCrudService.update(1L, updatedTask));

        verify(taskDBRepository, times(1)).findById(1L);
        verify(taskDBRepository, never()).save(any(Task.class));
    }

    @Test
    void testDeleteTask_Success() {
        when(taskDBRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskDBRepository).deleteById(1L);

        taskCrudService.delete(1L);

        verify(taskDBRepository, times(1)).existsById(1L);
        verify(taskDBRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTask_NotFound() {
        when(taskDBRepository.existsById(1L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskCrudService.delete(1L));

        verify(taskDBRepository, times(1)).existsById(1L);
        verify(taskDBRepository, never()).deleteById(anyLong());
    }

}
