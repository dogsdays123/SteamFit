package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.zerock.b01.domain.Notice;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.NoticeService;
import org.zerock.b01.service.UserByService;

import java.util.List;

@Log4j2
@Controller
@ControllerAdvice
@RequiredArgsConstructor
public class AllOfDataController {

    private final NoticeService noticeService;
    private final UserByService userByService;

    @ModelAttribute
    public void Profile(UserByDTO userByDTO, Model model, Authentication auth, HttpServletRequest request) {
    }


    // 전화번호 포맷팅 메서드
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 11) {
            // 01012341234 -> 010-1234-1234
            return phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 7) + "-" + phoneNumber.substring(7);
        }
        return phoneNumber;  // 예외적인 경우 그대로 반환
    }


}
