package com.example.prac1.service;
import com.example.prac1.model.Role;
import com.example.prac1.repo.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }
    public List<Role> getAllRoles(){
        return roleRepository.findAll();
    }

    public Optional<Role> findByName(String name){
        return roleRepository.findByName(name);
    }
    public Role createRole(String roleName){
        if (roleRepository.findByName(roleName).isPresent()) {
            throw new RuntimeException("Role already exists " + roleName);
        }

        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }
}
