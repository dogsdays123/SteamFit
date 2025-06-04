package org.zerock.b01.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersistentLogins {  // 클래스 이름을 PascalCase로 변경
    @Id
    private String series; // 고유한 시리즈 값을 ID로 설정

    @Column(nullable = false)  // 사용자 이름
    private String username;

    @Column(nullable = false)  // 로그인 토큰
    private String token;

    @Column(nullable = false)  // 마지막 사용 시간
    private LocalDateTime lastUsed;  // JPA에서는 'last_used'와 같은 snake_case도 잘 처리되지만, camelCase가 더 일반적
}