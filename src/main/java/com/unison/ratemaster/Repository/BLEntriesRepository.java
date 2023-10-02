package com.unison.ratemaster.Repository;

import com.unison.ratemaster.Entity.BLEntries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BLEntriesRepository extends JpaRepository<BLEntries, String> {
}
