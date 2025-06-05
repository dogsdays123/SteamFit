package org.zerock.b01.service;

import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.security.UserBySecurityDTO;

import java.util.List;

public interface UserByService {
    UserBySecurityDTO loadUserBySteamId(String steamId);
}
