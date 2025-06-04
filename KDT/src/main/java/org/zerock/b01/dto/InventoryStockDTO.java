package org.zerock.b01.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryStockDTO {

    private Long isId;

    private Integer isNum; // 자재수량

    private Integer isAvailable; // 자재가용수량

    private String isLocation; // 창고 위치

    private String isComponentType;

    private String mCode;

    private String mName;

    private String pCode;

    private String pName;

    private String ppCode;

    private LocalDate regDate;

    public InventoryStockDTO(String mCode, String mName, Integer isNum, Integer isAvailable) {
        this.mCode = mCode;
        this.mName = mName;
        this.isNum = isNum;
        this.isAvailable = isAvailable;
    }

}
