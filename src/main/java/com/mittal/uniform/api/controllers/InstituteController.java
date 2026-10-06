package com.mittal.uniform.api.controllers;

import com.mittal.uniform.api.models.Institute;
import com.mittal.uniform.api.services.InstituteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institutes")
public class InstituteController {

    private final InstituteService instituteService;

    public InstituteController(InstituteService instituteService) {
        this.instituteService = instituteService;
    }

    // PUBLIC: Anyone can view the list of supported schools/corporates
    @GetMapping
    public ResponseEntity<List<Institute>> getAllInstitutes() {
        return ResponseEntity.ok(instituteService.getAllInstitutes());
    }

    // SECURED: Only administrators can register new institutions
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Institute> createInstitute(@RequestBody Institute institute) {
        Institute savedInstitute = instituteService.createInstitute(institute);
        return ResponseEntity.ok(savedInstitute);
    }

    @DeleteMapping("/instituteId")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteInstitute(@PathVariable Long instituteId) {
        instituteService.deleteInstitute(instituteId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}