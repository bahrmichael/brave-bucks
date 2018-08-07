package com.bravebucks.eve.repository;

import java.util.List;

import com.bravebucks.eve.domain.RattingEntry;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RattingEntryRepository extends MongoRepository<RattingEntry, String> {
    int countByJournalId(long journalId);

    List<RattingEntry> findByProcessed(boolean processed);
}
