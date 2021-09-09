package com.bandmates.application.repository;

import com.bandmates.application.domain.SpotifyData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotifyDataRepository extends JpaRepository<SpotifyData, Long> {
}
