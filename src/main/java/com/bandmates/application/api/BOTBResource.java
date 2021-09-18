package com.bandmates.application.api;

import com.bandmates.application.domain.BOTB;
import com.bandmates.application.domain.Track;
import com.bandmates.application.repository.TrackRepository;
import com.bandmates.application.service.BOTBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BOTBResource {
    private final BOTBService botbService;

    private final TrackRepository trackRepository;

    @GetMapping("/botb")
    public ResponseEntity<List<BOTB>> getAllBOTBs() {
        return ResponseEntity.ok(botbService.getAllBOTBs());
    }

    @GetMapping("/botb/{botbId}")
    public ResponseEntity<BOTB> getBOTB(@PathVariable Long botbId) {
        return ResponseEntity.ok(botbService.getBOTB(botbId));
    }

    @GetMapping("/botb/slug/{urlSlug}")
    public ResponseEntity<BOTB> getBOTBByUrlSlug(@PathVariable String urlSlug) {
        return ResponseEntity.ok(botbService.getBOTBByUrlSlug(urlSlug));
    }

    @PostMapping("/botb/create")
    public ResponseEntity<BOTB> createBOTB(@RequestBody BOTB botb) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/botb/create").toUriString());
        return ResponseEntity.created(uri).body(botbService.saveBOTB(botb));
    }

    @PutMapping("/botb/update/{id}")
    public ResponseEntity<BOTB> updateBOTB(@RequestBody BOTB botb, @PathVariable Long id) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/botb/update").toUriString());
        return ResponseEntity.created(uri).body(botbService.updateBOTB(botb, id));
    }

    @PostMapping("/botb/users/create/{username}")
    public ResponseEntity<BOTB> createBOTBForUser(@RequestBody BOTB botb, @PathVariable String username) {
        BOTB createdBOTB = botbService.saveBOTB(botb);
        botbService.addBOTBToUser(createdBOTB.getId(), username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/botb/users/add/{username}/{botbId}")
    public ResponseEntity<?> addUserToBOTB(@PathVariable Long botbId, @PathVariable String username) {
        BOTB fetchedBOTB = botbService.getBOTB(botbId);
        fetchedBOTB.getUsers().add(username);
        botbService.addBOTBToUser(fetchedBOTB.getId(), username);
        botbService.saveBOTB(fetchedBOTB);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/botb/tracks/add/{username}/{botbId}")
    public ResponseEntity<BOTB> addTrackToBOTB(@RequestBody Track track, @PathVariable String username, @PathVariable Long botbId) {
        BOTB fetchedBOTB = botbService.getBOTB(botbId);
        Track newTrack = trackRepository.save(track);
        Map<String, Track> tracksAddedMap = fetchedBOTB.getTracksAdded();
        tracksAddedMap.put(username, newTrack);
        fetchedBOTB.setTracksAdded(tracksAddedMap);
        return ResponseEntity.ok(botbService.saveBOTB(fetchedBOTB));
    }

    @PostMapping("/botb/votes/add/{username}/{seedId}/{botbId}")
    public ResponseEntity<BOTB> voteOnBOTBTrack(@PathVariable String username, @PathVariable String seedId, @PathVariable Long botbId) {
        BOTB fetchedBOTB = botbService.getBOTB(botbId);
        Map<String, String> trackVotesMap = fetchedBOTB.getTrackVotes();
        trackVotesMap.put(username, seedId);
        fetchedBOTB.setTrackVotes(trackVotesMap);
        return ResponseEntity.ok(botbService.saveBOTB(fetchedBOTB));
    }

    @PostMapping("/botb/votes/remove/{username}/{seedId}/{botbId}")
    public ResponseEntity<BOTB> removeVoteFromBOTBTrack(@PathVariable String username, @PathVariable String seedId, @PathVariable Long botbId) {
        BOTB fetchedBOTB = botbService.getBOTB(botbId);
        Map<String, String> trackVotesMap = fetchedBOTB.getTrackVotes();
        trackVotesMap.remove(username, seedId);
        fetchedBOTB.setTrackVotes(trackVotesMap);
        return ResponseEntity.ok(botbService.saveBOTB(fetchedBOTB));
    }

    @DeleteMapping("/botb/{botbId}")
    public ResponseEntity<?> deleteBOTB(@PathVariable Long botbId) {
        botbService.deleteBOTB(botbId);
        return ResponseEntity.ok().build();
    }
}
