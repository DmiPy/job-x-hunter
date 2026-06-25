package dev.dmitriy.job_x_hunter.entity;

import jakarta.persistence.*;

@Entity
public class UserSkill {
    @EmbeddedId
    private UserSkillId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name="skill_id")
    private Skill skill;

    private Integer proficiency;
}
