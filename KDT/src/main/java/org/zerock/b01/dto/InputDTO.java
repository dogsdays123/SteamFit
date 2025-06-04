package org.zerock.b01.dto;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.DeliveryRequest;
import org.zerock.b01.domain.OrderBy;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputDTO {

    private String ipCode;

    private String ipNum;

    private String ipTrueNum;

    private String ipFalseNum;

    private CurrentStatus ipState;

    private String drCode; // 납품 코드
    private String drNum;
    private String drDate; // 납품 희망일
    private CurrentStatus drState; // 납품 상태

    private String oCode; // 발주코드
    private String oNum; // 발주 수량

    private String mCode; // 자재코드
    private String mName; // 자재명

    private LocalDate regDate;

}
