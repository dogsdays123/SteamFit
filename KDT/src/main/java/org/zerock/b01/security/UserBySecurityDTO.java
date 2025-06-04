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
    private String uPassword;
    private String uName;
    private String uAddress;
    private String userType;
    private String userJob;
    private String uEmail;
    private String uPhone;
    private LocalDate uBirthDay;
    private String status;

    private Map<String, Object> props;

    public UserBySecurityDTO(String uId, String uPassword, String uName, String uAddress,
                             String userType, String userJob, String uEmail, String uPhone,
                             LocalDate uBirthDay, String status, Collection<? extends GrantedAuthority> authorities) {

        super(uId, uPassword, authorities);

        this.uId = uId;
        this.uPassword = uPassword;
        this.uName = uName;
        this.uAddress = uAddress;
        this.userType = userType;
        this.userJob = userJob;
        this.uEmail = uEmail;
        this.uPhone = uPhone;
        this.uBirthDay = uBirthDay;
        this.status = status;
    }

    @Override
    public Map<String, Object> getAttributes(){
        return this.getProps();
    }

    @Override
    public String getName() {
        return this.uId;
    }
}
