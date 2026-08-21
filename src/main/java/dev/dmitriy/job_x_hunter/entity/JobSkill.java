package dev.dmitriy.job_x_hunter.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="job_skill")
public class JobSkill {
    @EmbeddedId
    private JobSkillId id;

    @ManyToOne
    @MapsId("jobVacancyId")
    @JoinColumn(name="job_vacancy_id")
    private JobVacancy jobVacancy;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name="skill_id")
    private Skill skill;

}

