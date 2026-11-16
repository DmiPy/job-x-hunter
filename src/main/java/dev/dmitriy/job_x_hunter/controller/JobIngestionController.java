package dev.dmitriy.job_x_hunter.controller;

import dev.dmitriy.job_x_hunter.ingestion.IngestionAlreadyRunningException;
import dev.dmitriy.job_x_hunter.ingestion.JobIngestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/ingestion")
public class JobIngestionController {
    private final JobIngestionService service;
    public JobIngestionController(JobIngestionService service) { this.service = service; }
    @PostMapping
    public JobIngestionService.RunSummary ingest() {
        try { return service.ingest(); }
        catch (IngestionAlreadyRunningException e) { throw new ResponseStatusException(HttpStatus.CONFLICT, "Ingestion already running"); }
    }
}
