package com.example.lab2.repository;

import com.example.lab2.model.Candidate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcClientCandidateDao implements CandidateDao {

    private final JdbcClient jdbcClient;

    private final RowMapper<Candidate> mapper = this::mapCandidate;

    public JdbcClientCandidateDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private Candidate mapCandidate(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new Candidate(
                rs.getLong("id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("skills"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    @Override
    public long create(Candidate candidate) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql("""
                INSERT INTO candidates(full_name, email, skills)
                VALUES (:name, :email, :skills)
                """)
                .param("name", candidate.getFullName())
                .param("email", candidate.getEmail())
                .param("skills", candidate.getSkills())
                .update(keyHolder, "id");

        Number key = keyHolder.getKey();

        if (key == null) {
            throw new IllegalStateException(
                    "PostgreSQL не повернув ID кандидата"
            );
        }

        return key.longValue();
    }

    @Override
    public Optional<Candidate> read(long id) {

        return jdbcClient.sql("""
                SELECT *
                FROM candidates
                WHERE id = :id
                """)
                .param("id", id)
                .query(mapper)
                .optional();
    }

    @Override
    public List<Candidate> readAll() {

        return jdbcClient.sql("""
                SELECT *
                FROM candidates
                ORDER BY id
                """)
                .query(mapper)
                .list();
    }

    // UPDATE
    @Override
    public boolean update(long id, Candidate candidate) {

        int changed = jdbcClient.sql("""
                UPDATE candidates
                SET full_name = :name,
                    email = :email,
                    skills = :skills
                WHERE id = :id
                """)
                .param("name", candidate.getFullName())
                .param("email", candidate.getEmail())
                .param("skills", candidate.getSkills())
                .param("id", id)
                .update();

        return changed > 0;
    }

    @Override
    public boolean delete(long id) {

        jdbcClient.sql("""
                UPDATE vacancies v
                SET applications_count =
                    GREATEST(v.applications_count - related.amount, 0)
                FROM (
                    SELECT vacancy_id, COUNT(*)::integer AS amount
                    FROM applications
                    WHERE candidate_id = :id
                    GROUP BY vacancy_id
                ) related
                WHERE v.id = related.vacancy_id
                """)
                .param("id", id)
                .update();

        return jdbcClient.sql("""
                DELETE FROM candidates
                WHERE id = :id
                """)
                .param("id", id)
                .update() > 0;
    }

    @Override
    public List<Candidate> search(String query) {

        if (query == null || query.isBlank()) {
            return readAll();
        }

        String pattern = "%" + query.trim() + "%";

        return jdbcClient.sql("""
                SELECT *
                FROM candidates
                WHERE LOWER(full_name) LIKE LOWER(:q)
                   OR LOWER(email) LIKE LOWER(:q)
                   OR LOWER(skills) LIKE LOWER(:q)
                ORDER BY id
                """)
                .param("q", pattern)
                .query(mapper)
                .list();
    }
}