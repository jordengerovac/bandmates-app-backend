package com.bandmates.application.domain;

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

    private String email;

    private String phone;

    @OneToOne(fetch = FetchType.EAGER)
    private SpotifyData spotifyData;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private AppUser user;
}
