package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.dto.SearchFilterRequest;
import dev.dmitriy.job_x_hunter.dto.SearchFilterResponse;
import dev.dmitriy.job_x_hunter.entity.SearchFilter;
import dev.dmitriy.job_x_hunter.entity.Skill;
import dev.dmitriy.job_x_hunter.entity.UserProfile;
import dev.dmitriy.job_x_hunter.exception.*;
import dev.dmitriy.job_x_hunter.repository.SearchFilterRepository;
import dev.dmitriy.job_x_hunter.repository.SkillRepository;
import dev.dmitriy.job_x_hunter.repository.UserProfileRepository;
import dev.dmitriy.job_x_hunter.security.CurrentUserService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchFilterService {
    private final SearchFilterRepository filters;
    private final UserProfileRepository profiles;
    private final SkillRepository skills;
    private final CurrentUserService currentUser;

    public SearchFilterService(SearchFilterRepository filters, UserProfileRepository profiles,
                               SkillRepository skills, CurrentUserService currentUser) {
        this.filters = filters;
        this.profiles = profiles;
        this.skills = skills;
        this.currentUser = currentUser;
    }

    private UserProfile myProfile(Jwt jwt) {
        return profiles.findByUser_UserId(currentUser.getOrProvision(jwt).getUserId())
                .orElseThrow(() -> new UserProfileNotFoundException("Profile not found."));
    }

    private SearchFilter myFilter(Jwt jwt) {
        return filters.findByUserProfile_UserProfileId(myProfile(jwt).getUserProfileId())
                .orElseThrow(SearchFilterNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public SearchFilterResponse get(Jwt jwt) { return response(myFilter(jwt)); }

    @Transactional
    public SearchFilterResponse create(SearchFilterRequest request, Jwt jwt) {
        UserProfile profile = myProfile(jwt);
        if (filters.existsByUserProfile_UserProfileId(profile.getUserProfileId())) {
            throw new SearchFilterAlreadyExistsException();
        }
        SearchFilter filter = new SearchFilter();
        filter.setUserProfile(profile);
        apply(filter, request);
        return response(filters.saveAndFlush(filter));
    }

    @Transactional
    public SearchFilterResponse patch(SearchFilterRequest request, Jwt jwt) {
        SearchFilter filter = myFilter(jwt);
        apply(filter, request);
        return response(filters.saveAndFlush(filter));
    }

    @Transactional
    public void delete(Jwt jwt) { filters.delete(myFilter(jwt)); }

    private void apply(SearchFilter filter, SearchFilterRequest request) {
        if (request.has("preferredLocation")) filter.setPreferredLocation(request.getPreferredLocation());
        if (request.has("radiusKm")) filter.setRadiusKm(request.getRadiusKm());
        if (request.has("minimumSalary")) filter.setMinimumSalary(request.getMinimumSalary());
        if (request.has("keywords")) filter.setKeywords(request.getKeywords());
        if (request.has("employmentTypes")) filter.setEmploymentTypes(enumSet(request.getEmploymentTypes()));
        if (request.has("workplaceTypes")) filter.setWorkplaceTypes(enumSet(request.getWorkplaceTypes()));
        if (request.has("preferredSkillIds")) filter.setPreferredSkills(resolveSkills(request.getPreferredSkillIds()));
        if (request.has("excludedSkillIds")) filter.setExcludedSkills(resolveSkills(request.getExcludedSkillIds()));
        Set<Long> preferred = filter.getPreferredSkills().stream().map(Skill::getSkillId).collect(Collectors.toSet());
        if (filter.getExcludedSkills().stream().map(Skill::getSkillId).anyMatch(preferred::contains)) {
            throw new InvalidSearchFilterException("A skill cannot be both preferred and excluded.");
        }
    }

    private <T> Set<T> enumSet(List<T> values) {
        if (values == null) return new LinkedHashSet<>();
        if (values.contains(null) || values.size() != new HashSet<>(values).size()) {
            throw new InvalidSearchFilterException("Selection contains null or duplicate values.");
        }
        return new LinkedHashSet<>(values);
    }

    private Set<Skill> resolveSkills(List<Long> ids) {
        if (ids == null) return new LinkedHashSet<>();
        if (ids.contains(null) || ids.size() != new HashSet<>(ids).size()) {
            throw new InvalidSearchFilterException("Skill IDs contain null or duplicate values.");
        }
        Set<Skill> result = new LinkedHashSet<>();
        skills.findAllById(ids).forEach(result::add);
        if (result.size() != ids.size()) throw new InvalidSearchFilterException("Unknown skill ID.");
        return result;
    }

    private SearchFilterResponse response(SearchFilter f) {
        return new SearchFilterResponse(f.getId(), f.getPreferredLocation(), f.getRadiusKm(),
                f.getMinimumSalary(), "EUR", "ANNUAL_GROSS", f.getKeywords(),
                Set.copyOf(f.getEmploymentTypes()), Set.copyOf(f.getWorkplaceTypes()),
                f.getPreferredSkills().stream().map(Skill::getSkillId).collect(Collectors.toSet()),
                f.getExcludedSkills().stream().map(Skill::getSkillId).collect(Collectors.toSet()));
    }
}
