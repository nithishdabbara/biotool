package com.biotool.controller;

import com.biotool.model.AnalysisRequest;
import com.biotool.model.AnalysisResult;
import com.biotool.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sequence")
@CrossOrigin(origins = "*")
public class SequenceController {

    private final SequenceService sequenceService;

    @Autowired
    public SequenceController(SequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResult> analyzeSequence(@RequestBody AnalysisRequest request) {
        if (request.getSequence() == null || request.getSequence().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        AnalysisResult result = sequenceService.analyze(request.getSequence());
        return ResponseEntity.ok(result);
    }
}