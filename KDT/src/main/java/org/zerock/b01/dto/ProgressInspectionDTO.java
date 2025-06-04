package org.zerock.b01.dto;

import lombok.*;
import org.zerock.b01.domain.CurrentStatus;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressInspectionDTO {
    private Long psId;
    private String psNum;
    private LocalDate psDate;
    private String uId;
    private String oCode;
    private String psRemarks;
    private Long ssId;
    private LocalDate regDate;
    private String mName;
    private String mCode;
    private CurrentStatus psState;
    private CurrentStatus oState;
}
