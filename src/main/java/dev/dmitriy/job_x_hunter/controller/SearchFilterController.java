package dev.dmitriy.job_x_hunter.controller;

import dev.dmitriy.job_x_hunter.dto.SearchFilterRequest;
import dev.dmitriy.job_x_hunter.dto.SearchFilterResponse;
import dev.dmitriy.job_x_hunter.service.SearchFilterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search-filters/me")
public class SearchFilterController {
    private final SearchFilterService service;
    public SearchFilterController(SearchFilterService service) { this.service = service; }

    @GetMapping public SearchFilterResponse get(@AuthenticationPrincipal Jwt jwt) { return service.get(jwt); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public SearchFilterResponse create(@Valid @RequestBody SearchFilterRequest request, @AuthenticationPrincipal Jwt jwt) {
        return service.create(request, jwt);
    }
    @PatchMapping public SearchFilterResponse patch(@Valid @RequestBody SearchFilterRequest request, @AuthenticationPrincipal Jwt jwt) {
        return service.patch(request, jwt);
    }
    @DeleteMapping @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt) { service.delete(jwt); }
}
