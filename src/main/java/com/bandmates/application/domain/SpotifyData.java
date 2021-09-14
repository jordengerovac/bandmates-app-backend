package com.bandmates.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String topGenre;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Track> recentTracks;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Track> topTracks;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Artist> topArtists;

    private String spotifyAccessToken;

    private String spotifyRefreshToken;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("spotifyData")
    private Profile profile;
}
