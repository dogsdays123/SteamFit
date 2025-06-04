package org.zerock.b01.controller;

import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.OrderByListAllDTO;
import org.zerock.b01.dto.allDTO.OrderByPdfDTO;
import org.zerock.b01.dto.formDTO.OrderByPdfFormDTO;
import org.zerock.b01.repository.*;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.PageService;
import org.zerock.b01.service.PdfService;
import org.zerock.b01.service.UserByService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestMapping("/document")
public class DocumentController {

    private final OrderByRepository orderByRepository;
    private final DeliveryProcurementPlanRepository deliveryProcurementPlanRepository;
    private final MaterialRepository materialRepository;
    private final SupplierRepository supplierRepository;
    @Value("${org.zerock.upload.readyPlanPath}")
    private String readyPath;

    private final UserByService userByService;
    private final PageService pageService;
    private final SupplierStockRepository supplierStockRepository;
    private final PdfService pdfService;

    @GetMapping("/orderDoc")
    public void orderDocList(PageRequestDTO pageRequestDTO, Model model, @ModelAttribute("userBy") UserByDTO userBy) {

        List<OrderBy> orderByList = orderByRepository.findAll();
        model.addAttribute("orderByList", orderByList);

        List<Material> materialList = materialRepository.findAll();
        model.addAttribute("materialList", materialList);

        List<CurrentStatus> stateList = orderByRepository.findDistinctOrderStates();
        model.addAttribute("stateList", stateList);

        List<Supplier> suppliers = supplierRepository.findSupplierByUIDList(userBy.getUId());

        if (!suppliers.isEmpty()) {
            if (suppliers.size() != 1) {
                throw new IllegalStateException("Supplier UID 중복: 공급업체는 하나만 등록되어야 합니다.");
            }
            Supplier supplier = suppliers.get(0);
            String sName = supplier.getSName();
            model.addAttribute("sName", sName);
        } else {

            model.addAttribute("sName", "");
        }

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<OrderByListAllDTO> responseDTO =
                pageService.orderByWithAll(pageRequestDTO, "obd");

        if(responseDTO.getDtoList() != null) {
            for (OrderByListAllDTO dto : responseDTO.getDtoList()) {
                dto.setLeadTime(supplierStockRepository.findLeadTimeByETC(dto.getSName(), dto.getMCode()));
            }
        }
        List<OrderByListAllDTO> filteredList = new ArrayList<>();
        String sName = (String) model.getAttribute("sName");
        if (responseDTO.getDtoList() != null) {
            for (OrderByListAllDTO dto : responseDTO.getDtoList()) {
                if (!"협력회사".equals(userBy.getUserJob()) || sName.equals(dto.getSName())) {
                    filteredList.add(dto);
                }
            }
        }
        model.addAttribute("filteredList", filteredList);
        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
    }

    @GetMapping("/tStateDoc")
    public void tStateDocList(PageRequestDTO pageRequestDTO, Model model, @ModelAttribute("userBy") UserByDTO userBy) {
        List<OrderBy> orderByList = orderByRepository.findAll();
        model.addAttribute("orderByList", orderByList);

        List<Material> materialList = materialRepository.findAll();
        model.addAttribute("materialList", materialList);

        List<CurrentStatus> stateList = orderByRepository.findDistinctOrderStates();
        model.addAttribute("stateList", stateList);

        supplierRepository.findSupplierByUID(userBy.getUId()).ifPresent(supplier -> {
            String sName = supplier.getSName();
            model.addAttribute("sName", sName);
        });
        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<OrderByListAllDTO> responseDTO =
                pageService.orderByWithAll(pageRequestDTO, "obd");

        List<String> sNameList = new ArrayList<>();
        if(responseDTO.getDtoList() != null) {
            for (OrderByListAllDTO dto : responseDTO.getDtoList()) {
                dto.setLeadTime(supplierStockRepository.findLeadTimeByETC(dto.getSName(), dto.getMCode()));
            }
        }

        List<OrderByListAllDTO> filteredList = new ArrayList<>();
        String sName = (String) model.getAttribute("sName");
        if (responseDTO.getDtoList() != null) {
            for (OrderByListAllDTO dto : responseDTO.getDtoList()) {
                if (!"협력회사".equals(userBy.getUserJob()) || sName.equals(dto.getSName())) {
                    filteredList.add(dto);
                }
            }
        }
        model.addAttribute("filteredList", filteredList);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
    }

