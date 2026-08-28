package dev.dmitriy.job_x_hunter.jobparsing;

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

    public AdzunaApiClientResponseDTO getJobs(
            Integer jobsAmount,
            String[] keywords,
            String[] locations,
            Integer distance,
            Integer maxDaysOld,
            Integer salaryMin,
            Integer salaryMax,
            String fullTime,
            String partTime
    ){
        if(properties.getAppId() == null || properties.getAppKey() == null){
            throw new AdzunaApiClientNotConfiguredCorrectlyException("Adzuna did not receive an App Id or App Key.");
        }

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.adzuna.com")
                        .path("/v1/api/jobs/{country}/search/{page}")
                        .queryParam("app_id", APP_ID)
                        .queryParam("app_key", APP_KEY)
                        .queryParam("results_per_page", jobsAmount)
                        .queryParam("what", keywords)
                        .queryParam("where", locations)
                        .queryParam("distance", distance)
                        .queryParam("max_days_old", maxDaysOld)
                        .queryParam("salary_min", salaryMin)
                        .queryParam("salary_max", salaryMax)
                        .queryParam("full_time", fullTime)
                        .queryParam("part_time", partTime)
                        .build(COUNTRY_CODE, 1))
                .retrieve()
                .body(AdzunaApiClientResponseDTO.class);


    }
}
