package com.biotool.model;

import java.util.List;
import java.util.Map;

/**
 * A Data Transfer Object (DTO) that represents the complete result of a sequence analysis.
 * This object is returned by the API to the frontend.
 */
public class AnalysisResult {

    private int length;
    private double gcContent;
    private Map<Character, Integer> nucleotideCounts;
    private String rnaTranscript;
    private String proteinSequence;
    private String sequenceType;
    private List<String> openReadingFrames;
    private String reverseComplement;
    private double meltingTemperature;

    // Getters and Setters for all fields, required for JSON serialization

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getGcContent() {
        return gcContent;
    }

    public void setGcContent(double gcContent) {
        this.gcContent = gcContent;
    }

    public Map<Character, Integer> getNucleotideCounts() {
        return nucleotideCounts;
    }

    public void setNucleotideCounts(Map<Character, Integer> nucleotideCounts) {
        this.nucleotideCounts = nucleotideCounts;
    }

    public String getRnaTranscript() {
        return rnaTranscript;
    }

    public void setRnaTranscript(String rnaTranscript) {
        this.rnaTranscript = rnaTranscript;
    }

    public String getProteinSequence() {
        return proteinSequence;
    }

    public void setProteinSequence(String proteinSequence) {
        this.proteinSequence = proteinSequence;
    }

    public String getSequenceType() {
        return sequenceType;
    }

    public void setSequenceType(String sequenceType) {
        this.sequenceType = sequenceType;
    }

    public List<String> getOpenReadingFrames() {
        return openReadingFrames;
    }

    public void setOpenReadingFrames(List<String> openReadingFrames) {
        this.openReadingFrames = openReadingFrames;
    }

    public String getReverseComplement() {
        return reverseComplement;
    }

    public void setReverseComplement(String reverseComplement) {
        this.reverseComplement = reverseComplement;
    }

    public double getMeltingTemperature() {
        return meltingTemperature;
    }

    public void setMeltingTemperature(double meltingTemperature) {
        this.meltingTemperature = meltingTemperature;
    }
}

