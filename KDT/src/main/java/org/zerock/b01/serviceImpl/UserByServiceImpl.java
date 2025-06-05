package org.zerock.b01.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.UserByService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class UserByServiceImpl implements UserByService {

    UserByRepository userByRepository;

    public UserBySecurityDTO loadUserBySteamId(String steamId) {
        UserBy user = userByRepository.findByUId(steamId)
                .orElseThrow(() -> new UsernameNotFoundException("Steam ID not found: " + steamId));

        // 권한 변환 (MemberRole -> GrantedAuthority)
        Collection<? extends GrantedAuthority> authorities = user.getRoleSet().stream()
                .map(role -> (GrantedAuthority) () -> "ROLE_" + role.name())
                .collect(Collectors.toSet());

        UserBySecurityDTO userDetails = new UserBySecurityDTO(
                user.getUId(),
                user.getUPw() == null ? "" : user.getUPw(),  // Steam 로그인은 비번 없을 수도 있음
                user.getUName(),
                user.getUserType(),
                authorities
        );

        // OAuth2User의 속성도 세팅 가능
        // userDetails.setProps(Map.of("steamId", steamId)); // 필요하면 추가

        return userDetails;
    }
}
