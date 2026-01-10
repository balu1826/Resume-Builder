// com.talentstream.repository.FeedbackFieldRepository
package com.talentstream.repository;

import com.talentstream.entity.FeedbackField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FeedbackFieldRepository extends JpaRepository<FeedbackField, Long> {

    @Transactional
    void deleteByFormId(Long formId);   // <-- add this
}
