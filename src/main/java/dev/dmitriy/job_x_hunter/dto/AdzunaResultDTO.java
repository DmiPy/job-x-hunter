package dev.dmitriy.job_x_hunter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdzunaResultDTO {
    private String id;
    private String title;
    private String description;
    private String created;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    private String adref;
    private Double latitude;
    private Double longitude;
    private AdzunaLocationDTO location;
    private AdzunaCategoryDTO category;
    private AdzunaCompanyDTO company;

    @JsonProperty("salary_min")
    private Integer salaryMin;

    @JsonProperty("salary_max")
    private Integer salaryMax;

    @JsonProperty("salary_is_predicted")
    private String salaryIsPredicted;

    @JsonProperty("contract_time")
    private String contractTime;

    @JsonProperty("contract_type")
    private String contractType;
}
