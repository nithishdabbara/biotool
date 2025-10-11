package com.biotool.controller;

import com.biotool.entity.SavedAnalysis;
import com.biotool.entity.User;
import com.biotool.model.AnalysisRequest;
import com.biotool.model.AnalysisResult;
import com.biotool.repository.SavedAnalysisRepository;
import com.biotool.repository.UserRepository;
import com.biotool.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles API requests related to saving and retrieving analysis results.
 * All endpoints in this controller are protected and require authentication.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private SavedAnalysisRepository savedAnalysisRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SequenceService sequenceService;

    /**
     * Analyzes a sequence and saves the result to the authenticated user's account.
     * @param request The request containing the sequence to analyze and save.
     * @return A success message.
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveAnalysis(@RequestBody AnalysisRequest request) {
        // Get the currently logged-in user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Perform the analysis using the existing service
        AnalysisResult analysisResult = sequenceService.analyze(request.getSequence());

        // Create a new entity to save to the database
        SavedAnalysis savedAnalysis = new SavedAnalysis();
        savedAnalysis.setOriginalSequence(request.getSequence().toUpperCase());
        savedAnalysis.setSequenceType(analysisResult.getSequenceType());
        savedAnalysis.setSequenceLength(analysisResult.getLength());
        savedAnalysis.setGcContent(analysisResult.getGcContent());
        savedAnalysis.setRnaTranscript(analysisResult.getRnaTranscript());
        savedAnalysis.setProteinSequence(analysisResult.getProteinSequence());
        savedAnalysis.setUser(user); // Link the result to the current user

        // Save the entity to the database using its repository
        savedAnalysisRepository.save(savedAnalysis);

        return ResponseEntity.ok("Analysis saved successfully!");
    }

    /**
     * Retrieves the analysis history for the authenticated user.
     * @return A list of all saved analyses for the current user.
     */
    @GetMapping("/history")
    public ResponseEntity<List<SavedAnalysis>> getAnalysisHistory() {
        // Get the currently logged-in user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Use our custom repository method to find all results for this user
        List<SavedAnalysis> history = savedAnalysisRepository.findByUser(user);
        
        return ResponseEntity.ok(history);
    }

    /**
     * Deletes a specific saved analysis by its ID.
     * Ensures that a user can only delete their own analysis results for security.
     * @param id The ID of the analysis to delete.
     * @return A success message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnalysis(@PathVariable Long id) {
        // Get the currently logged-in user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Find the analysis by its ID and ensure it belongs to the current user
        return savedAnalysisRepository.findById(id)
                .map(analysis -> {
                    if (analysis.getUser().getId().equals(user.getId())) {
                        savedAnalysisRepository.delete(analysis);
                        return ResponseEntity.ok("Analysis deleted successfully.");
                    } else {
                        // If the analysis does not belong to the user, return a 'Forbidden' error
                        return ResponseEntity.status(403).body("Error: You do not have permission to delete this analysis.");
                    }
                }).orElse(ResponseEntity.notFound().build()); // If no analysis with that ID is found
    }
}

