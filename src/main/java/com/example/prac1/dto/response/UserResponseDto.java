package com.example.prac1.dto.response;

import com.example.prac1.model.Role;

import java.util.List;
import java.util.Set;

public class UserResponseDto {
    private Long id;
    private String name;
    private Set<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
