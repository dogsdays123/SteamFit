package org.zerock.b01.service;

import com.itextpdf.text.*;

import com.itextpdf.text.pdf.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Supplier;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.PurchaseItemDTO;
import org.zerock.b01.dto.PurchaseOrderRequestDTO;
import org.zerock.b01.dto.TransactionItemDTO;
import org.zerock.b01.dto.TransactionStatementDTO;
import org.zerock.b01.dto.allDTO.OrderByPdfDTO;
import org.zerock.b01.dto.formDTO.OrderByPdfFormDTO;
import org.zerock.b01.repository.DeliveryProcurementPlanRepository;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.SupplierRepository;
import org.zerock.b01.repository.UserByRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.awt.SystemColor.text;

@Log4j2
@Service
public class PdfService {

    @Value("${pdf.font.malgun}")
    private Resource malgunFontResource;

    @Autowired
    MaterialRepository materialRepository;
    @Autowired
    DeliveryProcurementPlanRepository dppRepository;
    @Autowired
    UserByRepository userByRepository;
    @Autowired
    SupplierRepository supplierRepository;

    public BaseFont getKoreanFont() {
        try (InputStream fontStream = malgunFontResource.getInputStream()) {
            byte[] fontBytes = fontStream.readAllBytes();
            return BaseFont.createFont("malgun.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, false, fontBytes, null);
        } catch (Exception e) {
            throw new RuntimeException("한글 폰트 로딩 실패: " + e.getMessage(), e);
        }
    }

    public byte[] createSupplierPdf(TransactionStatementDTO request) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BaseFont baseFont = getKoreanFont();

        Supplier supplier = new Supplier();
        for(TransactionItemDTO t : request.getPlans()){
            supplier = t.getSupplier();
        }

        Supplier dgSup = supplierRepository.findSupplierByUID(userByRepository.findAdmin().getUId()).orElseThrow();

        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            //  PDF 에 작성될 폰트 설정
            Font font = new Font(baseFont, 9);
            Font titleFont = new Font(baseFont, 20, Font.BOLD);
            Font contentTitleFont = new Font(baseFont, 12, Font.BOLD);
            Font contentTitleFont1 = new Font(baseFont, 10, Font.BOLD);
            Font materialInfoFont = new Font(baseFont, 10, Font.NORMAL);

            document.add(new Paragraph(" "));
            PdfPTable materialInfoTableHeader = new PdfPTable(2);
            materialInfoTableHeader.setWidthPercentage(100);

            PdfPCell leftCell = new PdfPCell(new Phrase("", materialInfoFont));
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.setHorizontalAlignment(Element.ALIGN_LEFT); // 왼쪽 정렬

            String today = new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date());
            PdfPCell rightCell = new PdfPCell(new Phrase("발행일 : " + today, materialInfoFont));
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT); // 오른쪽 정렬

            materialInfoTableHeader.addCell(leftCell);
            materialInfoTableHeader.addCell(rightCell);

            document.add(materialInfoTableHeader);

            PdfPTable materialInfoTableHeader1 = new PdfPTable(2);
            materialInfoTableHeader1.setWidthPercentage(100);

            PdfPCell leftCell1 = new PdfPCell(new Phrase("거래일자 : " + today, materialInfoFont));
            leftCell1.setBorder(Rectangle.NO_BORDER);
            leftCell1.setHorizontalAlignment(Element.ALIGN_LEFT); // 왼쪽 정렬

            PdfPCell rightCell1 = new PdfPCell(new Phrase("인수자 : " + dgSup.getSName(), materialInfoFont));
            rightCell1.setBorder(Rectangle.NO_BORDER);
            rightCell1.setHorizontalAlignment(Element.ALIGN_RIGHT); // 오른쪽 정렬

            materialInfoTableHeader1.addCell(leftCell1);
            materialInfoTableHeader1.addCell(rightCell1);

            document.add(new Paragraph(" "));

            Paragraph titleParagraph = new Paragraph("거 래 명 세 서", titleFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(titleParagraph);

            document.add(new Paragraph(" "));
            document.add(materialInfoTableHeader1);
            document.add(new Paragraph(" "));
            PdfPTable supplierInfoTable = new PdfPTable(4);
            supplierInfoTable.setWidthPercentage(100);

            float[] columnWidths = {1.5f, 3.5f, 1.5f, 3.5f};
            supplierInfoTable.setWidths(columnWidths);

            PdfPCell headerCell = new PdfPCell(new Phrase("공급 받는자", contentTitleFont));
            headerCell.setColspan(4); // 열 4개 병합
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setBackgroundColor(new BaseColor(255, 204, 128)); // 배경색은 선택
            headerCell.setPadding(7f); // 여백도 선택
            supplierInfoTable.addCell(headerCell);

            PdfPCell supplierAddressCell = new PdfPCell(new Phrase(dgSup.getSAddress() + " / " + dgSup.getSAddressExtra(), font));
            supplierAddressCell.setColspan(3);
            supplierAddressCell.setHorizontalAlignment(Element.ALIGN_CENTER);   // ← 가운데 정렬
            supplierAddressCell.setVerticalAlignment(Element.ALIGN_MIDDLE);     // ← 수직 가운데 정렬
            supplierAddressCell.setFixedHeight(20f);

            //우리회사 정보
            supplierInfoTable.addCell(createCenteredCell("상호명", font));
            supplierInfoTable.addCell(createCenteredCell(dgSup.getSName(), font));
            supplierInfoTable.addCell(createCenteredCell("사업자등록번호", font));
            supplierInfoTable.addCell(createCenteredCell(dgSup.getSRegNum(), font));
            supplierInfoTable.addCell(createCenteredCell("대표자명", font));
            supplierInfoTable.addCell(createCenteredCell(dgSup.getSExponent(), font));
            supplierInfoTable.addCell(createCenteredCell("업종", font));
            supplierInfoTable.addCell(createCenteredCell(dgSup.getSBusinessArray() + ", " + dgSup.getSBusinessType(), font));
            supplierInfoTable.addCell(createCenteredCell("사업장주소", font));
            supplierInfoTable.addCell(supplierAddressCell);
            supplierInfoTable.addCell(createCenteredCell("전화", font));
            supplierInfoTable.addCell(createCenteredCell(dgSup.getSPhone(), font));
            supplierInfoTable.addCell(createCenteredCell("팩스(FAX)", font));
            supplierInfoTable.addCell(createCenteredCell(dgSup.getSFax(), font));
            supplierInfoTable.addCell(createCenteredCell("직통전화", font));
            supplierInfoTable.addCell(createCenteredCell(dgSup.getSPhoneDirect(), font));
            supplierInfoTable.addCell(createCenteredCell("담당직원", font));
            supplierInfoTable.addCell(createCenteredCell(dgSup.getSManager(), font));
            supplierInfoTable.addCell(createCenteredCell("비고", font));
            supplierInfoTable.addCell(createCenteredCell("", font));

            // 발신자(DG전동) 정보 테이블
            PdfPTable orderInfoTable = new PdfPTable(4);
            orderInfoTable.setWidthPercentage(100);

            float[] columnWidths1 = {1.5f, 3.5f, 1.5f, 3.5f};
            orderInfoTable.setWidths(columnWidths1);

            PdfPCell orderHeaderCell = new PdfPCell(new Phrase("공급자", contentTitleFont));
            orderHeaderCell.setColspan(4); // 열 4개 병합
            orderHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            orderHeaderCell.setBackgroundColor(new BaseColor(173, 216, 230)); // 배경색은 선택
            orderHeaderCell.setPadding(7f); // 여백도 선택
            orderInfoTable.addCell(orderHeaderCell);

            PdfPCell addressCell = new PdfPCell(new Phrase(supplier.getSAddress() + " / " + supplier.getSAddressExtra(), font));
            addressCell.setColspan(3);
            addressCell.setHorizontalAlignment(Element.ALIGN_CENTER);   // ← 가운데 정렬
            addressCell.setVerticalAlignment(Element.ALIGN_MIDDLE);     // ← 수직 가운데 정렬
            addressCell.setFixedHeight(20f);                            // ← 필요 시 높이 조정

            orderInfoTable.addCell(createCenteredCell("회사명", font));
            orderInfoTable.addCell(createCenteredCell(supplier.getSName(), font));
            orderInfoTable.addCell(createCenteredCell("대표자명", font));
            orderInfoTable.addCell(createCenteredCell(supplier.getSExponent(), font));

            orderInfoTable.addCell(createCenteredCell("사업자등록번호", font));
            orderInfoTable.addCell(createCenteredCell(supplier.getSRegNum(), font));
            orderInfoTable.addCell(createCenteredCell("전화", font));
            orderInfoTable.addCell(createCenteredCell(supplier.getSPhone(), font));

            orderInfoTable.addCell(createCenteredCell("사업자소재지", font));
            orderInfoTable.addCell(addressCell);

            orderInfoTable.addCell(createCenteredCell("이메일", font));
            orderInfoTable.addCell(createCenteredCell(supplier.getUserBy().getUEmail(), font));
            orderInfoTable.addCell(createCenteredCell("직통전화", font));
            orderInfoTable.addCell(createCenteredCell(supplier.getSPhoneDirect(), font));

            orderInfoTable.addCell(createCenteredCell("팩스번호", font));
            orderInfoTable.addCell(createCenteredCell(supplier.getSFax(), font));
            orderInfoTable.addCell(createCenteredCell("발주담당자", font));
            orderInfoTable.addCell(createCenteredCell(supplier.getSManager(), font));

            PdfPTable rowTable = new PdfPTable(2);  // 3개의 열을 가진 테이블
            rowTable.setWidthPercentage(100);

            PdfPCell supplierInfoTableCell = new PdfPCell(supplierInfoTable);
            supplierInfoTableCell.setBorder(0);  // 테두리 없애기
            supplierInfoTableCell.setHorizontalAlignment(Element.ALIGN_LEFT);  // 왼쪽 정렬
            supplierInfoTableCell.setVerticalAlignment(Element.ALIGN_TOP);    // 위쪽 정렬
//            supplierInfoTableCell.setPadding(5f);  // 패딩을 적절히 설정
//            supplierInfoTableCell.setFixedHeight(200f); // 고정된 높이 설정
            rowTable.addCell(supplierInfoTableCell);

            PdfPCell orderInfoTableCell = new PdfPCell(orderInfoTable);
            orderInfoTableCell.setBorder(0);  // 테두리 없애기
            orderInfoTableCell.setHorizontalAlignment(Element.ALIGN_LEFT);  // 왼쪽 정렬
            orderInfoTableCell.setVerticalAlignment(Element.ALIGN_TOP);    // 위쪽 정렬
//            orderInfoTableCell.setPadding(5f);  // 패딩을 적절히 설정
//            orderInfoTableCell.setFixedHeight(200f); // 고정된 높이 설정
            rowTable.addCell(orderInfoTableCell);
            document.add(rowTable);

            document.add(new Paragraph(" "));
            PdfPTable materialInfoTableHeader2 = new PdfPTable(1);
            materialInfoTableHeader2.setWidthPercentage(100);

            PdfPCell leftCell2 = new PdfPCell(new Phrase("아래와 같이 발주합니다.", materialInfoFont));
            leftCell2.setBorder(Rectangle.NO_BORDER);
            leftCell2.setHorizontalAlignment(Element.ALIGN_LEFT); // 왼쪽 정렬

            materialInfoTableHeader2.addCell(leftCell2);
            document.add(materialInfoTableHeader2);
            document.add(new Paragraph(" "));
            // 품목 정보 시작 헤더

            BaseColor headerColor = new BaseColor(230, 230, 250); // 연보라
            BaseColor totalColor = new BaseColor(240, 240, 240);  // 연회색

            PdfPTable materialInfoTable = new PdfPTable(7);
            materialInfoTable.setWidthPercentage(100);

            float[] columnWidths2 = {1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 2.5f};
            materialInfoTable.setWidths(columnWidths2);

            PdfPCell headerNameCell = createColoredCell("상품명", contentTitleFont1, headerColor);
            headerNameCell.setColspan(3); //
            // 여백도 선택
            materialInfoTable.addCell(headerNameCell);

            materialInfoTable.addCell(createColoredCell("규격", contentTitleFont1, headerColor));
            materialInfoTable.addCell(createColoredCell("수량", contentTitleFont1, headerColor));
            materialInfoTable.addCell(createColoredCell("단가", contentTitleFont1, headerColor));
            materialInfoTable.addCell(createColoredCell("공급가액(VAT포함)", contentTitleFont1, headerColor));

            for(TransactionItemDTO item : request.getPlans()){
                String standard = item.getWidth() + "x" + item.getDepth() + "x" + item.getHeight();
                // 수량 x 단가
                NumberFormat numberFormat = NumberFormat.getInstance();

                int quantity = Integer.parseInt(item.getQuantity());
                int unitPrice = Integer.parseInt(item.getUnitPrice());
                int total = quantity * unitPrice;

                String formattedQuantity = numberFormat.format(quantity);
                String formattedUnitPrice = numberFormat.format(unitPrice);

                int vat = total / 10; // 10% 부가세
                int sum = total + vat; // VAT 포함 금액

                String formattedTotal = "\\ " + numberFormat.format(total);
                String formattedVat = "\\ " + numberFormat.format(vat);
                String formattedSum = "\\ " + numberFormat.format(sum);

                PdfPCell nameCell = createCenteredCell(item.getMaterialName(), font);
                nameCell.setColspan(3);
                materialInfoTable.addCell(nameCell);

                materialInfoTable.addCell(createCenteredCell(standard, font));
                materialInfoTable.addCell(createCenteredCell(formattedQuantity, font)); // 수량
                materialInfoTable.addCell(createCenteredCell(formattedUnitPrice, font)); // 단가
                materialInfoTable.addCell(createCenteredCell(formattedSum, font)); // 공급가액(VAT)포함)

                PdfPCell nameCell2 = createCenteredCell("", font);
                nameCell2.setColspan(3);
                materialInfoTable.addCell(nameCell2);
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");

                PdfPCell nameCell3 = createCenteredCell("", font);
                nameCell3.setColspan(3);
                materialInfoTable.addCell(nameCell3);
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");

                PdfPCell nameCell4 = createCenteredCell("", font);
                nameCell4.setColspan(3);
                materialInfoTable.addCell(nameCell4);
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");

                PdfPCell nameCell5 = createCenteredCell("", font);
                nameCell5.setColspan(3);
                materialInfoTable.addCell(nameCell5);
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");

                // 아래 합계 정보 (공급가액, 세액, 합계)
                PdfPCell priceSumCell = createColoredCell(formattedSum, contentTitleFont1, headerColor);
                priceSumCell.setColspan(2);

                materialInfoTable.addCell(createColoredCell("공급가액", contentTitleFont1, headerColor));
                materialInfoTable.addCell(createColoredCell(formattedTotal, contentTitleFont1, headerColor));
                materialInfoTable.addCell(createColoredCell("세액(VAT)", contentTitleFont1, headerColor));
                materialInfoTable.addCell(createColoredCell(formattedVat, contentTitleFont1, headerColor));
                materialInfoTable.addCell(createColoredCell("합계", contentTitleFont1, headerColor));
                materialInfoTable.addCell(priceSumCell);



            }


            document.add(materialInfoTable);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    public byte[] createPdf(OrderByPdfFormDTO request) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        UserBy userBy = userByRepository.findById(request.getPdfs().get(0).getUId()).orElseThrow();
        Supplier sup = supplierRepository.findSupplierBySName(request.getPdfs().get(0).getSName());
        BaseFont baseFont = getKoreanFont();
        Supplier dgSup = supplierRepository.findSupplierByUID(userByRepository.findAdmin().getUId()).orElseThrow();


        try {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            //  PDF 에 작성될 폰트 설정
            Font font = new Font(baseFont, 9);
            Font titleFont = new Font(baseFont, 20, Font.BOLD);
            Font contentTitleFont = new Font(baseFont, 12, Font.BOLD);
            Font contentTitleFont1 = new Font(baseFont, 10, Font.BOLD);

            //  PDF 헤더 타이틀
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            Paragraph titleParagraph = new Paragraph("구 매 발 주 서", titleFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(titleParagraph);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            //  수신자 (공급업체 정보 테이블)
            PdfPTable supplierInfoTable = new PdfPTable(4);
            supplierInfoTable.setWidthPercentage(100);

            float[] columnWidths = {1.5f, 3.5f, 1.5f, 3.5f};
            supplierInfoTable.setWidths(columnWidths);

            PdfPCell headerCell = new PdfPCell(new Phrase("수신자 정보", contentTitleFont));
            headerCell.setColspan(4); // 열 4개 병합
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setBackgroundColor(new BaseColor(230, 230, 250)); // 배경색은 선택
            headerCell.setPadding(7f); // 여백도 선택
            supplierInfoTable.addCell(headerCell);

            PdfPCell supplierAddressCell = new PdfPCell(new Phrase(sup.getSAddress() + " / " + sup.getSAddressExtra(), font));
            supplierAddressCell.setColspan(3);
            supplierAddressCell.setHorizontalAlignment(Element.ALIGN_CENTER);   // ← 가운데 정렬
            supplierAddressCell.setVerticalAlignment(Element.ALIGN_MIDDLE);     // ← 수직 가운데 정렬
            supplierAddressCell.setFixedHeight(20f);

            supplierInfoTable.addCell(createCenteredCell("상호명", font));
            supplierInfoTable.addCell(createCenteredCell(sup.getSName(), font));
            supplierInfoTable.addCell(createCenteredCell("사업자등록번호", font));
            supplierInfoTable.addCell(createCenteredCell(sup.getSRegNum(), font));
            supplierInfoTable.addCell(createCenteredCell("대표자명", font));
            supplierInfoTable.addCell(createCenteredCell(sup.getSExponent(), font));
            supplierInfoTable.addCell(createCenteredCell("업종", font));
            supplierInfoTable.addCell(createCenteredCell(sup.getSBusinessType() + ", " + sup.getSBusinessArray(), font));
            supplierInfoTable.addCell(createCenteredCell("사업장주소", font));
            supplierInfoTable.addCell(supplierAddressCell);
            supplierInfoTable.addCell(createCenteredCell("전화", font));
            supplierInfoTable.addCell(createCenteredCell(sup.getSPhone(), font));
            supplierInfoTable.addCell(createCenteredCell("팩스(FAX)", font));
            supplierInfoTable.addCell(createCenteredCell(sup.getSFax(), font));
            supplierInfoTable.addCell(createCenteredCell("직통전화", font));
            supplierInfoTable.addCell(createCenteredCell(sup.getSPhoneDirect(), font));
            supplierInfoTable.addCell(createCenteredCell("담당직원", font));
            supplierInfoTable.addCell(createCenteredCell(sup.getSManager(), font));
            supplierInfoTable.addCell(createCenteredCell("비고", font));
            supplierInfoTable.addCell(createCenteredCell("", font));

            document.add(supplierInfoTable);
            document.add(new Paragraph(" "));

            // 발신자(DG전동) 정보 테이블
            PdfPTable orderInfoTable = new PdfPTable(4);
            orderInfoTable.setWidthPercentage(100);

            float[] columnWidths1 = {1.5f, 3.5f, 1.5f, 3.5f};
            orderInfoTable.setWidths(columnWidths1);

            PdfPCell orderHeaderCell = new PdfPCell(new Phrase("발주자 정보", contentTitleFont));
            orderHeaderCell.setColspan(4); // 열 4개 병합
            orderHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            orderHeaderCell.setBackgroundColor(new BaseColor(230, 230, 250)); // 배경색은 선택
            orderHeaderCell.setPadding(7f); // 여백도 선택
            orderInfoTable.addCell(orderHeaderCell);

            PdfPCell addressCell = new PdfPCell(new Phrase(dgSup.getSAddress() + " / " + dgSup.getSAddressExtra(), font));
            addressCell.setColspan(3);
            addressCell.setHorizontalAlignment(Element.ALIGN_CENTER);   // ← 가운데 정렬
            addressCell.setVerticalAlignment(Element.ALIGN_MIDDLE);     // ← 수직 가운데 정렬
            addressCell.setFixedHeight(20f);                            // ← 필요 시 높이 조정

            orderInfoTable.addCell(createCenteredCell("회사명", font));
            orderInfoTable.addCell(createCenteredCell(dgSup.getSName(), font));
            orderInfoTable.addCell(createCenteredCell("대표자명", font));
            orderInfoTable.addCell(createCenteredCell(dgSup.getSExponent(), font));

            orderInfoTable.addCell(createCenteredCell("사업자등록번호", font));
            orderInfoTable.addCell(createCenteredCell(dgSup.getSRegNum(), font));
            orderInfoTable.addCell(createCenteredCell("전화", font));
            orderInfoTable.addCell(createCenteredCell(dgSup.getSPhone(), font));

            orderInfoTable.addCell(createCenteredCell("사업자소재지", font));
            orderInfoTable.addCell(addressCell);

            orderInfoTable.addCell(createCenteredCell("이메일", font));
            orderInfoTable.addCell(createCenteredCell(dgSup.getUserBy().getUEmail(), font));
            orderInfoTable.addCell(createCenteredCell("직통전화", font));
            orderInfoTable.addCell(createCenteredCell(dgSup.getSPhoneDirect(), font));

            orderInfoTable.addCell(createCenteredCell("팩스번호", font));
            orderInfoTable.addCell(createCenteredCell(dgSup.getSFax(), font));
            orderInfoTable.addCell(createCenteredCell("담당자", font));
            orderInfoTable.addCell(createCenteredCell(dgSup.getSManager(), font));

            document.add(orderInfoTable);
            document.add(new Paragraph(" "));

            // 품목 정보 시작 헤더
            Font materialInfoFont = new Font(baseFont, 10, Font.NORMAL);

            PdfPTable materialInfoTableHeader = new PdfPTable(2);
            materialInfoTableHeader.setWidthPercentage(100);

            PdfPCell leftCell = new PdfPCell(new Phrase("아래와 같이 발주합니다", materialInfoFont));
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.setHorizontalAlignment(Element.ALIGN_LEFT); // 왼쪽 정렬

            String today = new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date());
            PdfPCell rightCell = new PdfPCell(new Phrase("발주일자 : " + today, materialInfoFont));
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT); // 오른쪽 정렬

            materialInfoTableHeader.addCell(leftCell);
            materialInfoTableHeader.addCell(rightCell);

            document.add(materialInfoTableHeader);
            document.add(new Paragraph(" "));

            // 품목 테이블

            BaseColor headerColor = new BaseColor(230, 230, 250); // 연보라
            BaseColor totalColor = new BaseColor(240, 240, 240);  // 연회색

            PdfPTable materialInfoTable = new PdfPTable(7);
            materialInfoTable.setWidthPercentage(100);

            float[] columnWidths2 = {1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 2.5f};
            materialInfoTable.setWidths(columnWidths2);

            PdfPCell headerNameCell = createColoredCell("상품명", contentTitleFont1, headerColor);
            headerNameCell.setColspan(3); //
             // 여백도 선택
            materialInfoTable.addCell(headerNameCell);

            materialInfoTable.addCell(createColoredCell("규격", contentTitleFont1, headerColor));
            materialInfoTable.addCell(createColoredCell("수량", contentTitleFont1, headerColor));
            materialInfoTable.addCell(createColoredCell("단가", contentTitleFont1, headerColor));
            materialInfoTable.addCell(createColoredCell("공급가액(VAT포함)", contentTitleFont1, headerColor));

            for (OrderByPdfDTO item : request.getPdfs()){
                log.info("%%% " + item.getDppCode());
                Material m  = materialRepository.findById(dppRepository.findById(item.getDppCode()).orElseThrow().getMaterial().getMCode()).orElseThrow();
                String standard = m.getMWidth() + "x" + m.getMDepth() + "x" + m.getMHeight();
                // 수량 x 단가
                NumberFormat numberFormat = NumberFormat.getInstance();

                int quantity = Integer.parseInt(item.getONum()); // 문자열을 숫자로 먼저 변환
                int unitPrice = Integer.parseInt(m.getMUnitPrice());

                int total = quantity * unitPrice;

                String formattedQuantity = numberFormat.format(quantity);
                String formattedUnitPrice = numberFormat.format(unitPrice);

                int vat = total / 10; // 10% 부가세
                int sum = total + vat; // VAT 포함 금액

                String formattedTotal = "\\ " + numberFormat.format(total);
                String formattedVat = "\\ " + numberFormat.format(vat);
                String formattedSum = "\\ " + numberFormat.format(sum);

                PdfPCell nameCell = createCenteredCell(m.getMName(), font);
                nameCell.setColspan(3);
                materialInfoTable.addCell(nameCell);

                // 나머지 셀 - 규격, 수량, 단가, 공급가액 (3줄 예시)
                materialInfoTable.addCell(createCenteredCell(standard, font));
                materialInfoTable.addCell(createCenteredCell(formattedQuantity, font)); // 수량
                materialInfoTable.addCell(createCenteredCell(formattedUnitPrice, font)); // 단가
                materialInfoTable.addCell(createCenteredCell(formattedSum, font)); // 공급가액(VAT)포함)

                PdfPCell nameCell1 = createCenteredCell("", font);
                nameCell1.setColspan(3);
                materialInfoTable.addCell(nameCell1);
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");

                PdfPCell nameCell2 = createCenteredCell("", font);
                nameCell2.setColspan(3);
                materialInfoTable.addCell(nameCell2);
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");
                materialInfoTable.addCell("");

                // 아래 합계 정보 (공급가액, 세액, 합계)
                PdfPCell priceSumCell = createColoredCell(formattedSum, contentTitleFont1, headerColor);
                priceSumCell.setColspan(2);

                materialInfoTable.addCell(createColoredCell("공급가액", contentTitleFont1, headerColor));
                materialInfoTable.addCell(createColoredCell(formattedTotal, contentTitleFont1, headerColor));
                materialInfoTable.addCell(createColoredCell("세액(VAT)", contentTitleFont1, headerColor));
                materialInfoTable.addCell(createColoredCell(formattedVat, contentTitleFont1, headerColor));
                materialInfoTable.addCell(createColoredCell("합계", contentTitleFont1, headerColor));
                materialInfoTable.addCell(priceSumCell);

                PdfPCell deliveryLocationCell = createColoredCell("납품 장소", contentTitleFont1, headerColor);
                deliveryLocationCell.setColspan(2);
                deliveryLocationCell.setMinimumHeight(30f);

                PdfPCell deliveryLocationCellInfo = createCenteredCell(item.getOrderAddress(), font);
                deliveryLocationCellInfo.setColspan(5);
                deliveryLocationCell.setMinimumHeight(30f);

                materialInfoTable.addCell(deliveryLocationCell);
                materialInfoTable.addCell(deliveryLocationCellInfo);

                PdfPCell deliveryDateCell = createColoredCell("희망 입고일", contentTitleFont1, headerColor);
                deliveryDateCell.setColspan(2);
                deliveryLocationCell.setMinimumHeight(5f);

                PdfPCell deliveryDateCellInfo = createCenteredCell(item.getOExpectDate(), font);
                deliveryDateCellInfo.setColspan(5);
                deliveryLocationCell.setMinimumHeight(5f);

                materialInfoTable.addCell(deliveryDateCell);
                materialInfoTable.addCell(deliveryDateCellInfo);

                PdfPCell significantCell = createColoredCell("특이사항(요청사항)", contentTitleFont1, headerColor);
                significantCell.setColspan(2);

                PdfPCell significantCellInfo = createCenteredCell(item.getORemarks(), font);
                significantCellInfo.setColspan(5);

                materialInfoTable.addCell(significantCell);
                materialInfoTable.addCell(significantCellInfo);

                BaseFont baseFont1 = BaseFont.createFont("fonts/NanumGothic-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font font1 = new Font(baseFont1, 10, Font.NORMAL);

                //결제방식
                String payMethod = item.getPayMethod();

                PdfPCell payMethodCell = createColoredCell("결제방법", contentTitleFont1, headerColor);
                payMethodCell.setColspan(2);

                PdfPCell payMethodCellInfo = createCenteredCell(payMethod, font1);
                payMethodCellInfo.setColspan(5);

                materialInfoTable.addCell(payMethodCell);
                materialInfoTable.addCell(payMethodCellInfo);

                //세후계산
                String payDocument = item.getPayDocument();

                PdfPCell payDateCell = createColoredCell("결제일자", contentTitleFont1, headerColor);
                payDateCell.setColspan(2);
                payDateCell.setMinimumHeight(5f);

                PdfPCell payDateCellInfo = createCenteredCell(item.getPayDate(), font);
                payDateCellInfo.setColspan(5);
                payDateCellInfo.setMinimumHeight(5f);

                materialInfoTable.addCell(payDateCell);
                materialInfoTable.addCell(payDateCellInfo);

                PdfPCell payDocumentCell = createColoredCell("증빙서류 요청", contentTitleFont1, headerColor);
                payDocumentCell.setColspan(2);

                PdfPCell payDocumentCellInfo = createCenteredCell(payDocument, font1);
                payDocumentCellInfo.setColspan(5);

                materialInfoTable.addCell(payDocumentCell);
                materialInfoTable.addCell(payDocumentCellInfo);

            }


            document.add(materialInfoTable);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    private PdfPCell createCenteredCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); // 가운데 정렬
        cell.setFixedHeight(20f); // 고정 높이 설정 (단위: pt)
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);    // 세로 가운데
        return cell;
    }

    private PdfPCell createHeightCenteredCell(String text, Font font, float height) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); // 가운데 정렬
//        cell.setFixedHeight(20f); // 고정 높이 설정 (단위: pt)
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(height);// 세로 가운데
        return cell;
    }

    private PdfPCell createColoredCell(String text, Font font, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(bgColor);
        cell.setPadding(7f);
        return cell;
    }

    public byte[] mergePdfFiles(List<byte[]> pdfList) throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfContentByte content = writer.getDirectContent();

        for (byte[] pdfBytes : pdfList) {
            PdfReader reader = new PdfReader(pdfBytes);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                document.newPage();
                content.addTemplate(writer.getImportedPage(reader, i), 0, 0);
            }
        }

        document.close();
        return outputStream.toByteArray();
    }
}