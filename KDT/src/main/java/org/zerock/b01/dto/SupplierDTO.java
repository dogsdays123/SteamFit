package org.zerock.b01.dto;

import jakarta.persistence.Entity;
import lombok.*;
import org.zerock.b01.domain.Contract;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDTO {
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
    private Long cCode;
    private String uId;
}
