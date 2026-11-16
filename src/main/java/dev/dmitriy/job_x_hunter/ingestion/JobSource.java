package dev.dmitriy.job_x_hunter.ingestion;

public interface JobSource {
    String name();
    boolean enabled();
    SourceFetchResult fetch();
}
