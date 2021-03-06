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
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String bio;

    private String location;

    private String instrument;

    // not being used currently
    @Lob
    private String image;

    private String iconName;

    private String iconColour;

    @OneToOne(fetch = FetchType.EAGER)
    private SpotifyData spotifyData;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"profile", "botb"})
    private AppUser user;
}
