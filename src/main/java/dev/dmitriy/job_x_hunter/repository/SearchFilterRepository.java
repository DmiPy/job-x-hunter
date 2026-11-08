package dev.dmitriy.job_x_hunter.repository;

import dev.dmitriy.job_x_hunter.entity.SearchFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SearchFilterRepository extends JpaRepository<SearchFilter, Long> {
    Optional<SearchFilter> findByUserProfile_UserProfileId(Long profileId);
    boolean existsByUserProfile_UserProfileId(Long profileId);
}
