package dev.dmitriy.job_x_hunter.jobparsing;

import dev.dmitriy.job_x_hunter.dto.AdzunaApiClientQueryDTO;
import dev.dmitriy.job_x_hunter.dto.AdzunaApiClientResponseDTO;
import dev.dmitriy.job_x_hunter.exception.AdzunaApiClientNotConfiguredCorrectlyException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AdzunaApiClient {
    private final RestClient restClient;
    private final AdzunaProperties properties;

    public AdzunaApiClient(RestClient restClient, AdzunaProperties adzunaProperties){
        this.restClient = restClient;
        this.properties = adzunaProperties;
    }

    public AdzunaApiClientResponseDTO getJobs(AdzunaApiClientQueryDTO query) {
        if(properties.getAppId() == null || properties.getAppKey() == null){
            throw new AdzunaApiClientNotConfiguredCorrectlyException("Adzuna did not receive an App Id or App Key.");
        }

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.adzuna.com")
                        .path("/v1/api/jobs/{country}/search/{page}")
                        .queryParam("app_id", properties.getAppId())
                        .queryParam("app_key", properties.getAppKey())
                        .queryParam("results_per_page", query.getJobsAmount())
                        .queryParam("what", query.getKeywords())
                        .queryParam("where", query.getLocations())
                        .queryParam("distance", query.getDistance())
                        .queryParam("max_days_old", query.getMaxDaysOld())
                        .queryParam("salary_min", query.getSalaryMin())
                        .queryParam("salary_max", query.getSalaryMax())
                        .queryParam("full_time", query.getFullTime())
                        .queryParam("part_time", query.getPartTime())
                        .build(properties.getCountryCode(), 1))
                .retrieve()
                .body(AdzunaApiClientResponseDTO.class);


    }
}
