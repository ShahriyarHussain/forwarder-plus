package com.unison.ratemaster.Repository;

import com.unison.ratemaster.Entity.Port;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortRepository extends JpaRepository<Port, String> {
}
