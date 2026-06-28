package dev.dmitriy.job_x_hunter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Optional;

@Getter
@Setter
@Entity
@Table(name = "user_skill")
public class UserProfileSkill {
    @EmbeddedId
    private UserProfileSkillId id;

    @ManyToOne
    @MapsId("userProfileId")
    @JoinColumn(name="user_profile_id")
    private UserProfile userProfile;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name="skill_id")
    private Skill skill;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Integer proficiency;

    public UserProfileSkill(UserProfile userProfile, Skill skill, @NotNull Integer proficiency) {
        this.userProfile = userProfile;
        this.skill = skill;
        this.proficiency = proficiency;
    }

    public UserProfileSkill(UserProfileSkillId id, UserProfile up, Skill skill, Integer proficiency) {
        this.id = id;
        this.userProfile = up;
        this.skill = skill;
        this.proficiency = proficiency;
    }
    //1	Beginner
    //2	Elementary
    //3	Intermediate
    //4	Advanced
    //5	Expert
}
