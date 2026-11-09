package com.example.lab2.repository;

import com.example.lab2.model.JobApplication;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTemplateApplicationDao implements ApplicationDao {

    private static final String SELECT_JOIN = """
            SELECT
                a.id,
                a.vacancy_id,
                v.title AS vacancy_title,
                a.candidate_id,
                c.full_name AS candidate_name,
                a.cover_letter,
                a.status,
                a.created_at
            FROM applications a
            JOIN vacancies v ON v.id = a.vacancy_id
            JOIN candidates c ON c.id = a.candidate_id
            """;

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<JobApplication> mapper = (rs, rowNum) ->
            new JobApplication(
                    rs.getLong("id"),
                    rs.getLong("vacancy_id"),
                    rs.getString("vacancy_title"),
                    rs.getLong("candidate_id"),
                    rs.getString("candidate_name"),
                    rs.getString("cover_letter"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at").toLocalDateTime()
            );

    public JdbcTemplateApplicationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public JobApplication create(
            long candidateId,
            long vacancyId,
            String coverLetter
    ) {

        String sql = """
                INSERT INTO applications(
                    candidate_id,
                    vacancy_id,
                    cover_letter
                )
                VALUES (?, ?, ?)
                """;

        KeyHolder holder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    new String[]{"id"}
            );

            ps.setLong(1, candidateId);
            ps.setLong(2, vacancyId);

            ps.setString(
                    3,
                    coverLetter == null ? "" : coverLetter
            );

            return ps;
        }, holder);

        Number key = holder.getKey();

        if (key == null) {
            throw new IllegalStateException(
                    "PostgreSQL не повернув ID заявки"
            );
        }

        return read(key.longValue())
                .orElseThrow(() -> new IllegalStateException(
                        "Створену заявку не знайдено"
                ));
    }

    @Override
    public Optional<JobApplication> read(long id) {

        return jdbcTemplate.query(
                SELECT_JOIN + " WHERE a.id = ?",
                mapper,
                id
        ).stream().findFirst();
    }

    @Override
    public List<JobApplication> readAll() {

        return jdbcTemplate.query(
                SELECT_JOIN + " ORDER BY a.id",
                mapper
        );
    }

    @Override
    public List<JobApplication> search(
            Long candidateId,
            Long vacancyId,
            String status
    ) {

        StringBuilder sql = new StringBuilder(SELECT_JOIN)
                .append(" WHERE 1=1");

        List<Object> args = new ArrayList<>();

        if (candidateId != null) {
            sql.append(" AND a.candidate_id = ?");
            args.add(candidateId);
        }

        if (vacancyId != null) {
            sql.append(" AND a.vacancy_id = ?");
            args.add(vacancyId);
        }

        if (status != null && !status.isBlank()) {
            sql.append(" AND a.status = ?");
            args.add(status.trim().toUpperCase());
        }

        sql.append(" ORDER BY a.id");

        return jdbcTemplate.query(
                sql.toString(),
                mapper,
                args.toArray()
        );
    }

    @Override
    public boolean update(
            long id,
            String coverLetter,
            String status
    ) {

        return jdbcTemplate.update(
                """
                UPDATE applications
                SET cover_letter = ?,
                    status = ?
                WHERE id = ?
                """,
                coverLetter == null ? "" : coverLetter,
                status,
                id
        ) > 0;
    }

    @Override
    public boolean delete(long id) {

        return jdbcTemplate.update(
                "DELETE FROM applications WHERE id = ?",
                id
        ) > 0;
    }
}