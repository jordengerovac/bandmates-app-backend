package com.bandmates.application.domain;

import lombok.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
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

    private String name;

    private String startDate;

    private String endDate;

    @ElementCollection
    private Set<String> users = new HashSet<>();

    // {username, track}
    @ElementCollection
    private Map<String, Track> tracksAdded = new HashMap<>();

    // {username, track}
    @ElementCollection
    private Map<String, Track> trackVotes = new HashMap<>();
}
