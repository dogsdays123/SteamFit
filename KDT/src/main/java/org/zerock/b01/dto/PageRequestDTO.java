package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b01.domain.UserBy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    private String type; //검색의 종류
    private String keyword;

    //공통
    private String uName; //작성자
    private String pName; //제품이름

    //유저
    private String userJob;
    //유저 랭크에 따라 신청상태가 변경됨
    private String userRank;
    private LocalDate uRegDate;
    private String status;
    private String uId;

    //협력업체
    private String sName;
    private String sRegNum;
    private String sBusinessType;
    private String sManager;
    private String sPhone;
    private LocalDate sRegDate;
    private String sStatus;

    //자재
    private String mCode;
    private String componentType;
    private String mName;
    private String mType;

    //창고
    private String isLocation;
    private LocalDate isRegDate;

    //product
    private String pCode;
    private LocalDate pReg;

    //plan
    private String ppCode;
    private String ppNum;
    private LocalDate ppStart;
    private LocalDate ppEnd;
    private String ppState;
    private LocalDate ppRegDate;

    //납품
    private String drCode;
    private String drNum;
    private LocalDate drDate;
    private String drState;

    //발주서
    private LocalDate oRegDate;
    private LocalDate oExpectDate;
    private String oState;
    private String obCode;

    //입고
    private String ipState;

    //반품
    private String rState;

    //출고
    private String opState;

    //dpp
    private String dppCode;
    private Long dppNum;
    private LocalDate dppDate;
    private String dppState;

    //진척검수
    private LocalDate psDate;
    private String psState;

    //업체 창고
    private String leadTime;

    public String[] getTypes() {
        if(type == null || type.isEmpty()) {
            return null;
        }
        return type.split("");
    }

    public Pageable getPageable(String...props) {
        return PageRequest.of(this.page -1, this.size, Sort.by(props).descending());
    }

    private String link;

    public String getLink() {
        if(link == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("page=" + this.page);
            builder.append("&size=" + this.size);

            if(type != null && type.length() > 0) {
                builder.append("&type=" + type);
            }

            if(keyword != null) {
                try{
                    builder.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e){

                }
            }

            link = builder.toString();
        }
        return link;
    }


}
