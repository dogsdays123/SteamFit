package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @GetMapping("/main")
    public void main() {
        log.info("main");
    }

    @GetMapping("/login")
    public void login() {
        log.info("login");
    }

    @PostMapping("/admin")
    @ResponseBody
    public String generate() throws UserByService.MidExistException {
        if (userByRepository.findAdmin() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 생성된 데이터입니다.");
        }
        testRegister();
        return "firstView/login";
    }

    public void testRegister() {
        UserBy user = UserBy.builder()
                .uId("Admin")
                .uPassword(passwordEncoder.encode("1234"))
                .roleSet(Set.of(MemberRole.ADMIN))
                .build();

        userByService.registerAdmin(user);
    }
}
