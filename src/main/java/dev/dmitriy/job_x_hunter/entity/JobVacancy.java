package dev.dmitriy.job_x_hunter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;


@Entity
@Table(name="job_vacancy")
@Getter
@Setter
public class JobVacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobVacancyId;

    @NotBlank
    private String title;

    @NotBlank
    private String url;

    @Column(name = "post_date", updatable = false)
    private LocalDate postDate;

    private String description;

    @NotBlank
    private String company;

    @NotBlank
    private String companyLocation;
}

