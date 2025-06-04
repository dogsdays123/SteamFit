package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest extends BaseEntity {

    @Id
    private String drCode;

    private String drNum;

    private String drDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "dr_state")
    private CurrentStatus drState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oCode", nullable = false)
    private OrderBy orderBy; // 발주서 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId", nullable = false)
    private Supplier supplier; // 공급업체 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mCode", nullable = false)
    private Material material; // 자재 외래키
}
