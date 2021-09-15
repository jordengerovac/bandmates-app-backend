package com.bandmates.application.domain;

import java.util.Map;
import java.util.Set;

public class BOTB {
    private String urlSlug;

    private Set<AppUser> users;

    private Map<Track, AppUser> tracksAdded;

    private Map<Track, Integer> trackVotes;
}
