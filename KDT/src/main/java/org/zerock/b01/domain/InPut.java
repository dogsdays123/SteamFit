package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InPut extends BaseEntity {

    @Id
    private String ipCode;

    private String ipNum;

    private String ipTrueNum;

    private String ipFalseNum;

    @Enumerated(EnumType.STRING)
    private CurrentStatus ipState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drCode", nullable = false)
    private DeliveryRequest deliveryRequest; // 납품 지시 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oCode", nullable = false)
    private OrderBy orderBy; // 발주서 외래키
}
