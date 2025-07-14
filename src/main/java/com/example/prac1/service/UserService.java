package com.example.prac1.service;

import com.example.prac1.dto.UserRegistrationDto;
import com.example.prac1.model.Role;
import com.example.prac1.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.prac1.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService){
        this.userRepo = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }
    public User registerNewUser(UserRegistrationDto userDto) {
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Check if user already exists
        if (userRepo.findByName(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Create and save user
        User user = new User();
        user.setName(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Optional<Role> defaultRole = roleService.findByName("USER");
        defaultRole.ifPresent(user::addRole);

        return userRepo.save(user);
    }
    public void deleteUserById(Long id){
        userRepo.deleteById(id);
    }

    public User assignRoleToUser(Long userId, String roleName){
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleService.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        user.addRole(role);
        return userRepo.save(user);
    }
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
    public Set<Role> getUserRoles(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRoles();
    }

}
