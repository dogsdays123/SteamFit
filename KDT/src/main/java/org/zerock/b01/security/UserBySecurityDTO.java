package org.zerock.b01.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
public class UserBySecurityDTO extends User implements OAuth2User {

    private String uId;
    private String uPw;
    private String uName;
    private String userType;

    private String url;
    private String url2;

    private Map<String, Object> props;

    public UserBySecurityDTO(String uId, String uPw, String uName, String userType,
                             Collection<? extends GrantedAuthority> authorities) {

        super(uId, uPw, authorities);

        this.uId = uId;
        this.uPw = uPw;
        this.uName = uName;
        this.userType = userType;
    }

    @Override
    public Map<String, Object> getAttributes(){
        return this.getProps();
    }

    @Override
    public String getName() {
        return this.uId;  // 보통 식별 가능한 고유 ID를 반환
    }
}
