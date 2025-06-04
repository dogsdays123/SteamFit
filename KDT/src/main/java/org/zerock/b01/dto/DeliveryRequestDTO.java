package org.zerock.b01.dto;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.OrderBy;
import org.zerock.b01.domain.Supplier;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRequestDTO {


    private String drCode; // 납품 코드
    private Integer drNum; // 납품 수량
    private String drDate; // 납품 희망일
    private CurrentStatus drState; // 납품 상태

    private String oCode; // 발주코드
    private String oNum; // 발주 수량
    private String oTotalPrice;

    private Long sId; // 공급업체 ID
    private String sName; // 공급업체 이름

    private String mCode; // 자재코드
    private String mName; // 자재명

    private LocalDate regDate;

}
