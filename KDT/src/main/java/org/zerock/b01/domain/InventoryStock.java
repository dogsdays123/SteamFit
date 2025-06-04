package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long isId;

    private String isNum; // 자재수량

    private String isAvailable; // 자재가용수량

    private String isLocation; // 창고 위치

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mCode", nullable = false)
    private Material material; // 자재 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ssId")
    private SupplierStock supplierStock; // 업체 자재 재고 외래키

    public void change(String isNum, String isAvailable, String isLocation, Material material){
        this.material = material;
        this.isNum = isNum;
        this.isAvailable = isAvailable;
        this.isLocation = isLocation;
    }
}
