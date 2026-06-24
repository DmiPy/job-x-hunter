package dev.dmitriy.job_x_hunter.dto;

public class SkillResponseDTO {
    private Long skillId;
    private String skillName;

    public Long getSkillId() {
        return skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }
}
