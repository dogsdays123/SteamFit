package org.zerock.b01.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private String pCode;
    private String pName;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private String uName;
}
