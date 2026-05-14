package com.project.websitebuilder.repository;

import com.project.websitebuilder.entity.GeneratedProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// JpaRepository<Entity, PrimaryKeyType> gives us:
// save(), findById(), findAll(), deleteById(), count() — for free
// We just need to add our own custom queries below
@Repository
public interface ProjectRepository extends JpaRepository<GeneratedProject, Long> {

    // Spring reads the method name and generates SQL automatically:
    // SELECT * FROM generated_projects ORDER BY created_at DESC
    List<GeneratedProject> findAllByOrderByCreatedAtDesc();

    // SELECT * FROM generated_projects WHERE status = ?
    List<GeneratedProject> findByStatus(String status);

    // SELECT * FROM generated_projects WHERE provider_used = ? ORDER BY created_at DESC
    List<GeneratedProject> findByProviderUsedOrderByCreatedAtDesc(String providerUsed);

    // SELECT COUNT(*) FROM generated_projects WHERE provider_used = ?
    long countByProviderUsed(String providerUsed);

}