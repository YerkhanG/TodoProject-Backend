package com.example.prac1.repo;

import com.example.prac1.model.Task;
import com.example.prac1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task ,Long> {
    Optional<List<Task>> findAllByUser(Optional<User> user);
}
