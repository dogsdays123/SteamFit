package org.zerock.b01.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract extends BaseEntity {

    @Id
    private String cCode;

    //부품 이름을 명확히 기재하여 주십시오
    private String cMaterialName;

    private String cSize;

    private String cUintPrice;

    private String cTax;

    private String cSignA;

    private String cSignB;

    private LocalDate cDate;
}
