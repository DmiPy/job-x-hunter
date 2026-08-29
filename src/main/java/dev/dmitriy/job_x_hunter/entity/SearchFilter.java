package dev.dmitriy.job_x_hunter.entity;

import dev.dmitriy.job_x_hunter.enums.EmploymentType;
import dev.dmitriy.job_x_hunter.enums.WorkplaceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "search_filter")
public class SearchFilter {
    @Id
    @GeneratedValue
    private Long searchFilterId;

    @OneToOne
    @JoinColumn(name="user_profile_id")
    private UserProfile userProfile;

    @Column(name="min_salary")
    private Integer minSalary;

    @Column(name="max_salary")
    private Integer maxSalary;

    @Column(name="employment_type")
    private EmploymentType employmentType;

    @Column(name="workplace_type")
    private WorkplaceType workplaceType;

    @Column(name="is_notify_active")
    private boolean isNotifyActive;

    @Column(name="location")
    private String location;

    @Column(name="distance")
    private Integer distance;

    @Column(name="keywords")
    private String keywords;
}
