package dev.dmitriy.job_x_hunter.entity;

import dev.dmitriy.job_x_hunter.enums.EmploymentType;
import dev.dmitriy.job_x_hunter.enums.WorkplaceType;
import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "search_filter", uniqueConstraints = @UniqueConstraint(name = "uk_search_filter_profile", columnNames = "user_profile_id"))
public class SearchFilter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_filter_id")
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false, unique = true)
    private UserProfile userProfile;

    @Column(name = "preferred_location") private String preferredLocation;
    @Column(name = "radius_km") private Integer radiusKm;
    // Annual gross salary in EUR; no vacancy currency conversion occurs here.
    @Column(name = "min_salary") private Integer minimumSalary;
    @Column(name = "keywords") private String keywords;

    @ElementCollection
    @CollectionTable(name = "search_filter_employment_type", joinColumns = @JoinColumn(name = "search_filter_id"))
    @Column(name = "employment_type") @Enumerated(EnumType.STRING)
    private Set<EmploymentType> employmentTypes = new LinkedHashSet<>();

    @ElementCollection
    @CollectionTable(name = "search_filter_workplace_type", joinColumns = @JoinColumn(name = "search_filter_id"))
    @Column(name = "workplace_type") @Enumerated(EnumType.STRING)
    private Set<WorkplaceType> workplaceTypes = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "search_filter_preferred_skill", joinColumns = @JoinColumn(name = "search_filter_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Set<Skill> preferredSkills = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "search_filter_prohibited_skill", joinColumns = @JoinColumn(name = "search_filter_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Set<Skill> excludedSkills = new LinkedHashSet<>();

    public Long getId() { return id; }
    public UserProfile getUserProfile() { return userProfile; }
    public void setUserProfile(UserProfile value) { userProfile = value; }
    public String getPreferredLocation() { return preferredLocation; }
    public void setPreferredLocation(String value) { preferredLocation = value; }
    public Integer getRadiusKm() { return radiusKm; }
    public void setRadiusKm(Integer value) { radiusKm = value; }
    public Integer getMinimumSalary() { return minimumSalary; }
    public void setMinimumSalary(Integer value) { minimumSalary = value; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String value) { keywords = value; }
    public Set<EmploymentType> getEmploymentTypes() { return employmentTypes; }
    public void setEmploymentTypes(Set<EmploymentType> value) { employmentTypes = value; }
    public Set<WorkplaceType> getWorkplaceTypes() { return workplaceTypes; }
    public void setWorkplaceTypes(Set<WorkplaceType> value) { workplaceTypes = value; }
    public Set<Skill> getPreferredSkills() { return preferredSkills; }
    public void setPreferredSkills(Set<Skill> value) { preferredSkills = value; }
    public Set<Skill> getExcludedSkills() { return excludedSkills; }
    public void setExcludedSkills(Set<Skill> value) { excludedSkills = value; }
}
