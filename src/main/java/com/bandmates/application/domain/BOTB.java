package com.bandmates.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Map;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BOTB {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String urlSlug;

    @OneToMany(mappedBy = "botb", fetch = FetchType.EAGER)
    private Set<AppUser> users;

    @ElementCollection
    private Set<Track> tracksAdded;
}
