package com.example.prac1.controller;

import com.example.prac1.dto.UserRegistrationDto;
import com.example.prac1.dto.response.UserResponseDto;
import com.example.prac1.model.Role;
import com.example.prac1.model.User;
import com.example.prac1.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    @Autowired
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }
    @Operation(summary = "Registers a User")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "201", description = "Successfully registered a User.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request. Username already exists.",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto userDto) {
        try {
            User newUser = userService.registerNewUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDto(newUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @Operation(summary = "Assigns a Role to a User")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Successfully assigned a role to a User.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request: User not found or invalid user ID, or Role not found.",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not have sufficient permissions.",
                    content = @Content)
    })
    @PostMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<UserResponseDto> assignRoleToUser(@PathVariable Long userId , @PathVariable String roleName){
        try{
            User updatedUser = userService.assignRoleToUser(userId, roleName);
            return ResponseEntity.ok(convertToResponseDto(updatedUser));
        }catch(RuntimeException e ){
            return ResponseEntity.badRequest().build();
        }
    }
    @Operation(summary = "Get User's Roles")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of user's roles. Can be an empty array if no roles are assigned.",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Role.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request: User not found or invalid user ID.",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not have sufficient permissions (e.g., not ADMIN or USER role).",
                    content = @Content)
    })
    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<Set<Role>> getUserRoles(@PathVariable Long userId) {
        try {
            Set<Role> roles = userService.getUserRoles(userId);
            return ResponseEntity.ok(roles != null ? roles : java.util.Collections.emptySet());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private UserResponseDto convertToResponseDto(User user){
        UserResponseDto dto = modelMapper.map(user , UserResponseDto.class);
        if(user.getRoles() != null){
            dto.setRoles(user.getRoles());
        }
        return dto;
    }


}
