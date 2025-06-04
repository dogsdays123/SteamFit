package org.zerock.b01.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;


// 공급업체 거래명세서 발급 pdf -> supplier/orderByList.html
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionStatementDTO {
    private List<TransactionItemDTO> plans;
}
