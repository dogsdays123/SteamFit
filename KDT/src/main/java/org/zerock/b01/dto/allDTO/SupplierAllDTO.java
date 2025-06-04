package org.zerock.b01.dto.allDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.b01.domain.Contract;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SupplierAllDTO {
    private String sName;
    private String sRegNum;
    private String sBusinessType;
    private String sManager;
    private String sPhone;
    private LocalDateTime regDate;
    private String sStatus;

    private Long sId;
    private String sAddress;
    private String sAddressExtra;
    private Contract sContract;
    private String sBusinessArray;
    private String sFax;
    private String sPhoneDirect;
    private String sExponent;
    private String sAgree;
    private String uId;
}
