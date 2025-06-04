package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryProcurementPlan extends BaseEntity {

    @Id
    private String dppCode;

    private String dppRequireNum;

    private Long dppNum;

    private LocalDate dppDate;

    @Enumerated(EnumType.STRING)
    private CurrentStatus dppState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ppCode", nullable = false)
    private ProductionPlan productionPlan; // 생산계획코드 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mCode", nullable = false)
    private Material material; // 자재 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sId", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uId", nullable = false)
    private UserBy userBy;

    public void setOneCode(String dppCode){
        this.dppCode = dppCode;
    }
}
