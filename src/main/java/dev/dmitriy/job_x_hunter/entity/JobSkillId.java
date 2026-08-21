package dev.dmitriy.job_x_hunter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;


import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JobSkillId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "job_vacancy_id")
    private Long jobVacancyId;

    @Column(name="skill_id")
    private Long skillId;
}
