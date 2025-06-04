package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutPut extends BaseEntity {

    @Id
    private String opCode;

    private String opANum;

    @Enumerated(EnumType.STRING)
    private CurrentStatus opState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aId")
    private Assy assy; // 조립 구조 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mCode", nullable = false)
    private Material material; // 자재 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ppCode", nullable = false)
    private ProductionPlan productionPlan; // 생산 계획 외래키
}
