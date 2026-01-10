// src/main/java/com/talentstream/repository/ApplicantProjectRepository.java
package com.talentstream.repository;

import com.talentstream.entity.ApplicantProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//ApplicantProjectRepository.java
@Repository
public interface ApplicantProjectRepository extends JpaRepository<ApplicantProject, Long> {

 List<ApplicantProject> findByApplicantId(Long applicantId);

 // NEW: grab the latest project to update
 Optional<ApplicantProject> findTopByApplicantIdOrderByUpdatedAtDesc(Long applicantId);

 // (optional) for sorted lists
 List<ApplicantProject> findByApplicantIdOrderByUpdatedAtDesc(Long applicantId);
}
