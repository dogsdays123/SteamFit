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
public class ProgressInspection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long psId;

    private String psNum;

    private LocalDate psDate;

    private String psRemarks;

    @Enumerated(EnumType.STRING)
    private CurrentStatus psState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oCode", nullable = false)
    private OrderBy orderBy; // 발주서 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ssId", nullable = false)
    private SupplierStock supplierStock; // 자재 외래키
}
