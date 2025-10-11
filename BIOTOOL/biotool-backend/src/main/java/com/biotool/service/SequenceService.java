package com.biotool.service;

import com.biotool.model.AnalysisResult;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This service contains the core business logic for all bioinformatics calculations.
 */
@Service
public class SequenceService {

    // A map to translate RNA codons into single-letter amino acid codes based on the standard genetic code.
    private static final Map<String, String> CODON_MAP = new HashMap<>();
    static {
        CODON_MAP.put("AUG", "M"); CODON_MAP.put("UUU", "F"); CODON_MAP.put("UUC", "F");
        CODON_MAP.put("UUA", "L"); CODON_MAP.put("UUG", "L"); CODON_MAP.put("CUU", "L");
        CODON_MAP.put("CUC", "L"); CODON_MAP.put("CUA", "L"); CODON_MAP.put("CUG", "L");
        CODON_MAP.put("AUU", "I"); CODON_MAP.put("AUC", "I"); CODON_MAP.put("AUA", "I");
        CODON_MAP.put("GUU", "V"); CODON_MAP.put("GUC", "V"); CODON_MAP.put("GUA", "V");
        CODON_MAP.put("GUG", "V"); CODON_MAP.put("UCU", "S"); CODON_MAP.put("UCC", "S");
        CODON_MAP.put("UCA", "S"); CODON_MAP.put("UCG", "S"); CODON_MAP.put("AGU", "S");
        CODON_MAP.put("AGC", "S"); CODON_MAP.put("CCU", "P"); CODON_MAP.put("CCC", "P");
        CODON_MAP.put("CCA", "P"); CODON_MAP.put("CCG", "P"); CODON_MAP.put("ACU", "T");
        CODON_MAP.put("ACC", "T"); CODON_MAP.put("ACA", "T"); CODON_MAP.put("ACG", "T");
        CODON_MAP.put("GCU", "A"); CODON_MAP.put("GCC", "A"); CODON_MAP.put("GCA", "A");
        CODON_MAP.put("GCG", "A"); CODON_MAP.put("UAU", "Y"); CODON_MAP.put("UAC", "Y");
        CODON_MAP.put("UAA", "*"); CODON_MAP.put("UAG", "*"); CODON_MAP.put("UGA", "*"); // Stop codons
        CODON_MAP.put("CAU", "H"); CODON_MAP.put("CAC", "H"); CODON_MAP.put("CAA", "Q");
        CODON_MAP.put("CAG", "Q"); CODON_MAP.put("AAU", "N"); CODON_MAP.put("AAC", "N");
        CODON_MAP.put("AAA", "K"); CODON_MAP.put("AAG", "K"); CODON_MAP.put("GAU", "D");
        CODON_MAP.put("GAC", "D"); CODON_MAP.put("GAA", "E"); CODON_MAP.put("GAG", "E");
        CODON_MAP.put("UGU", "C"); CODON_MAP.put("UGC", "C"); CODON_MAP.put("UGG", "W");
        CODON_MAP.put("CGU", "R"); CODON_MAP.put("CGC", "R"); CODON_MAP.put("CGA", "R");
        CODON_MAP.put("CGG", "R"); CODON_MAP.put("AGA", "R"); CODON_MAP.put("AGG", "R");
        CODON_MAP.put("GGU", "G"); CODON_MAP.put("GGC", "G"); CODON_MAP.put("GGA", "G");
        CODON_MAP.put("GGG", "G");
    }

    // A regular expression to quickly validate if a sequence consists only of DNA characters.
    private static final Pattern DNA_PATTERN = Pattern.compile("^[ATGC]+$");

