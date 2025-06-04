package org.zerock.b01.dto.allDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderByPdfDTO {
    @JsonProperty("dppCode")
    private String dppCode;

    @JsonProperty("oNum")
    private String oNum;

    @JsonProperty("oExpectDate")
    private String oExpectDate;

    @JsonProperty("sName")
    private String sName;

    @JsonProperty("orderAddress")
    private String orderAddress;

    @JsonProperty("oRemarks")
    private String oRemarks;

    @JsonProperty("payDate")
    private String payDate;

    @JsonProperty("payMethod")
    private String payMethod;

    @JsonProperty("payDocument")
    private String payDocument;

    @JsonProperty("uId")
    private String uId;
}
