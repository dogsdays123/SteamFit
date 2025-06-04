package org.zerock.b01.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderBy extends BaseEntity {

    @Id
    private String oCode;

    private String oNum;

    private String oTotalPrice;

    private LocalDate oExpectDate;

    @Enumerated(EnumType.STRING)
    private CurrentStatus oState;

    private String orderAddress;

    private String oRemarks;

    private String payDate;

    private String payMethod;

    private String payDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uId", nullable = false)
    private UserBy userBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dppCode", nullable = false)
    private DeliveryProcurementPlan deliveryProcurementPlan; // 조달 계획 외래키
}
