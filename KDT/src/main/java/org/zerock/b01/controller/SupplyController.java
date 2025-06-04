package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.OrderByListAllDTO;
import org.zerock.b01.dto.allDTO.PlanListAllDTO;
import org.zerock.b01.dto.formDTO.DppFormDTO;
import org.zerock.b01.dto.formDTO.ProgressInspectionFormDTO;
import org.zerock.b01.repository.*;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '구매부서'))")
@RequestMapping("/supply")
public class SupplyController {

    private final DeliveryRequestService deliveryRequestService;
    private final MaterialRepository materialRepository;
    private final ProgressInspectionRepository progressInspectionRepository;
    @Value("${org.zerock.upload.readyPlanPath}")
    private String readyPath;

    private final OrderByRepository orderByRepository;
    private final UserByService userByService;
    private final PageService pageService;
    private final UserByRepository userByRepository;
    private final SupplierStockRepository supplierStockRepository;
    private final ProgressInspectionService progressInspectionService;
    private final SupplierRepository supplierRepository;

    @GetMapping("/progressInspection")
    public void progressInspection(PageRequestDTO pageRequestDTO, Model model) {
        List<OrderBy> orderByList = orderByRepository.findAll();
        model.addAttribute("orderByList", orderByList);

        List<Supplier> sNameList = supplierRepository.findAll();
        model.addAttribute("sNameList", sNameList);

        List<Material> mNameList = materialRepository.findAll();
        model.addAttribute("mNameList", mNameList);

        List<UserBy> userByList = userByRepository.findAll();
        model.addAttribute("userByList", userByList);

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<OrderByListAllDTO> responseDTO =
                pageService.orderByWithAll(pageRequestDTO, "ps");

        // ✅ leadTime 추가 로직 (Material 기준으로 조회)
        List<OrderByListAllDTO> dtoList = responseDTO.getDtoList();

        if(dtoList != null && !dtoList.isEmpty()) {
            for (OrderByListAllDTO dto : dtoList) {
                String leadTime = supplierStockRepository.findLeadTimeByMCodeSName
                        (dto.getMCode(), dto.getSId());
                log.info("mCode {} / leadTime {}", dto.getMCode(), leadTime);
                if (leadTime != null) {
                    dto.setLeadTime(leadTime);
                } else {
                    dto.setLeadTime("미배정");
                }
            }
        }

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
    }

    @PostMapping("/register")
    public String postPi(@ModelAttribute ProgressInspectionFormDTO form,
                       Model model,
                       RedirectAttributes redirectAttributes,
                       HttpServletRequest request) throws IOException {

        List<ProgressInspectionDTO> psDTOs = form.getPss();
        String message = "";

        for(ProgressInspectionDTO psDTO : psDTOs) {
            if(progressInspectionService.register(psDTO)){
                message = "등록이 완료되었습니다.";
                log.info("Test true ");
            } else {
                message = "이미 등록된 검수입니다.";
                log.info("Test false ");
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:progressInspection";
            }
        }

        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:progressInspection";
    }

    @GetMapping("/requestDelivery")
    public void requestDelivery(PageRequestDTO pageRequestDTO, Model model) {

        log.info("##REQUEST DELIVERY PAGE GET....##");

        List<String> sNameList = orderByRepository.findSupplierNamesDistinct();
        model.addAttribute("sNameList", sNameList);

        List<String> mNameList = orderByRepository.findMaterialNamesDistinct();
        model.addAttribute("mNameList", mNameList);

        List<CurrentStatus> oStateList = orderByRepository.findDistinctOrderStates();
        model.addAttribute("oStateList", oStateList);


        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<OrderByListAllDTO> responseDTO =
                pageService.orderByWithAll(pageRequestDTO, "dr");

        model.addAttribute("selectedSName", pageRequestDTO.getSName() != null ? pageRequestDTO.getSName() : "");
        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedOState", pageRequestDTO.getOState() != null ? pageRequestDTO.getOState() : "");
        log.info("OSTATE### " + oStateList);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
        log.info("##REQUEST DELIVERY ResponseDTO##" + responseDTO);

    }


    @GetMapping("/purchaseOrderStatus")
    public void purchaseOrderStatus() {
        log.info("##PURCHASE ORDER STATUS PAGE GET....##");
    }

    @GetMapping("/transactionStatement")
    public void transactionStatement() {
        log.info("##TRANSACTION STATEMENT PAGE GET....##");
    }

    @PostMapping("/deliveryRequestRegister")
    public String deliveryRequestRegister(DeliveryRequestDTO deliveryRequestDTO, RedirectAttributes redirectAttributes){
        try {
            deliveryRequestService.registerDeliveryRequest(deliveryRequestDTO);
            redirectAttributes.addFlashAttribute("message", "납품 요청이 완료되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/supply/requestDelivery";
    }
}