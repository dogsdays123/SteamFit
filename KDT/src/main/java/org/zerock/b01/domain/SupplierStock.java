package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierStock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ssId;

    private String ssNum; // 공급 수량 (현재 자재 수량)

    private String ssMinOrderQty; // 최소 발주 수량

    private String unitPrice; // 공급 단가

    private String leadTime; // 리드 타임

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId", nullable = false)
    private Supplier supplier; // 공급업체 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mCode", nullable = false)
    private Material material; // 자재 외래키

    public void change(String ssNum, String ssMinOrderQty, String unitPrice, String leadTime) {
        this.ssNum = ssNum;
        this.ssMinOrderQty = ssMinOrderQty;
        this.unitPrice = unitPrice;
        this.leadTime = leadTime;
    }
}
