package org.zerock.b01.dto.allDTO;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DppListAllDTO {
    private String dppCode;

    private String dppRequireNum;

    private Long dppNum;

    private String mPerPrice;

    private LocalDate dppDate;

    private LocalDateTime dppRegDate;

    private String dppState;

    private String ppCode; // 생산계획코드 외래키

    private String leadTime;

    private String pName;

    private String mName;

    private String sName;

    private String mCode; // 자재 외래키

    private String uId;
}
