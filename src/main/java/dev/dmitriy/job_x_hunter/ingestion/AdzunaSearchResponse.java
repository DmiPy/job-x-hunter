package dev.dmitriy.job_x_hunter.ingestion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/** The provider may add fields or omit optional fields on individual results. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AdzunaSearchResponse(List<JsonNode> results, Long count) { }
