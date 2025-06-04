package org.zerock.b01.dto;

import lombok.*;
import org.zerock.b01.domain.CurrentStatus;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutPutDTO {

    private String opCode;

    private String opANum;

    private CurrentStatus opState;

    private String mCode; // 자재코드
    private String mName; // 자재명

    private String ppCode; // 자재명

    private LocalDate regDate;

}
