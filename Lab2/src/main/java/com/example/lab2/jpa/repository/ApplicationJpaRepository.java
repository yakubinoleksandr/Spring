package com.example.lab2.jpa.repository;

import com.example.lab2.jpa.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ApplicationJpaRepository extends CrudRepository<ApplicationEntity, Long> {
    boolean existsByCandidateIdAndVacancyId(Long candidateId, Long vacancyId);

    @Query("select a from ApplicationEntity a join fetch a.vacancy join fetch a.candidate where a.vacancy.id = :vacancyId order by a.id")
    List<ApplicationEntity> findForVacancyWithRelations(@Param("vacancyId") Long vacancyId);
}