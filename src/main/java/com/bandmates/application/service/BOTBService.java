package com.bandmates.application.service;

import com.bandmates.application.domain.BOTB;

import java.util.List;

public interface BOTBService {
    BOTB saveBOTB(BOTB botb);

    BOTB getBOTB(Long botbId);

    BOTB getBOTBByUrlSlug(String urlSlug);

    List<BOTB> getAllBOTBs();

    BOTB updateBOTB(BOTB botb, Long botbId);

    void deleteBOTB(Long botbId);

    void addUserToBOTB(Long botbId, String username);

    void addBOTBToUser(Long botbId, String username);
}
