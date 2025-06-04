package org.zerock.b01.dto;

import lombok.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STRubyAlign;
import org.zerock.b01.domain.Supplier;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionItemDTO {
    private String materialName;
    private String quantity;
//    private String totalPrice;
    private String unitPrice;
    private String dueDate;
    private String width; // 가로
    private String depth; // 깊이
    private String height; // 높이세로 규격 가로x깊이x높이
    private Supplier supplier;
}
