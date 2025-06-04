package org.zerock.b01.dto;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseItemDTO {
    private String mName;
    private String oNum; // 조달수량(요구수량)
    private String oExpectDate; // 조달납기일

    //  자재 공급 업체의 정보
    private String sName; // 공급업체명
}
