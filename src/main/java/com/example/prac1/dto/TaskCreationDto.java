package com.example.prac1.dto;

import jakarta.validation.constraints.NotBlank;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

public class TaskCreationDto {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    private String description;
    private String status;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
