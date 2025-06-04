package org.zerock.b01.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDTO {

    private String mCode;

    private String mType;

    private String mName;

    private String mUnitPrice;

    private String mMinNum;

    private Float mDepth;

    private Float mHeight;

    private Float mWidth;

    private Float mWeight;

    private String mComponentType;

    private LocalDate regDate;

    private String pName;

    private String uId;

    public MaterialDTO(String mCode, String mName) {
        this.mCode = mCode;
        this.mName = mName;
    }

}
