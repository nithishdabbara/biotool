package com.biotool.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID; // Import for generating unique IDs

/**
 * Represents a saved analysis result in the database.
 * Each instance of this class will be a row in the 'saved_analyses' table.
 */
@Entity
@Table(name = "saved_analyses")
public class SavedAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NEW: A unique, user-facing ID for each analysis
    private String archiveId;
    
    // NEW: The status of the analysis
    private String status;

    @Lob
    private String originalSequence;

    private String sequenceType;
    private int sequenceLength;
    private double gcContent;
    
    @Lob
    private String rnaTranscript;

    @Lob
    private String proteinSequence;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // Automatically generate a unique Archive ID before saving
        if (archiveId == null) {
            archiveId = "ARC-" + UUID.randomUUID().toString().toUpperCase().substring(0, 8);
        }
        // Set a default status
        if (status == null) {
            status = "Completed";
        }
    }

    // --- Getters and Setters for all fields, including new ones ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getArchiveId() { return archiveId; }
    public void setArchiveId(String archiveId) { this.archiveId = archiveId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOriginalSequence() { return originalSequence; }
    public void setOriginalSequence(String originalSequence) { this.originalSequence = originalSequence; }
    public String getSequenceType() { return sequenceType; }
    public void setSequenceType(String sequenceType) { this.sequenceType = sequenceType; }
    public int getSequenceLength() { return sequenceLength; }
    public void setSequenceLength(int sequenceLength) { this.sequenceLength = sequenceLength; }
    public double getGcContent() { return gcContent; }
    public void setGcContent(double gcContent) { this.gcContent = gcContent; }
    public String getRnaTranscript() { return rnaTranscript; }
    public void setRnaTranscript(String rnaTranscript) { this.rnaTranscript = rnaTranscript; }
    public String getProteinSequence() { return proteinSequence; }
    public void setProteinSequence(String proteinSequence) { this.proteinSequence = proteinSequence; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

