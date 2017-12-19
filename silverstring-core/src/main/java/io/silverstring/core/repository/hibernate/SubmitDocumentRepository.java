package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.hibernate.SubmitDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SubmitDocumentRepository extends JpaRepository<SubmitDocument, Long> {
    Page<SubmitDocument> findAll(Pageable pageable);
}