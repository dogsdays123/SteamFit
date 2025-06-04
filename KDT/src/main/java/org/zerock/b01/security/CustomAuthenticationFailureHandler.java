package org.zerock.b01.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Log4j2
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String errorMessage = "아이디 또는 비밀번호가 올바르지 않습니다.";

        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디 또는 비밀번호가 올바르지 않습니다.";
        }

        log.warn("로그인 실패: {}", exception.getMessage());

        // 메시지를 쿼리 파라미터로 전달
        response.sendRedirect("/firstView/login?error=true&message=" + URLEncoder.encode(errorMessage, "UTF-8"));
    }
}
