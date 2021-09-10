package com.bandmates.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String topGenre;

    private String spotifyAccessToken;

    private String spotifyRefreshToken;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("spotifyData")
    private Profile profile;
}
