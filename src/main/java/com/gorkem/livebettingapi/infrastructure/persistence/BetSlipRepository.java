package com.gorkem.livebettingapi.infrastructure.persistence;


import com.gorkem.livebettingapi.domain.entity.BetSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BetSlipRepository extends JpaRepository<BetSlip, Long> {

    int countByEventId(Long eventId);

    Optional<BetSlip> findByCustomerIdAndEventId(String customerId, Long eventİd);
}
