package org.zerock.b01.dto.allDTO;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderByListAllDTO {
    private String oCode;
    private LocalDate dppDate;
    private String oNum;
    private String oTotalPrice;
    private LocalDateTime oRegDate;
    private String sName;
    private String mName;
    private String mCode;
    private String mUnitPrice;
    private Float mDepth;
    private Float mHeight;
    private Float mWidth;
    private Float mWeight;
    private LocalDate oExpectDate;
    private String oState;
    private String leadTime;
    private String uId;
    private Long sId;
}
