package org.zerock.b01.dto.allDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserByAllDTO {
    private String uId;
    private String uName;
    private String userJob;
    //유저 랭크에 따라 신청상태가 변경됨
    private String userRank;
    private LocalDateTime modDate;
    private String status;

    private String uPassword;
    private String uAddress;
    private String userType;
    private String uEmail;
    private String uPhone;
    private LocalDate uBirthDay;
}
