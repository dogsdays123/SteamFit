package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.dto.SupplierDTO;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sId;

    private String sName;

    private String sRegNum;

    private String sAddress;

    private String sAddressExtra;

    private String sManager;

    private String sBusinessType;

    private String sBusinessArray;

    private String sPhone;

    private String sFax;

    private String sPhoneDirect;

    private String sExponent;

    private String sAgree;

    private String sStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cCode", nullable = true)
    private Contract sContract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uId", nullable = false)
    private UserBy userBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mCode")
    private Material material;

    public void changeStatus(String status) {
        this.sStatus = status;
    }

    public void modifySupplier(SupplierDTO supplierDTO){
        this.sName = supplierDTO.getSName();
        this.sExponent = supplierDTO.getSExponent();
        this.sManager = supplierDTO.getSManager();
        this.sBusinessType = supplierDTO.getSBusinessType();
        this.sBusinessArray = supplierDTO.getSBusinessArray();
        this.sPhone = supplierDTO.getSPhone();
        this.sFax = supplierDTO.getSFax();
        this.sPhoneDirect = supplierDTO.getSPhoneDirect();
        this.sAddress = supplierDTO.getSAddress();
        this.sAddressExtra = supplierDTO.getSAddressExtra();
    }
}
