package dev.dmitriy.job_x_hunter.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "job_vacancy",
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

    @Column(nullable = false, length = 2048)
    private String url;

    @Column(nullable = false)
    private String source;

    @Column(name = "source_job_id", nullable = false)
    private String sourceJobId;

    @Column(name = "post_date")
    private LocalDate postDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String company;

    @Column(name = "company_location")
    private String companyLocation;

    @Column(name = "min_salary", precision = 10, scale = 2)
    private BigDecimal minSalary;

    @Column(name = "max_salary", precision = 10, scale = 2)
    private BigDecimal maxSalary;

    @Column(name = "contract_type")
    private String contractType;

    @Column(name = "contract_time")
    private String contractTime;
}