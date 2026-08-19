package dev.dmitriy.job_x_hunter.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Setter
@Getter
public class JobVacancyResponseDTO {
    private String title;
    private Long jobVacancyId;
    private String url;
    private LocalDate postDate;
    private String description;
    private String source;
    private String sourceJobId;
    private String company;
    private String companyLocation;
}

