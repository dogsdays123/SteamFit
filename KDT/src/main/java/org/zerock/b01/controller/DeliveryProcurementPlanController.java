package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.*;
import org.zerock.b01.dto.formDTO.DppFormDTO;
import org.zerock.b01.repository.*;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '구매부서'))")
@RequestMapping("/dpp")
public class DeliveryProcurementPlanController {
    private final UserByService userByService;
    private final ProductService productService;
    private final SupplierService supplierService;
    private final DppService dppService;
    private final ProductionPlanService productionPlanService;
    private final PageService pageService;
    private final MaterialRepository materialRepository;
    private final UserByRepository userByRepository;
    private final DeliveryProcurementPlanRepository deliveryProcurementPlanRepository;
    private final ProductionPlanRepository productionPlanRepository;
    private final SupplierStockRepository supplierStockRepository;
    private final BomRepository bomRepository;
    private final InventoryStockRepository inventoryStockRepository;
    private final InventoryStockService inventoryStockService;
    private final OutputService outputService;
    private final NoticeService noticeService;

    @GetMapping("/dppRegister")
    public void dppRegister(PageRequestDTO pageRequestDTO, Model model) {
        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);

        List<Supplier> supplierList = supplierService.getSupplier();
        model.addAttribute("supplierList", supplierList);

        List<ProductionPlan> ppList = productionPlanService.getPlans();
        model.addAttribute("ppList", ppList);

        List<Material> materialList = materialRepository.findAll();
        model.addAttribute("materialList", materialList);

        List<UserBy> userList = userByRepository.findAll();
        model.addAttribute("userList", userList);

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<PlanListAllDTO> responseDTO =
                pageService.planListWithAll(pageRequestDTO);

