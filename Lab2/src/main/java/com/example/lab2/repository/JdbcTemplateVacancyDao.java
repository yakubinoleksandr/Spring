package com.example.lab2.repository;

import com.example.lab2.model.EmploymentType;
import com.example.lab2.model.Vacancy;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTemplateVacancyDao implements VacancyDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Vacancy> mapper = (rs, rowNum) -> {
        BigDecimal salaryValue = rs.getBigDecimal("salary");

        Double salary = salaryValue == null
                ? null
                : salaryValue.doubleValue();

        return new Vacancy(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("requirements"),
                salary,
                EmploymentType.valueOf(rs.getString("employment_type")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getInt("applications_count")
        );
    };

    public JdbcTemplateVacancyDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // CREATE
    @Override
    public long create(Vacancy vacancy) {

        String sql = """
                INSERT INTO vacancies(
                    title,
                    description,
                    requirements,
                    salary,
                    employment_type
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    new String[]{"id"}
            );

            ps.setString(1, vacancy.getTitle());
            ps.setString(2, vacancy.getDescription());
            ps.setString(3, vacancy.getRequirements());

            if (vacancy.getSalary() == null) {
                ps.setNull(4, Types.NUMERIC);
            } else {
                ps.setBigDecimal(
                        4,
                        BigDecimal.valueOf(vacancy.getSalary())
                );
            }

            ps.setString(5, vacancy.getEmploymentType().name());

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();

        if (key == null) {
            throw new IllegalStateException(
                    "PostgreSQL не повернув ID вакансії"
            );
        }

        return key.longValue();
    }

    @Override
    public Optional<Vacancy> read(long id) {

        List<Vacancy> result = jdbcTemplate.query(
                "SELECT * FROM vacancies WHERE id = ?",
                mapper,
                id
        );

        return result.stream().findFirst();
    }

    @Override
    public List<Vacancy> readAll() {

        return jdbcTemplate.query(
                "SELECT * FROM vacancies ORDER BY id",
                mapper
        );
    }

    @Override
    public boolean update(long id, Vacancy vacancy) {

        String sql = """
                UPDATE vacancies
                SET title = ?,
                    description = ?,
                    requirements = ?,
                    salary = ?,
                    employment_type = ?
                WHERE id = ?
                """;

        int changed = jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, vacancy.getTitle());
            ps.setString(2, vacancy.getDescription());
            ps.setString(3, vacancy.getRequirements());

            if (vacancy.getSalary() == null) {
                ps.setNull(4, Types.NUMERIC);
            } else {
                ps.setBigDecimal(
                        4,
                        BigDecimal.valueOf(vacancy.getSalary())
                );
            }

            ps.setString(5, vacancy.getEmploymentType().name());
            ps.setLong(6, id);

            return ps;
        });

        return changed > 0;
    }

    @Override
    public boolean delete(long id) {

        int changed = jdbcTemplate.update(
                "DELETE FROM vacancies WHERE id = ?",
                id
        );

        return changed > 0;
    }

    @Override
    public List<Vacancy> search(
            String title,
            EmploymentType employmentType
    ) {

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM vacancies WHERE 1=1"
        );

        List<Object> args = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            sql.append(" AND LOWER(title) LIKE LOWER(?)");
            args.add("%" + title.trim() + "%");
        }

        if (employmentType != null) {
            sql.append(" AND employment_type = ?");
            args.add(employmentType.name());
        }

        sql.append(" ORDER BY id");

        return jdbcTemplate.query(
                sql.toString(),
                mapper,
                args.toArray()
        );
    }

    @Override
    public List<Vacancy> searchByKeyword(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return readAll();
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return jdbcTemplate.query(
                """
                SELECT *
                FROM vacancies
                WHERE LOWER(title) LIKE ?
                   OR LOWER(description) LIKE ?
                   OR LOWER(requirements) LIKE ?
                ORDER BY id
                """,
                mapper,
                pattern,
                pattern,
                pattern
        );
    }

    @Override
    public int incrementApplicationsCount(long vacancyId) {

        return jdbcTemplate.update(
                """
                UPDATE vacancies
                SET applications_count = applications_count + 1
                WHERE id = ?
                """,
                vacancyId
        );
    }

    @Override
    public int decrementApplicationsCount(long vacancyId) {

        return jdbcTemplate.update(
                """
                UPDATE vacancies
                SET applications_count =
                    GREATEST(applications_count - 1, 0)
                WHERE id = ?
                """,
                vacancyId
        );
    }
}