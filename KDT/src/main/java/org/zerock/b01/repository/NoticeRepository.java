package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Notice;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query("select n from Notice n where n.readNotice = false and n.userBy.uId = :uId")
    List<Notice> findByReadUser(@Param("uId") String uId);

    @Query("select n from Notice n where n.userBy.uId = :uId")
    List<Notice> findUser(@Param("uId") String uId);

    @Query("SELECT n FROM Notice n WHERE n.regDate < :cutoffDate")
    List<Notice> findOldNotices(@Param("cutoffDate") LocalDateTime cutoffDate);
}
