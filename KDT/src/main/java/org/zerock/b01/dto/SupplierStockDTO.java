package org.zerock.b01.dto;


import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierStockDTO {

    private Long ssId;

    private String ssNum;
    private String ssMinOrderQty;

    private String unitPrice;
    private String leadTime;

    private String mName;
    private String mCode;

    private Long sId;

    private LocalDate regDate;
}
