package dev.dmitriy.job_x_hunter.ingestion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;

@Repository
public class VacancyUpsertRepository {
    private final JdbcTemplate jdbc;
    public VacancyUpsertRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Transactional
    public long upsert(JobVacancyData job) {
        return upsertWithResult(job).id();
    }

    @Transactional
    public UpsertResult upsertWithResult(JobVacancyData job) {
        Instant now = Instant.now();
        return jdbc.queryForObject("""
                INSERT INTO job_vacancy (source, source_job_id, title, url, company, company_location,
                  description, post_date, source_published_at, first_seen_at, last_seen_at,
                  salary_min, salary_max, salary_currency, salary_period, salary_is_predicted)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (source, source_job_id) DO UPDATE SET
                  title = EXCLUDED.title, url = EXCLUDED.url, company = EXCLUDED.company,
                  company_location = EXCLUDED.company_location, description = EXCLUDED.description,
                  source_published_at = EXCLUDED.source_published_at, last_seen_at = EXCLUDED.last_seen_at,
                  salary_min = EXCLUDED.salary_min, salary_max = EXCLUDED.salary_max,
                  salary_currency = EXCLUDED.salary_currency, salary_period = EXCLUDED.salary_period,
                  salary_is_predicted = EXCLUDED.salary_is_predicted
                RETURNING job_vacancy_id, (xmax = 0) AS inserted
                """, (rs, row) -> new UpsertResult(rs.getLong(1), rs.getBoolean(2)), job.source(), job.sourceJobId(), job.title(), job.url(), job.company(),
                job.location(), job.description(), (job.publishedAt() == null ? now : job.publishedAt()).atZone(ZoneOffset.UTC).toLocalDate(),
                job.publishedAt() == null ? null : Timestamp.from(job.publishedAt()), Timestamp.from(now), Timestamp.from(now),
                job.salaryMin(), job.salaryMax(), job.salaryCurrency(), job.salaryPeriod(), job.salaryIsPredicted());
    }
    public record UpsertResult(long id, boolean inserted) { }
}
