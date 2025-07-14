package com.example.prac1.service;

import com.example.prac1.dto.TaskCreationDto;
import com.example.prac1.model.Task;
import com.example.prac1.model.User;
import com.example.prac1.repo.TaskRepository;
import com.example.prac1.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Optional<List<Task>> getAllTasks(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = Optional.ofNullable(userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("user not found")));
        return taskRepository.findAllByUser(user);
    }

    public Optional<Task> getTaskById(Long id){
        return taskRepository.findById(id);
    }

    // *** THIS IS THE CRUCIAL PART THAT NEEDS TO BE MODIFIED ***
    public void deleteTask(Long id) throws AccessDeniedException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 1. Find the task you intend to delete
        Optional<Task> taskOptional = taskRepository.findById(id);

        if (taskOptional.isPresent()) {
            Task taskToDelete = taskOptional.get();

            // 2. Get the owner of the task
            String taskOwnerUsername = taskToDelete.getUser().getName();

            // 3. Compare the owner with the currently authenticated user
            if (!taskOwnerUsername.equals(username)) {
                // If they don't match, the current user is NOT authorized to delete this task
                throw new AccessDeniedException("You are not authorized to delete this task.");
            }

            // 4. If they match, proceed with deletion
            taskRepository.deleteById(id);
        } else {
            // If the task doesn't exist, it's generally considered a successful "no-op" delete,
            // or you could throw a NotFoundException and map it to 404 in GlobalExceptionHandler.
            // For your test expecting 403 (Forbidden), we primarily care about *existing* tasks
            // that the user doesn't own.
            // For now, if not found, it will proceed to return 204 (default for successful delete without content)
            // if your controller doesn't explicitly return 404 for not found.
        }
    }

    public Task createTask(TaskCreationDto request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setUser(user);

        return taskRepository.save(task);
    }
}
