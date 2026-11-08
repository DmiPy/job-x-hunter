package dev.dmitriy.job_x_hunter.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.dmitriy.job_x_hunter.enums.EmploymentType;
import dev.dmitriy.job_x_hunter.enums.WorkplaceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFilterRequest {
    @Size(max = 255) private String preferredLocation;
    @Min(0) private Integer radiusKm;
    @Min(0) private Integer minimumSalary;
    @Size(max = 1000) private String keywords;
    private List<EmploymentType> employmentTypes;
    private List<WorkplaceType> workplaceTypes;
    private List<Long> preferredSkillIds;
    private List<Long> excludedSkillIds;
    @JsonIgnore private final Set<String> provided = new HashSet<>();

    public boolean has(String field) { return provided.contains(field); }
    public String getPreferredLocation() { return preferredLocation; }
    public void setPreferredLocation(String v) { provided.add("preferredLocation"); preferredLocation = v; }
    public Integer getRadiusKm() { return radiusKm; }
    public void setRadiusKm(Integer v) { provided.add("radiusKm"); radiusKm = v; }
    public Integer getMinimumSalary() { return minimumSalary; }
    public void setMinimumSalary(Integer v) { provided.add("minimumSalary"); minimumSalary = v; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String v) { provided.add("keywords"); keywords = v; }
    public List<EmploymentType> getEmploymentTypes() { return employmentTypes; }
    public void setEmploymentTypes(List<EmploymentType> v) { provided.add("employmentTypes"); employmentTypes = v; }
    public List<WorkplaceType> getWorkplaceTypes() { return workplaceTypes; }
    public void setWorkplaceTypes(List<WorkplaceType> v) { provided.add("workplaceTypes"); workplaceTypes = v; }
    public List<Long> getPreferredSkillIds() { return preferredSkillIds; }
    public void setPreferredSkillIds(List<Long> v) { provided.add("preferredSkillIds"); preferredSkillIds = v; }
    public List<Long> getExcludedSkillIds() { return excludedSkillIds; }
    public void setExcludedSkillIds(List<Long> v) { provided.add("excludedSkillIds"); excludedSkillIds = v; }
}
