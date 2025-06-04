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

    private String uPassword;

    private String uName;

    private String uAddress;

    private String userType;

    private String userJob;

    private String userRank;

    @Column(nullable = false, unique = true)
    private String uEmail;

    private String uPhone;

    private LocalDate uBirthDay;

    private String status;

    @OneToMany(mappedBy = "userBy", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Supplier> suppliers = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<MemberRole> roleSet = new HashSet<>();

    public void changeUPassword(String uPassword) {
        this.uPassword = uPassword;
    }
    public void addRole(MemberRole role) {
       this.roleSet.add(role);
    }
    public void clearRoles() {
        this.roleSet.clear();
    }
    public void changeAll(String uAddress, String uEmail, String uPhone) {
        this.uAddress = uAddress;
        this.uEmail = uEmail;
        this.uPhone = uPhone;
    }
    public void changeETC(String userRank, String status, String userJob) {
        this.userRank = userRank;
        this.status = status;
        this.userJob = userJob;
    }

    public void changeStatus(String status) {
        this.status = status;
    }
}