    /**
     * The main analysis method that orchestrates all calculations.
     * @param sequence The input DNA sequence from the user.
     * @return An AnalysisResult object containing all calculated data.
     */
    public AnalysisResult analyze(String sequence) {
        AnalysisResult result = new AnalysisResult();
        String upperSeq = sequence.toUpperCase().trim();

        result.setLength(upperSeq.length());
        
        if (isDna(upperSeq)) {
            result.setSequenceType("DNA");
            result.setGcContent(calculateGcContent(upperSeq));
            result.setNucleotideCounts(countNucleotides(upperSeq));
            String rna = transcribe(upperSeq);
            result.setRnaTranscript(rna);
            result.setProteinSequence(translate(rna));
            result.setOpenReadingFrames(findOrfs(upperSeq));
            result.setReverseComplement(calculateReverseComplement(upperSeq));
            result.setMeltingTemperature(calculateMeltingTemp(upperSeq));
        } else {
            result.setSequenceType("Unknown");
            result.setGcContent(0);
            result.setNucleotideCounts(new HashMap<>());
            result.setRnaTranscript("N/A for non-DNA sequences");
            result.setProteinSequence("N/A for non-DNA sequences");
            result.setOpenReadingFrames(new ArrayList<>());
            result.setReverseComplement("N/A for non-DNA sequences");
            result.setMeltingTemperature(0);
        }

        return result;
    }

    private boolean isDna(String sequence) {
        return DNA_PATTERN.matcher(sequence).matches();
    }

    private double calculateGcContent(String dna) {
        if (dna.isEmpty()) return 0;
        long gcCount = dna.chars().filter(c -> c == 'G' || c == 'C').count();
        return (double) gcCount / dna.length() * 100;
    }

    private Map<Character, Integer> countNucleotides(String dna) {
        Map<Character, Integer> counts = new HashMap<>();
        counts.put('A', 0); counts.put('T', 0); counts.put('G', 0); counts.put('C', 0);
        for (char nucleotide : dna.toCharArray()) {
            counts.put(nucleotide, counts.getOrDefault(nucleotide, 0) + 1);
        }
        return counts;
    }

    private String transcribe(String dna) {
        return dna.replace('T', 'U');
    }

    private String translate(String rna) {
        StringBuilder protein = new StringBuilder();
        // Find the first start codon to begin translation
        int startCodonIndex = rna.indexOf("AUG");
        if (startCodonIndex == -1) {
            return "No start codon found.";
        }

        for (int i = startCodonIndex; i <= rna.length() - 3; i += 3) {
            String codon = rna.substring(i, i + 3);
            String aminoAcid = CODON_MAP.getOrDefault(codon, "?");
            if ("*".equals(aminoAcid)) { // Stop at the first stop codon
                break;
            }
            protein.append(aminoAcid);
        }
        return protein.toString();
    }
    
    private List<String> findOrfs(String dna) {
        List<String> orfs = new ArrayList<>();
        Pattern startCodon = Pattern.compile("ATG");
        Matcher matcher = startCodon.matcher(dna);

        while (matcher.find()) {
            int startIndex = matcher.start();
            for (int i = startIndex + 3; i <= dna.length() - 3; i += 3) {
                String codon = dna.substring(i, i + 3);
                if (codon.equals("TAA") || codon.equals("TAG") || codon.equals("TGA")) {
                    orfs.add(dna.substring(startIndex, i + 3));
                    break; 
                }
            }
        }
        return orfs;
    }
    
    private String calculateReverseComplement(String dna) {
        StringBuilder complement = new StringBuilder();
        for (char base : dna.toCharArray()) {
            switch (base) {
                case 'A': complement.append('T'); break;
                case 'T': complement.append('A'); break;
                case 'G': complement.append('C'); break;
                case 'C': complement.append('G'); break;
                default: complement.append('N'); break;
            }
        }
        return complement.reverse().toString();
    }

    private double calculateMeltingTemp(String dna) {
        if (dna.isEmpty()) return 0;
        Map<Character, Integer> counts = countNucleotides(dna);
        int a = counts.getOrDefault('A', 0);
        int t = counts.getOrDefault('T', 0);
        int g = counts.getOrDefault('G', 0);
        int c = counts.getOrDefault('C', 0);

        if (dna.length() < 20) {
            // Basic formula for short sequences
            return (2.0 * (a + t)) + (4.0 * (g + c));
        } else {
            // Marmur dot plot formula for longer sequences
            return 64.9 + 41.0 * (g + c - 16.4) / (a + t + g + c);
        }
    }
}

