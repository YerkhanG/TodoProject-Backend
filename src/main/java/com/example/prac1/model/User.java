package com.example.prac1.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name= "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();
    public void setName(String name){
        this.name = name;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public void addRole(Role role){
        this.roles.add(role);
        role.getUsers().add(this);
    }
    public void addTask(Task task) {
        tasks.add(task);
        task.setUser(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setUser(null);
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void removeRole(Role role){
        this.roles.remove(role);
        role.getUsers().remove(this);
    }
    public Set<Role> getRoles() {
        return roles;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }
}
