package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.entity.SearchFilter;
import dev.dmitriy.job_x_hunter.job.AdzunaJobSource;
import dev.dmitriy.job_x_hunter.job.JobVacancyIngestionJob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class JobIngestionService {

    private final AdzunaJobSource adzunaJobSource;

    public JobIngestionService(AdzunaJobSource adzunaJobSource){
        this.adzunaJobSource = adzunaJobSource;
    }
    public void ingest(){
        System.out.println("call adzunaJobSource, JoobleJobSource, GreenhouseJobSource");
        SearchFilter filter = new SearchFilter();
        filter.set
        this.adzunaJobSource.search()
    }
}



//public class AdzunaApiClientQueryDTO {
//    @NotNull
//    private Integer jobsAmount;
//
//    @NotEmpty
//    private String[] keywords;
//
//    @NotEmpty
//    private String[] locations;
//
//    @NotNull
//    private Integer distance;
//
//    @NotNull
//    private Integer maxDaysOld;
//
//    @NotNull
//    private Integer salaryMin;
//
//    private Integer salaryMax;
//
//    @NotBlank
//    private String fullTime;
//
//    @NotBlank
//    private String partTime;
//}


//! when searchFilter is done
//@Service
//public class JobIngestionService {
//
//    private final SearchFilterService searchFilterService;
//    private final AdzunaJobSource adzunaJobSource;
//    private final JobVacancyService jobVacancyService;
//
//    public void ingest() {
//
//        List<SearchFilter> filters =
//                searchFilterService.getAllSearchFilters();
//
//        for (SearchFilter filter : filters) {
//
//            List<JobVacancyData> vacancies =
//                    adzunaJobSource.search(filter);
//
//            for (JobVacancyData vacancy : vacancies) {
//
//                jobVacancyService.saveIfNotExists(vacancy);
//            }
//        }
//    }
//}