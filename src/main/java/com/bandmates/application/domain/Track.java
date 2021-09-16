package com.bandmates.application.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String artist;

    private String songName;

    private String uri;

    private String artwork;

    private String seedId;

    private Integer votes;
}
