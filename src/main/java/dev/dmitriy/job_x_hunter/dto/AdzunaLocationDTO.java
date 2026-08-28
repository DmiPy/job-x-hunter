package dev.dmitriy.job_x_hunter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdzunaLocationDTO {
    @JsonProperty("display_name")
    private String displayName;
    private List<String> area;
}