    @PostMapping("/pdf/dg")
    public ResponseEntity<byte[]> downloadPurchaseOrderPdf(@RequestBody Map<String, List<String>> pdfs) {
        List<String> obCodes = pdfs.get("pdfs");

        if (obCodes == null || obCodes.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        OrderByPdfFormDTO orderByPdfFormDTO = new OrderByPdfFormDTO();
        List<OrderByPdfDTO> orderByPdfDTOS = new ArrayList<>();

        for (String obCode : obCodes) {
            OrderByPdfDTO orderByPdfDTO = new OrderByPdfDTO();
            OrderBy orderBy = orderByRepository.findByOrderByCode(obCode).orElseThrow();
            orderByPdfDTO.setDppCode(orderBy.getDeliveryProcurementPlan().getDppCode());
            orderByPdfDTO.setONum(orderBy.getONum());
            orderByPdfDTO.setOExpectDate(orderBy.getOExpectDate().toString());
            orderByPdfDTO.setSName(orderBy.getDeliveryProcurementPlan().getSupplier().getSName());
            orderByPdfDTO.setOrderAddress(orderBy.getOrderAddress());
            orderByPdfDTO.setORemarks(orderBy.getORemarks());
            orderByPdfDTO.setPayDate(orderBy.getPayDate());
            orderByPdfDTO.setPayMethod(orderBy.getPayMethod());
            orderByPdfDTO.setPayDocument(orderBy.getPayDocument());
            orderByPdfDTO.setUId(orderBy.getUserBy().getUId());
            orderByPdfDTOS.add(orderByPdfDTO);
        }

        orderByPdfFormDTO.setPdfs(orderByPdfDTOS);

        List<byte[]> pdfList = new ArrayList<>();

        for (OrderByPdfDTO order : orderByPdfFormDTO.getPdfs()) {
            OrderByPdfFormDTO orderByPdfFormDTO2 = new OrderByPdfFormDTO();
            List<OrderByPdfDTO> orderByPdfDTOS2 = new ArrayList<>();
            orderByPdfDTOS2.add(order);
            orderByPdfFormDTO2.setPdfs(orderByPdfDTOS2);
            byte[] pdfBytes = pdfService.createPdf(orderByPdfFormDTO2); // PDF 생성
            pdfList.add(pdfBytes);
        }

        // 여러 개의 PDF를 하나로 결합
        byte[] mergedPdf;
        try {
            mergedPdf = pdfService.mergePdfFiles(pdfList); // mergePdfFiles 메서드 호출
        } catch (IOException | DocumentException e) {
            // 예외가 발생한 경우 적절한 처리를 하세요
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error merging PDFs".getBytes());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("purchase_order.pdf").build());

        return new ResponseEntity<>(mergedPdf, headers, HttpStatus.OK);
    }

    @PostMapping("/pdf/s")
    public ResponseEntity<byte[]> previewOrderPDF(@RequestBody Map<String, List<String>> plans) {
        List<String> obCodes = plans.get("plans");

        if (obCodes == null || obCodes.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        //여기를 supplierPDF에 맞게 수정하면 됨~!
        TransactionStatementDTO transactionStatementDTO = new TransactionStatementDTO();
        List<TransactionItemDTO> transactionItemDTOs = new ArrayList<>();

        for (String obCode : obCodes) {
            TransactionItemDTO transactionItemDTO = new TransactionItemDTO();
            OrderBy orderBy = orderByRepository.findByOrderByCode(obCode).orElseThrow();
            transactionItemDTO.setMaterialName(orderBy.getDeliveryProcurementPlan().getMaterial().getMName());
            transactionItemDTO.setQuantity(orderBy.getONum());
            transactionItemDTO.setUnitPrice(orderBy.getDeliveryProcurementPlan().getMaterial().getMUnitPrice());
            transactionItemDTO.setDueDate(supplierStockRepository.findLeadTimeByETC(orderBy.getDeliveryProcurementPlan().getSupplier().getSName(), orderBy.getDeliveryProcurementPlan().getMaterial().getMName()));
            transactionItemDTO.setWidth(String.valueOf(orderBy.getDeliveryProcurementPlan().getMaterial().getMWidth()));
            transactionItemDTO.setDepth(String.valueOf(orderBy.getDeliveryProcurementPlan().getMaterial().getMDepth()));
            transactionItemDTO.setHeight(String.valueOf(orderBy.getDeliveryProcurementPlan().getMaterial().getMHeight()));
            transactionItemDTO.setSupplier(orderBy.getDeliveryProcurementPlan().getSupplier());
            transactionItemDTOs.add(transactionItemDTO);
        }

        transactionStatementDTO.setPlans(transactionItemDTOs);

        List<byte[]> planList = new ArrayList<>();

        for (TransactionItemDTO order : transactionStatementDTO.getPlans()) {
            TransactionStatementDTO transactionStatementDTO2 = new TransactionStatementDTO();
            List<TransactionItemDTO> transactionItemDTOS2 = new ArrayList<>();
            transactionItemDTOS2.add(order);
            transactionStatementDTO2.setPlans(transactionItemDTOS2);
            byte[] pdfBytes = pdfService.createSupplierPdf(transactionStatementDTO2); // PDF 생성
            planList.add(pdfBytes);
        }

        // 여러 개의 PDF를 하나로 결합
        byte[] mergedPdf;
        try {
            mergedPdf = pdfService.mergePdfFiles(planList); // mergePdfFiles 메서드 호출
        } catch (IOException | DocumentException e) {
            // 예외가 발생한 경우 적절한 처리를 하세요
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error merging PDFs".getBytes());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("purchase_order.pdf").build());

        return new ResponseEntity<>(mergedPdf, headers, HttpStatus.OK);
    }

    @GetMapping("/{obCode}/mNameList")
    @ResponseBody
    public String getMNameByDppCode(@PathVariable String obCode) {
        return deliveryProcurementPlanRepository.findMNameByDppCodeOne(orderByRepository.findByOrderByCode(obCode).orElseThrow().getDeliveryProcurementPlan().getDppCode());
    }
}
