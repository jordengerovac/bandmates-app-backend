package com.bandmates.application.service.impl;

import com.bandmates.application.domain.AppUser;
import com.bandmates.application.domain.BOTB;
import com.bandmates.application.repository.BOTBRepository;
import com.bandmates.application.repository.UserRepository;
import com.bandmates.application.service.BOTBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BOTBServiceImpl implements BOTBService {
    private final BOTBRepository botbRepository;

    private final UserRepository userRepository;

    @Override
    public BOTB saveBOTB(BOTB botb) {
        log.info("Adding new botb {} to database", botb.getId());
        return botbRepository.save(botb);
    }

    @Override
    public BOTB getBOTB(Long botbId) {
        log.info("Fetching botb {} from database", botbId);
        return botbRepository.findById(botbId).get();
    }

    @Override
    public BOTB getBOTBByUrlSlug(String urlSlug) {
        log.info("Fetching botb {} from database", urlSlug);
        return botbRepository.findByUrlSlug(urlSlug);
    }

    @Override
    public List<BOTB> getAllBOTBs() {
        return botbRepository.findAll();
    }

    @Override
    public BOTB updateBOTB(BOTB botb, Long botbId) {
        Optional<BOTB> oldBOTB = botbRepository.findById(botbId);
        if (oldBOTB.isPresent()) {
            if (!botb.getUsers().isEmpty())
                oldBOTB.get().setUsers(botb.getUsers());
            if (botb.getUrlSlug() != null)
                oldBOTB.get().setUrlSlug(botb.getUrlSlug());
            if (botb.getTracksAdded() != null)
                oldBOTB.get().setTracksAdded(botb.getTracksAdded());
            if (botb.getStartDate() != null)
                oldBOTB.get().setStartDate(botb.getStartDate());
            if (botb.getEndDate() != null)
                oldBOTB.get().setEndDate(botb.getEndDate());

            return botbRepository.save(oldBOTB.get());
        }
        return null;
    }

    @Override
    public void deleteBOTB(Long botbId) {
        log.info("Deleting botb {}", botbId);
        BOTB botb = botbRepository.getById(botbId);
        for(String user : botb.getUsers()) {
            AppUser appUser = userRepository.findByUsername(user);
            appUser.setBotb(new HashSet<>());
        }
        botbRepository.deleteById(botbId);
    }

    @Override
    public void addUserToBOTB(Long botbId, String username) {
        log.info("Adding user {} to botb {}", username, botbId);
        Optional<BOTB> botb = botbRepository.findById(botbId);
        if (botb.isPresent()) {
            Set<String> usersSet = botb.get().getUsers();
            usersSet.add(username);
            botb.get().setUsers(usersSet);
        }
        else {
            log.error("BOTB not found");
        }
    }

    @Override
    public void addBOTBToUser(Long botbId, String username) {
        log.info("Adding botb {} to user {}", botbId, username);
        AppUser user = userRepository.findByUsername(username);
        Optional<BOTB> botb = botbRepository.findById(botbId);
        if (botb.isPresent()) {
            Set<String> usersSet = botb.get().getUsers();
            usersSet.add(username);
            botb.get().setUsers(usersSet);

            Set<BOTB> botbSet = user.getBotb();
            botbSet.add(botb.get());
            user.setBotb(botbSet);

            userRepository.save(user);
        }
        else {
            log.error("BOTB not found");
        }
    }
}
