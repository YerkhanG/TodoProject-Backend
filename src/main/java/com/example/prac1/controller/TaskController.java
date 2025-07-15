package com.example.prac1.controller;

import com.example.prac1.dto.TaskCreationDto;
import com.example.prac1.model.Task;
import com.example.prac1.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    @Operation(summary = "Adds a Task")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Successfully created a task.",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Task.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request.",
                    content = @Content)
    })
    @PostMapping("/tasks")
    public ResponseEntity<Task> addTask(@Valid @RequestBody TaskCreationDto request){
        try{
            Task task = taskService.createTask(request);
            return ResponseEntity.ok(task);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(null);
        }
    }
    @Operation(summary = "Get all existing Tasks")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all tasks. Can be an empty array if no Tasks are created.",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Task.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request.",
                    content = @Content)
    })
    @GetMapping("/tasks")
    public ResponseEntity<Optional<List<Task>>> getAllTasks(){
        return ResponseEntity.ok(taskService.getAllTasks());
    }


    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<String> deleteTaskById(@PathVariable Long id) {
        try {

            taskService.deleteTask(id);

            return ResponseEntity.noContent().build();
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedControllerException(AccessDeniedException ex) {
        return new ResponseEntity<>("Forbidden by Controller: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }
    public class TaskNotFoundException extends RuntimeException {
        public TaskNotFoundException(String message) {
            super(message);
        }
    }

    public class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }



}
