package org.zerock.b01.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BomDTO {

    private Long bId;

    @JsonProperty("bRequireNum")
    private String bRequireNum;

    @JsonProperty("mComponentType")
    private String mComponentType;

    @JsonProperty("pName")
    private String pName;

    @JsonProperty("mName")
    private String mName;

    private LocalDate regDate;

    @JsonProperty("uId")
    private String uId;

}
