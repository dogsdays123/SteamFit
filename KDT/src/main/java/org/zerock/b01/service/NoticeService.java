package org.zerock.b01.service;

import jakarta.servlet.http.HttpSession;
import org.zerock.b01.domain.Notice;

import java.util.List;

public interface NoticeService {
    List<Notice> getNotice(String uId);
    void readNotice(Long nId);
    void clearNotice(Long nId);
    void clearOldNotice();
    void addNotice(String type);

}
