package com.talentstream.repository;

import com.talentstream.dto.ApplicantFullDataDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.talentstream.entity.ApplicantMappingHelper;

import java.util.Optional;
@Repository
public interface ResumeRepository extends JpaRepository<ApplicantMappingHelper, Long> {

    @Query(name = "ApplicantFullDataQuery", nativeQuery = true)
    Optional<ApplicantFullDataDTO> findFullApplicantData(Long applicantId);
}
