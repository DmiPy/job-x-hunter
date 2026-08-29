package dev.dmitriy.job_x_hunter.service;

import org.springframework.stereotype.Service;

@Service
public class JobIngestionService {
    public void ingest(){
        System.out.println("call adzunaJobSource, JoobleJobSource, GreenhouseJobSource");
    }
}
