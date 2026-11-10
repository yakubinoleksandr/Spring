package com.example.lab2.jpa.repository;

import com.example.lab2.jpa.entity.VacancyEntity;
import com.example.lab2.model.EmploymentType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface VacancyJpaRepository extends CrudRepository<VacancyEntity, Long> {
    // Derived Query Method — Spring Data builds the query from the method name.
    List<VacancyEntity> findByEmploymentTypeOrderByCreatedAtDesc(EmploymentType type);

    // JPQL, explicitly written in @Query.
    @Query("select v from VacancyEntity v where lower(v.title) like lower(concat('%', :text, '%')) order by v.id")
    List<VacancyEntity> searchByTitle(@Param("text") String text);

    // JPQL @NamedQuery declared on VacancyEntity.
    List<VacancyEntity> findByMinimumSalary(@Param("minimum") BigDecimal minimum);
}