package com.gorkem.livebettingapi.infrastructure.persistence;

import com.gorkem.livebettingapi.domain.entity.Event;
import com.gorkem.livebettingapi.domain.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BulletinRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByHomeTeamNameAndAwayTeamName(String homeTeamName, String awayTeamName);

    List<Event> findByStatusIn(Collection<Status> statuses);

    Page<Event> findByStatusIn(List<Status> statuses, Pageable pageable);
}
