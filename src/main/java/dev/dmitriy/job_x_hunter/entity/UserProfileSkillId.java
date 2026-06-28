package dev.dmitriy.job_x_hunter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Embeddable
public class UserProfileSkillId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserProfileSkillId() {}

    @Column(name="user_profile_id")
    private Long userProfileId;

    @Column(name="skill_id")
    private Long skillId;



}
