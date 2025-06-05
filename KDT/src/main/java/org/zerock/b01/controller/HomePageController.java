package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.SteamService;
import org.zerock.b01.service.UserByService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/homePage")
public class HomePageController {

    private final UserByService userByService;
    private final PasswordEncoder passwordEncoder;
    private final UserByRepository userByRepository;
    private final SteamService steamService;

    @GetMapping("/main")
    public void main() {
        log.info("main");
    }

    @GetMapping("/login/steam")
    public String redirectToSteamLogin() {
        String steamLoginUrl = steamService.generateSteamLoginUrl(); // OpenID 로그인 URL 생성
        return "redirect:" + steamLoginUrl;
    }

    @GetMapping("/login/steam/callback")
    public String handleSteamLogin(
            @RequestParam("openid.claimed_id") String claimedId,
            HttpServletRequest request
    ) {
        

        String steamId = steamService.extractSteamIdFromClaimedId(claimedId); // 64비트 Steam ID
        UserByDTO userByDTO = steamService.getSteamUserProfile(steamId);

        // Spring Security 인증 처리
        UserBySecurityDTO userDetails = userByService.loadUserBySteamId(steamId); // UserDetails 구현체
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 세션에도 저장해서 인증 유지
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        // 세션 저장 or JWT 발급 등
        return "redirect:homePage/main"; // 로그인 후 리디렉션
    }
}
