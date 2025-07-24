package com.example.prac1.service;

import com.example.prac1.dto.creation.TaskCreationDto;
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

    public void deleteTask(Long id) throws AccessDeniedException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Task> taskOptional = taskRepository.findById(id);

        if (taskOptional.isPresent()) {
            Task taskToDelete = taskOptional.get();

            String taskOwnerUsername = taskToDelete.getUser().getName();

            if (!taskOwnerUsername.equals(username)) {
                throw new AccessDeniedException("You are not authorized to delete this task.");
            }
            taskRepository.deleteById(id);
        } else {
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
