package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBy extends BaseEntity {

    @Id
    private String uId;

    private String uPw;
    private String uName;
    private String userType;

    private String url;
    private String url2;

//    @OneToMany(mappedBy = "userBy", cascade = CascadeType.REMOVE, orphanRemoval = true)
//    private Set<Supplier> suppliers = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<MemberRole> roleSet = new HashSet<>();

    public void addRole(MemberRole role) {
       this.roleSet.add(role);
    }
    public void clearRoles() {
        this.roleSet.clear();
    }
}