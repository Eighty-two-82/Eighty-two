package com.careapp.controller;

import com.careapp.domain.Task;
import com.careapp.domain.RecurringTask;
import com.careapp.dto.CreateRecurringTaskRequest;
import com.careapp.service.TaskService;
import com.careapp.service.RecurringTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private RecurringTaskService recurringTaskService;

    private Task mockTask;
    private RecurringTask mockRecurringTask;

    @BeforeEach
    void setUp() {
        mockTask = new Task();
        mockTask.setId("task001");
        mockTask.setTitle("Check medication");
        mockTask.setStatus("In Progress");
        mockTask.setPatientId("p1");
        mockTask.setAssignedToId("w1");

        mockRecurringTask = new RecurringTask();
        mockRecurringTask.setId("rec001");
        mockRecurringTask.setTitle("Weekly Health Check");
        mockRecurringTask.setIsActive(true);
    }

    // ========== Create Task ==========
    @Test
    void testCreateTaskSuccess() throws Exception {
        Mockito.when(taskService.createTask(Mockito.any(Task.class))).thenReturn(mockTask);

        String body = "{"
                + "\"title\":\"Check medication\","
                + "\"status\":\"In Progress\""
                + "}";

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Task created successfully!"))
                .andExpect(jsonPath("$.data.id").value("task001"));
    }

    @Test
    void testCreateTaskFailure() throws Exception {
        Mockito.when(taskService.createTask(Mockito.any(Task.class)))
                .thenThrow(new RuntimeException("DB error"));

        String body = "{"
                + "\"title\":\"Check medication\""
                + "}";

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("Failed to create task: DB error"));
    }

    // ========== Get All Tasks ==========
    @Test
    void testGetAllTasksSuccess() throws Exception {
        Mockito.when(taskService.getAllTasks()).thenReturn(Arrays.asList(mockTask));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("task001"))
                .andExpect(jsonPath("$.msg").value("Tasks retrieved successfully!"));
    }

    // ========== Get Task By ID ==========
    @Test
    void testGetTaskByIdFound() throws Exception {
        Mockito.when(taskService.getTaskById("task001"))
                .thenReturn(Optional.of(mockTask));

        mockMvc.perform(get("/api/tasks/task001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Task retrieved successfully!"))
                .andExpect(jsonPath("$.data.id").value("task001"));
    }

    @Test
    void testGetTaskByIdNotFound() throws Exception {
        Mockito.when(taskService.getTaskById("notExist"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/notExist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Task not found!"));
    }

    // ========== Delete Task ==========
    @Test
    void testDeleteTaskSuccess() throws Exception {
        Mockito.when(taskService.deleteTask("task001")).thenReturn(true);

        mockMvc.perform(delete("/api/tasks/task001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Task deleted successfully!"));
    }

    @Test
    void testDeleteTaskNotFound() throws Exception {
        Mockito.when(taskService.deleteTask("task001")).thenReturn(false);

        mockMvc.perform(delete("/api/tasks/task001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Task not found!"));
    }

    // ========== Update Task ==========
    @Test
    void testUpdateTaskSuccess() throws Exception {
        Mockito.when(taskService.getTaskById("task001"))
                .thenReturn(Optional.of(mockTask));
        Mockito.when(taskService.updateTask(Mockito.any(Task.class)))
                .thenReturn(mockTask);

        String body = "{"
                + "\"title\":\"Updated Task\""
                + "}";

        mockMvc.perform(put("/api/tasks/task001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Task updated successfully!"));
    }

    @Test
    void testUpdateTaskNotFound() throws Exception {
        Mockito.when(taskService.getTaskById("notExist"))
                .thenReturn(Optional.empty());

        String body = "{"
                + "\"title\":\"Updated Task\""
                + "}";

        mockMvc.perform(put("/api/tasks/notExist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Task not found!"));
    }

    // ========== Worker Complete Task ==========
    @Test
    void testWorkerCompleteTaskSuccess() throws Exception {
        Mockito.when(taskService.workerCompleteTask("task001"))
                .thenReturn(mockTask);

        mockMvc.perform(post("/api/tasks/task001/worker-complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Task marked as completed by worker!"));
    }

    @Test
    void testWorkerCompleteTaskNotFound() throws Exception {
        Mockito.when(taskService.workerCompleteTask("notExist"))
                .thenReturn(null);

        mockMvc.perform(post("/api/tasks/notExist/worker-complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Task not found!"));
    }

    // ========== Approve / Reject ==========
    @Test
    void testApproveTaskCompletionSuccess() throws Exception {
        Mockito.when(taskService.approveTaskCompletion(Mockito.eq("task001"), Mockito.anyString()))
                .thenReturn(mockTask);

        String body = "{"
                + "\"approvalReason\":\"Looks good\""
                + "}";

        mockMvc.perform(post("/api/tasks/task001/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Task completion approved!"));
    }

    @Test
    void testRejectTaskCompletionNotFound() throws Exception {
        Mockito.when(taskService.rejectTaskCompletion(Mockito.eq("task999"), Mockito.anyString()))
                .thenReturn(null);

        String body = "{"
                + "\"rejectionReason\":\"Incomplete\""
                + "}";

        mockMvc.perform(post("/api/tasks/task999/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Task not found!"));
    }

    // ========== Recurring Task ==========
    @Test
    void testCreateRecurringTaskSuccess() throws Exception {
        Mockito.when(recurringTaskService.createRecurringTask(Mockito.any(CreateRecurringTaskRequest.class)))
                .thenReturn(mockRecurringTask);

        String body = "{"
                + "\"title\":\"Weekly Health Check\","
                + "\"frequency\":\"WEEKLY\""
                + "}";

        mockMvc.perform(post("/api/tasks/recurring")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Recurring task template created successfully!"));
    }

    @Test
    void testToggleRecurringTaskStatusSuccess() throws Exception {
        mockRecurringTask.setIsActive(false);
        Mockito.when(recurringTaskService.toggleRecurringTaskStatus("rec001"))
                .thenReturn(mockRecurringTask);

        mockMvc.perform(post("/api/tasks/recurring/rec001/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Recurring task template deactivated successfully!"));
    }

    @Test
    void testToggleRecurringTaskNotFound() throws Exception {
        Mockito.when(recurringTaskService.toggleRecurringTaskStatus("notExist"))
                .thenReturn(null);

        mockMvc.perform(post("/api/tasks/recurring/notExist/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Recurring task template not found!"));
    }

    // ========== Task Stats ==========
    @Test
    void testGetTaskStatsSuccess() throws Exception {
        Mockito.when(taskService.getAllTasks()).thenReturn(Collections.singletonList(mockTask));
        Mockito.when(taskService.getTaskCountByStatus(Mockito.anyString())).thenReturn(1L);
        Mockito.when(taskService.getTaskCountByDueDate(Mockito.any(LocalDate.class))).thenReturn(1L);

        mockMvc.perform(get("/api/tasks/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Task statistics retrieved successfully!"))
                .andExpect(jsonPath("$.data.total").value(1));
    }
}

