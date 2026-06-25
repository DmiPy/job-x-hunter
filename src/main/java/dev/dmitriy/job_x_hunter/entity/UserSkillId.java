package dev.dmitriy.job_x_hunter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UserSkillId implements Serializable {
    public UserSkillId() {}

    @Column(name="user_id")
    private UUID userId;

    @Column(name="skill_id")
    private Long skillId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserSkillId that = (UserSkillId) o;
        return Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getSkillId(), that.getSkillId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getSkillId());
    }

    public UUID getUserId() {
        return userId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

}
