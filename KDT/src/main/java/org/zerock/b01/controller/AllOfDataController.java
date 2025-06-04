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
        if (auth == null || !(auth.getPrincipal() instanceof UserBySecurityDTO)) {
            model.addAttribute("userBy", null);
            return;
        }

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;

        // token.getPrincipal()이 MemberSecurityDTO 타입이라면, 이를 MemberSecurityDTO로 캐스팅
        UserBySecurityDTO principal = (UserBySecurityDTO) token.getPrincipal();
        String username = principal.getUId(); // MemberSecurityDTO에서 사용자 이름 가져오기

        // 일반 로그인 사용자 정보 가져오기
        userByDTO = userByService.readOne(username);

        log.info("##### 일반 로그인 사용자 정보: " + userByDTO);

        if(userByDTO != null) {
            List<Notice> notice = noticeService.getNotice(userByDTO.getUId());

            if (notice.isEmpty()) {
                model.addAttribute("notice", null);
            } else {
                model.addAttribute("notice", notice);
            }

            model.addAttribute("userBy", userByDTO);
            String formattedPhone = formatPhoneNumber(userByDTO.getUPhone());
            model.addAttribute("formattedPhone", formattedPhone);
        }
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