        log.info("디피피 {}", responseDTO.getDtoList());

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
    }

    @GetMapping("/dppList")
    public void dppList(PageRequestDTO pageRequestDTO, Model model) {
        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);

        List<Supplier> supplierList = supplierService.getSupplier();
        model.addAttribute("supplierList", supplierList);

        List<ProductionPlan> ppList = productionPlanService.getPlans();
        model.addAttribute("ppList", ppList);

        List<Material> materialList = materialRepository.findAll();
        model.addAttribute("materialList", materialList);

        List<UserBy> userList = userByRepository.findAll();
        model.addAttribute("userList", userList);

        List<DeliveryProcurementPlan> dppList = deliveryProcurementPlanRepository.findAll();
        model.addAttribute("dppList", dppList);

        List<CurrentStatus> dppStateList = deliveryProcurementPlanRepository.findDppState();
        model.addAttribute("dppStateList", dppStateList);

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<DppListAllDTO> responseDTO =
                pageService.dppListWithAll(pageRequestDTO);

        // Null 체크를 추가하여 NullPointerException 방지
        if (responseDTO.getDtoList() != null) {
            for (DppListAllDTO dto : responseDTO.getDtoList()) {
                dto.setLeadTime(supplierStockRepository.findLeadTimeByETC(dto.getSName(), dto.getMCode()));
            }
        }

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
    }

    //Register
    @GetMapping("/{pName}/mComponentType")
    @ResponseBody
    public List<String> getMTypeByPName(@PathVariable String pName) {
        List<String> mComponentTypes = materialRepository.findMComponentTypeByPName(pName);
        return mComponentTypes != null ? mComponentTypes : Collections.emptyList();
    }

    @GetMapping("/{mComponentType}/{pName}/mName")
    @ResponseBody
    public List<String> getMNameByPName(@PathVariable String mComponentType, @PathVariable String pName) {
        List<String> mNames = materialRepository.findMNameByETC(pName, mComponentType);
        return mNames != null ? mNames : Collections.emptyList();
    }

    @GetMapping("/{mName}/mCode")
    @ResponseBody
    public List<String> getMCodeByMName(@PathVariable String mName) {
        List<String> mCodes = materialRepository.findMCodeByMNameList(mName);
        return mCodes != null ? mCodes : Collections.emptyList();
    }

    @GetMapping("/{mCode}/ss")
    @ResponseBody
    public List<String> getSsByMCode(@PathVariable String mCode) {
        List<String> ss = supplierStockRepository.findSNameByMCode(mCode);
        return ss != null ? ss : Collections.emptyList();
    }

    @GetMapping("/{sup}/{mCode}/leadTime")
    @ResponseBody
    public String getLeadTimeByETC(@PathVariable String sup, @PathVariable String mCode) {
        return supplierStockRepository.findLeadTimeByETC(sup, mCode);
    }

    //List
    @GetMapping("/{ppCode}/dppCode")
    @ResponseBody
    public List<String> getDppCodeByPpCode(@PathVariable String ppCode) {
        List<String> dppCodes = deliveryProcurementPlanRepository.findDppCodeByPpCode(ppCode);
        return dppCodes != null ? dppCodes : Collections.emptyList();
    }

    @GetMapping("/{dppCode}/mNameList")
    @ResponseBody
    public List<String> getMNameByDppCode(@PathVariable String dppCode) {
        List<String> mNames = deliveryProcurementPlanRepository.findMNameByDppCode(dppCode);
        return mNames != null ? mNames : Collections.emptyList();
    }

    @GetMapping("/{mCode}/rn")
    @ResponseBody
    public String getRequireNum(@PathVariable String mCode) {
        log.info("Requested mCode: " + mCode); // mCode 확인
        String requireNum = bomRepository.findRequireNumByMaterialCode(mCode);
        if (requireNum == null) {
            log.info("Require Number is null for mCode: " + mCode); // null일 경우에 대한 로그 추가
        } else {
            log.info("Require Number: " + requireNum); // 정상적으로 값이 반환될 경우 로그 추가
        }
        return requireNum != null ? requireNum : "정보 없음";
    }

    @GetMapping("/{mCode}/an")
    @ResponseBody
    public String getAvailableNum(@PathVariable String mCode) {
        log.info("Requested mCode: " + mCode); // mCode 확인
        String availableNum = inventoryStockRepository.findAvailableNumByMaterialCode(mCode);
        if (availableNum == null) {
            log.info("availableNum Number is null for mCode: " + mCode);
        } else {
            log.info("availableNum Number: " + availableNum);
        }
        return availableNum != null ? availableNum : "정보 없음";
    }


    @PostMapping("/register")
    public String register(@RequestParam("uId") String uId,
                           @ModelAttribute DppFormDTO form,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request) throws IOException {



        List<DeliveryProcurementPlanDTO> dppDTOs = form.getDpps();
        log.info("dto$ {}", form.getDpps());

        for(DeliveryProcurementPlanDTO dppDTO : dppDTOs) {
            dppDTO.setUId(uId);
            dppService.registerDpp(dppDTO);
        }

        noticeService.addNotice("dpp");

        redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
        return "redirect:dppRegister";
    }

    @PostMapping("/outPutRegister")
    public String outPutRegisterByDPP(@RequestParam("uId") String uId,
                                      @RequestParam("mCode") Optional<String> mCode,
                           @RequestParam("planCode") Optional<String> planCode,
                           @RequestParam("availableQty") Optional<String> availableQty,
                           @ModelAttribute DppFormDTO form,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request) throws IOException {

        log.info("Received mCode: {}, planCode: {}, releaseQty: {}", mCode.orElse("No mCode"), planCode.orElse("No planCode"), availableQty.orElse("No releaseQty"));

        if (mCode.isPresent() && planCode.isPresent() && availableQty.isPresent()) {
            try {
                int qty = Integer.parseInt(availableQty.get());
                if (qty > 0) {
                    OutPutDTO outPutDTO = new OutPutDTO();
                    outPutDTO.setMCode(mCode.get());
                    outPutDTO.setPpCode(planCode.get());
                    outPutDTO.setOpANum(String.valueOf(qty));
                    outputService.registerOutput(outPutDTO);
                    redirectAttributes.addFlashAttribute("message", "출고가 정상 처리되었습니다.");
                }
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute("error", "출고 수량이 올바르지 않습니다.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "출고 등록에 필요한 정보가 부족합니다.");
        }

        String actualPlanCode = planCode.orElse("DefaultPlanCode");
        String actualReleaseQty = availableQty.orElse("0");

        return "redirect:dppRegister";
    }
}
