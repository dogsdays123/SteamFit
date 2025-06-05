package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserByDTO {
    private String uId;
    private String uPw;
    private String uName;
    private String userType;

    private String url;
    private String url2;

    private String role;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

    public void steam(String steamid, String personaname, String avatarfull, String profileurl, String role, LocalDateTime regDate, LocalDateTime modDate) {
        this.uId = steamid;
        this.uName = personaname;
        this.url = avatarfull;
        this.url2 = profileurl;
    }
}
