package org.zerock.b01.serviceImpl;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Notice;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.repository.NoticeRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.NoticeService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserByRepository userByRepository;

    public List<Notice> getNotice(String uId) {
        return noticeRepository.findByReadUser(uId);  // 읽지 않은 알림만 반환
    }

    public void readNotice(Long nId) {
        Notice notice = noticeRepository.findById(nId).orElseThrow();
        notice.setReadNotice(true);
        noticeRepository.save(notice);
    }

    public void clearNotice(Long nId) {
        noticeRepository.delete(noticeRepository.findById(nId).orElseThrow());  // 해당 사용자의 모든 알림 삭제
    }

    public void clearOldNotice() {
        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(0);
        List<Notice> oldNotices = noticeRepository.findOldNotices(fiveDaysAgo);
        noticeRepository.deleteAll(oldNotices);  // 해당 사용자의 모든 알림 삭제
    }

    public void addNotice(String type) {
        String message = "type에 맞는 변경사항 명시";
        List<String> outType = new ArrayList<>();

        switch (type) {
            case "p": message = "새로운 제품이 등록되었습니다.";
                Collections.addAll(outType, "생산부서", "구매부서", "자재부서");
                break;
            case "m": message = "새로운 부품이 등록되었습니다.";
                Collections.addAll(outType, "생산부서", "구매부서", "자재부서");
                break;
            case "pp": message = "새로운 생산 계획이 등록되었습니다.";
                Collections.addAll(outType, "생산부서", "구매부서", "자재부서");
                break;
            case "ppm": message = "수정된 생산 계획이 있습니다.";
                Collections.addAll(outType, "생산부서", "구매부서", "자재부서");
                break;
            case "b": message = "새로운 BOM이 등록되었습니다.";
                Collections.addAll(outType, "생산부서", "구매부서", "자재부서");
                break;
            case "dpp": message = "새로운 조달 계획이 등록되었습니다.";
                Collections.addAll(outType, "생산부서", "구매부서", "자재부서");
                break;
            case "ob": message = "새로운 구매 발주가 등록되었습니다.";
                Collections.addAll(outType, "생산부서", "구매부서", "자재부서");
                break;
            case "ip": message = "새로운 입고 제품이 등록되었습니다.";
                Collections.addAll(outType, "생산부서", "구매부서", "자재부서");
                break;
            case "i": message = "구상중.";
                Collections.addAll(outType, "생산부서", "구매부서", "자재부서");
                break;
            default:
        }

        List<UserBy> userBys = userByRepository.findByType(outType);

        for (UserBy userBy : userBys) {
            Notice notice = new Notice();
            notice.setUserBy(userBy);
            notice.setMessage(message);
            notice.setReadNotice(false);
            noticeRepository.save(notice);
        }
    }

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    public void deleteOldNotices() {
        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(2);
        List<Notice> oldNotices = noticeRepository.findOldNotices(fiveDaysAgo);
        noticeRepository.deleteAll(oldNotices);
    }
}
