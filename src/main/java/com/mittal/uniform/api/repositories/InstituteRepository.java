package com.mittal.uniform.api.repositories;

import com.mittal.uniform.api.models.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstituteRepository extends JpaRepository<Institute, Long> {
}
