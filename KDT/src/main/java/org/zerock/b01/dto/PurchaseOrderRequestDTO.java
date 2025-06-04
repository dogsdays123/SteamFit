package org.zerock.b01.dto;

import lombok.*;

import java.util.List;

// PDF 출력용 DTO -- purchaserOrder.html 의 구매 발주서 작성 기능
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderRequestDTO {

    // 구매 요청 등록된 조달 계획의 자재 정보
    private List<PurchaseItemDTO> items;


    private String supplierRegNum; // 사업자등록번호
    private String supplierCEO; // 대표자명
    private String supplierIndustryType; // 공급업체 업종
    private String supplierAddress; // 사업장주소
    private String supplierTel; // 전화
    private String supplierPhone; // 직통전화
    private String supplierFax; // 팩스
    private String supplierAdmin; // 담당직원

    // 추가 입력 사항

    private String unitPrice; // 자재 단가
    private String deliveryLocation; // 납품 장소
    private String deliveryDate; // 희망입고일
    private String significant; // 비고 특이사항
    private String payMethodCash; // 결제 방법 : 계좌이체
    private String payMethodCard; // 결제 방법 : 신용카드결제
    private String payDate;
    private String payDocumentsTex; // 증빙 서류 : 세금계산서
    private String payDocumentsCash; // 증빙 서류 : 현금영수증
}
