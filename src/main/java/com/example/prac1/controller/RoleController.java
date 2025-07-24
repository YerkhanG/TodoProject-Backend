package com.example.prac1.controller;

import com.example.prac1.dto.creation.RoleCreationDto;
import com.example.prac1.model.Role;
import com.example.prac1.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    @Operation(summary = "Creates a Role")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "201", description = "Successfully created a role.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Role.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request.",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not have sufficient permissions (e.g., not ADMIN role).",
                    content = @Content)
    })
    @PostMapping("/roles")
    public ResponseEntity<Role> createRole(@RequestBody RoleCreationDto request){
        try{
            Role newRole = roleService.createRole(request.getRoleName());
            return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(null);
        }
    }
    @Operation(summary = "Get all existing Roles")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all roles. Can be an empty array if no roles are created.",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Role.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request.",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not have sufficient permissions (e.g., not ADMIN role).",
                    content = @Content)
    })
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles(){
        return ResponseEntity.ok(roleService.getAllRoles());
    }

 }
