package dev.dmitriy.job_x_hunter.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdzunaApiClientResponseDTO {
    private Integer count;
    private Integer mean;
    private List<AdzunaResultDTO> results;
}
