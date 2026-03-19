package com.talentstream.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.Prompts;

@Repository
public interface PromptsRepository extends JpaRepository<Prompts, Long> {

    @Query("SELECT p FROM Prompts p WHERE p.feature = :feature")
    Prompts findByFeature(@Param("feature") String feature);

}