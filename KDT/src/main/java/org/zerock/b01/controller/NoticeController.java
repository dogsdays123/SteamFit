package org.zerock.b01.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.domain.Notice;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.NoticeService;
import org.zerock.b01.service.UserByService;

import javax.management.Notification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/notice/read")
    public ResponseEntity<Map<String, String>> readNotice(@RequestBody Map<String, String> nIds) {
        Long nId = Long.valueOf(nIds.get("nId"));
        noticeService.readNotice(nId);
        noticeService.clearNotice(nId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notice/clear")
    public void clearOldNotice(){
        noticeService.clearOldNotice();
    }
}
