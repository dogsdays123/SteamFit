package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assy extends BaseEntity {

    @Id
    private Long aId;

    private String aNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mCode", nullable = false)
    private Material material; // 자재 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pCode", nullable = false)
    private Product product; // 제품 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ppCode", nullable = false)
    private ProductionPlan productionPlan; // 생산 계획 외래키
}
