package org.zerock.b01.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderByDTO {

    private String oCode;
    private String oNum;
    private String oTotalPrice;
    private LocalDate oExpectDate;
    private String oState;
    private String orderAddress;
    private String oRemarks;
    private String payDate;
    private String payMethod;
    private String payDocument;
    private String uId;
    private String dppCode;
}
