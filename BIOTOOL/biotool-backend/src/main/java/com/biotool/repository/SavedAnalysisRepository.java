package com.biotool.repository;

import com.biotool.entity.SavedAnalysis;
import com.biotool.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository provides methods to interact with the 'saved_analyses' table in the database.
 * By extending JpaRepository, we get a full set of CRUD operations for free.
 */
@Repository
public interface SavedAnalysisRepository extends JpaRepository<SavedAnalysis, Long> {

    /**
     * A custom method to find all analyses saved by a specific user.
     * Spring Data JPA will automatically create the query for this method based on its name.
     *
     * @param user The user whose saved analyses we want to retrieve.
     * @return A list of SavedAnalysis objects.
     */
    List<SavedAnalysis> findByUser(User user);

}
