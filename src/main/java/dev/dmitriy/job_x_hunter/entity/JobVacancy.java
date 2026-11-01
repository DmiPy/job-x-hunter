package dev.dmitriy.job_x_hunter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Table(name = "job_vacancy",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_job_vacancy_source_job_id",
                    columnNames = {"source", "source_job_id"}
                )
        }
)
@Getter
@Setter
public class JobVacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobVacancyId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String source;

    @Column(name = "source_job_id")
    @NotBlank
    private String sourceJobId;

    @Column(name = "post_date", updatable = false, nullable = false)
    private LocalDate postDate = LocalDate.now();

    @Column
    private String description;

    @Column
    private String company;

    @Column
    private String companyLocation;

}

