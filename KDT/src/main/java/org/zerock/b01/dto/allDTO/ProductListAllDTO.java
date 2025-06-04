package org.zerock.b01.dto.allDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProductListAllDTO {
    private String pCode;
    private String pName; //제품이름
    private String uName; //작성자
    private LocalDate pReg;
}
