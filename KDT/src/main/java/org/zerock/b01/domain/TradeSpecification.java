package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeSpecification extends BaseEntity {

    @Id
    private Long tsId;

    private Long tsNum;

    private String tsTotalPrice;

    private String tsUnitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId", nullable = false)
    private Supplier supplier; // 공급업체 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mCode", nullable = false)
    private Material material; // 자재 외래키
}
