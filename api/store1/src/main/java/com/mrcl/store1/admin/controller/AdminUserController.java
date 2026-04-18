package com.mrcl.store1.admin.controller;



import com.mrcl.store1.admin.dto.AdminUserRow;
import com.mrcl.store1.auth.dao.AppUserRepository;
import com.mrcl.store1.auth.entity.AppUser;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AppUserRepository userRepo;

    public AdminUserController(AppUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping
    public List<AdminUserRow> listUsers() {
        return userRepo.findAll()
                .stream()
                .map(u -> new AdminUserRow(u.getId(), u.getEmail(), u.getRoles(), u.isEnabled(), u.getBlockedAt()))
                .toList();
    }

    @PatchMapping("/{id}/block")
    public void block(@PathVariable Long id) {
        AppUser u = userRepo.findById(id).orElseThrow();
        u.setEnabled(false);
        u.setBlockedAt(Instant.now());
        userRepo.save(u);
    }

    @PatchMapping("/{id}/unblock")
    public void unblock(@PathVariable Long id) {
        AppUser u = userRepo.findById(id).orElseThrow();
        u.setEnabled(true);
        u.setBlockedAt(null);
        userRepo.save(u);
    }
}