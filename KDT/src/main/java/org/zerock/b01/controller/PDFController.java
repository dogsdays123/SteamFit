package org.zerock.b01.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.b01.dto.PurchaseOrderRequestDTO;
import org.zerock.b01.dto.TransactionStatementDTO;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.dto.allDTO.OrderByPdfDTO;
import org.zerock.b01.dto.formDTO.OrderByPdfFormDTO;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.ByteArrayOutputStream;
import java.util.Map;


@Log4j2
@RestController
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class PDFController {


    private final ProductService productService;
//    @Value("${org.zerock.upload.readyPath}")
//    private String readyPath;

    private final ProductionPlanService productionPlanService;
    private final UserByService userByService;
    private final PdfService pdfService;

    // DG 전동 구매 발주서 pdf
    @PostMapping("/orderBy/pdf/download")
    public ResponseEntity<byte[]> downloadPurchaseOrderPdf(@RequestBody OrderByPdfFormDTO form) {
        log.info("pdfs: {}", form.getPdfs());

        if(form.getPdfs().isEmpty()){
            return null;
        }

        byte[] pdfBytes = pdfService.createPdf(form); // 여기에 진짜 PDF 생성

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("purchase_order.pdf").build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/orderBy/pdf/preview")
    public ResponseEntity<byte[]> previewPurchaseOrderPdf(@RequestBody OrderByPdfFormDTO form) {
        log.info("pdfsPreview: {}", form.getPdfs());

        if(form.getPdfs().isEmpty()){
            return null;
        }

        // PDF 생성 로직 - 예시로 dummy 데이터 사용
        byte[] pdf = pdfService.createPdf(form); // 전달받은 값으로 PDF 생성

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("purchase_order.pdf").build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    // 협력업체 거래명세서 pdf
    @PostMapping("/supplier/purchase/order/pdf/download")
    public ResponseEntity<byte[]> downloadSupplierPurchaseOrderPdf(@RequestBody TransactionStatementDTO request) {
        byte[] pdfBytes = pdfService.createSupplierPdf(request); // 여기에 진짜 PDF 생성

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("purchase_order.pdf").build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/supplier/purchase/order/pdf/preview")
    public ResponseEntity<byte[]> previewSupplierPurchaseOrderPdf(@RequestBody TransactionStatementDTO request) {
        log.info("## PDF PREVIEW 요청 ##");

        // PDF 생성 로직 - 예시로 dummy 데이터 사용
        byte[] pdf = pdfService.createSupplierPdf(request); // 전달받은 값으로 PDF 생성

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("purchase_order.pdf").build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
