package com.bandmates.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ElementCollection
    private Set<String> topGenres = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Track> recentTracks = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Track> topTracks = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Track> recommendedTracks = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Artist> topArtists = new HashSet<>();

    private String spotifyAccessToken;

    private String spotifyRefreshToken;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("spotifyData")
    private Profile profile;
}
