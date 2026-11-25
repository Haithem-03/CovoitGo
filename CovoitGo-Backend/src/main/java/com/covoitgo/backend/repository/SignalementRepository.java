package com.covoitgo.backend.repository;

import com.covoitgo.backend.model.Signalement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SignalementRepository extends JpaRepository<Signalement, Long> {
    List<Signalement> findAllByOrderByIdSignalementDesc();
}
