package com.mittal.uniform.api.services;

import com.mittal.uniform.api.models.Institute;
import com.mittal.uniform.api.repositories.InstituteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InstituteService {

    private final InstituteRepository instituteRepository;

    // Standard constructor injection
    public InstituteService(InstituteRepository instituteRepository) {
        this.instituteRepository = instituteRepository;
    }

    @Transactional
    public Institute createInstitute(Institute institute) {
        return instituteRepository.save(institute);
    }

    @Transactional(readOnly = true)
    public List<Institute> getAllInstitutes() {
        return instituteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Institute getInstituteById(Long id) {
        return instituteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institute not found with id: " + id));
    }

    public void deleteInstitute(Long instituteId) {
        instituteRepository.deleteById(instituteId);
    }
}