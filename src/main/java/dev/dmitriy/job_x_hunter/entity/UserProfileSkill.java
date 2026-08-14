package dev.dmitriy.job_x_hunter.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "user_profile_skill")
public class UserProfileSkill {
    @EmbeddedId
    private UserProfileSkillId id = new UserProfileSkillId();

    @ManyToOne
    @MapsId("userProfileId")
    @JoinColumn(name="user_profile_id")
    private UserProfile userProfile;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name="skill_id")
    private Skill skill;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfileSkill that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Integer proficiency;

    public UserProfileSkill() {
    }

    //1	Beginner
    //2	Elementary
    //3	Intermediate
    //4	Advanced
    //5	Expert
}
