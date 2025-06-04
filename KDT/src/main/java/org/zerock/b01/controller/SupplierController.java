package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.store.Cur;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.OrderByListAllDTO;
import org.zerock.b01.repository.*;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.zerock.b01.domain.QSupplier.supplier;

//협력사(공급업체) 컨트롤러
@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '협력회사'))")
@RequestMapping("/supplier")
public class SupplierController {

    private final MaterialRepository materialRepository;
    private final DeliveryRequestRepository deliveryRequestRepository;

    private final ProductService productService;
    private final SupplierStockService supplierStockService;
    private final PageService pageService;
    private final ProgressInspectionService progressInspectionService;
    private final OrderByService orderByService;
    private final InputService inputService;
    private final DeliveryRequestService deliveryRequestService;
    private final ReturnService returnService;


    @Value("${org.zerock.upload.awsPath}")
    private String awsPath;

    @Value("${org.zerock.upload.readyPlanPath}")
    private String readyPath;

    private final UserByService userByService;
    private final MaterialService materialService;
    private final SupplierService supplierService;
    private final OrderByRepository orderByRepository;
    private final NoticeService noticeService;

    @GetMapping("/purchaseOrderList")
    public void purchaseOrderList(PageRequestDTO pageRequestDTO, Authentication auth,
                                    RedirectAttributes redirectAttributes, Model model) {
        log.info("##SUPPLIER :: PURCHASE ORDER LIST PAGE GET....##");

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        UserBySecurityDTO principal = (UserBySecurityDTO) auth.getPrincipal();
        String uId = principal.getUId();
        String role = principal.getUserJob();

        PageResponseDTO<OrderByListAllDTO> responseDTO;

        if ("관리자".equals(role)) {
            responseDTO = pageService.orderByWithAll(pageRequestDTO, "po");
        } else {
            SupplierDTO supplierDTO = supplierService.findByUserId(uId);
            Long sId = supplierDTO.getSId();
            log.info("#### sId: " + sId);

            responseDTO = pageService.orderByWithSidAll(pageRequestDTO, sId);

        }
        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
        log.info("Supplier OrderBy ResponseDTO : " + responseDTO);

        List<String> mNameList = orderByRepository.findMaterialNamesDistinct();
        model.addAttribute("mNameList", mNameList);

        List<CurrentStatus> drStateList = orderByRepository.findDistinctOrderStates();
        model.addAttribute("drStateList", drStateList);

        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedDrState", pageRequestDTO.getDrState() != null ? pageRequestDTO.getDrState() : "");

    }

    @GetMapping("/sInventoryRegister")
    public String inventoryRegister(Model model) {

        log.info("##SUPPLIER :: REGISTER PAGE GET....##");

        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);
        List<Material> materialList = materialService.getMaterials();
        model.addAttribute("materialList", materialList);
        return "supplier/sInventoryRegister";
    }

    @GetMapping("/api/products/{pName}/component-types")
    @ResponseBody
    public List<String> getComponentTypesByProductCode(@PathVariable String pName) {
        List<String> componentTypes = materialRepository.findComponentTypesByProductName(pName);
        return componentTypes != null ? componentTypes : Collections.emptyList();
    }

    @GetMapping("/api/products/{mComponentType}/{pName}/mName")
    @ResponseBody
    public List<String> getMNamesByETC(@PathVariable String mComponentType, @PathVariable String pName) {
        List<String> mNames = materialRepository.findMNameByETC(pName, mComponentType);
        log.info("dddd {}", mNames);
        return mNames != null ? mNames : Collections.emptyList();
    }


    @GetMapping("/progressInspection")
    public void progressInspection(PageRequestDTO pageRequestDTO, Model model,  Authentication auth) {
        log.info("##SUPPLIER :: PROGRESS INSPECTION PAGE GET....##");

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        UserBySecurityDTO principal = (UserBySecurityDTO) auth.getPrincipal();
        String uId = principal.getUId();
        String role = principal.getUserJob();

        PageResponseDTO<ProgressInspectionDTO> responseDTO;
        List<ProgressInspectionDTO> progressInspectionList;


        if ("관리자".equals(role)) {

            responseDTO = pageService.adminProgressInspectionWithAll(pageRequestDTO);
        } else {

            SupplierDTO supplierDTO = supplierService.findByUserId(uId);
            Long sId = supplierDTO.getSId();
            log.info("#### sId: " + sId);


            responseDTO = pageService.supplierProgressInspectionWithAll(pageRequestDTO, sId);
        }


        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }


        model.addAttribute("responseDTO", responseDTO);
        log.info("Progress Inspection ResponseDTO : " + responseDTO);

        List<String> mNameList = orderByRepository.findMaterialNamesDistinct();
        model.addAttribute("mNameList", mNameList);

        List<CurrentStatus> drStateList = orderByRepository.findDistinctOrderStates();
        model.addAttribute("drStateList", drStateList);

        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedDrState", pageRequestDTO.getDrState() != null ? pageRequestDTO.getDrState() : "");
    }





    @GetMapping("/sInventoryList")
    public void inventoryList(PageRequestDTO pageRequestDTO, Model model,  Authentication auth) {
        log.info("##SUPPLIER :: INVENTORY PAGE GET....##");

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        UserBySecurityDTO principal = (UserBySecurityDTO) auth.getPrincipal();
        String uId = principal.getUId();
        String role = principal.getUserJob();

        PageResponseDTO<SupplierStockDTO> responseDTO;
        List<SupplierStockDTO> supplierStockList;



        if ("관리자".equals(role)) {
            responseDTO = pageService.adminSupplierStockWithAll(pageRequestDTO);
        } else {
            SupplierDTO supplierDTO = supplierService.findByUserId(uId);
            Long sId = supplierDTO.getSId();
            log.info("#### sId: " + sId);

            responseDTO = pageService.supplierStockWithAll(pageRequestDTO, sId);
        }
        List<String> materialNames = supplierStockService.findAllMaterialNames();
        model.addAttribute("materialNames", materialNames);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }
        model.addAttribute("selectedMaterial", pageRequestDTO.getMName());
        model.addAttribute("responseDTO", responseDTO);
        log.info("SStock ResponseDTO : " + responseDTO);
    }

    //  직접 등록
    @PostMapping("/sInventoryRegister")
    public String sStocksRegisterPost(@RequestParam("pNames[]") List<String> pNames,
                                      @RequestParam("mNames[]") List<String> mNames,
                                      @RequestParam("ssNums[]") List<String> ssNums,
                                      @RequestParam("ssMinOrderQty[]") List<String> ssMinOrderQty,
//                                      @RequestParam("unitPrices[]") List<String> unitPrices,
                                      @RequestParam("leadTimes[]") List<String> leadTimes,
                                      Model model, RedirectAttributes redirectAttributes,
                                      HttpServletRequest request){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String uId = ((UserBySecurityDTO) auth.getPrincipal()).getUId();
        SupplierDTO supplierDTO;
        try {
            supplierDTO = supplierService.findByUserId(uId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", "공급업체 회원만 재고 등록이 가능합니다.");
            return "redirect:sInventoryRegister";
        }
        Long sId = supplierDTO.getSId();

        List<SupplierStockDTO> supplierStockDTOS = new ArrayList<>();

        for (int i = 0; i < mNames.size(); i++) {
            SupplierStockDTO supplierStockDTO = new SupplierStockDTO();
            supplierStockDTO.setSId(sId);
            List<String> mCodes = materialRepository.findMCodeByMName(mNames.get(i));
            if (mCodes.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "해당 자재명이 존재하지 않습니다: " + mNames.get(i));
                return "redirect:sInventoryRegister";
            }
            supplierStockDTO.setMCode(mCodes.get(0)); // 첫 번째 mCode 사용
            supplierStockDTO.setSsNum(ssNums.get(i));
            supplierStockDTO.setSsMinOrderQty(ssMinOrderQty.get(i));
            supplierStockDTO.setLeadTime(leadTimes.get(i));
            supplierStockDTOS.add(supplierStockDTO);
        }

        try {
            for (SupplierStockDTO supplierStockDTO : supplierStockDTOS) {
                supplierStockService.registerSStock(supplierStockDTO);
            }
            redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage()); // 중복 알림
        }

        return "redirect:sInventoryRegister";
    }

    @PostMapping("/addSStock")
    @ResponseBody
    public Map<String, Object> SStockRegisterAuto(
            @RequestParam("check") boolean check,
            @RequestParam("file") MultipartFile[] files) throws IOException {

        Map<String, Object> response = new HashMap<>();
        List<String> allMCodes = new ArrayList<>();

        for (MultipartFile file : files) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            Map<String, String[]> temp = registerSStockOnController(worksheet);

            if (check) {
                if (temp.containsKey("mCodes")) {
                    allMCodes.addAll(Arrays.asList(temp.get("mCodes")));
                }
            } else {
                response.put("errorCheck", temp.get("errorCheck"));
                return response;
            }
        }

        response.put("mCodes", allMCodes.toArray(new String[0]));
        return response;
    }


    private Map<String, String[]> registerSStockOnController(XSSFSheet worksheet) {
        List<SupplierStockDTO> supplierStockDTOS = new ArrayList<>();
        List<String> mCodes = new ArrayList<>();
        Map<String, String[]> result = new HashMap<>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String uId = ((UserBySecurityDTO) auth.getPrincipal()).getUId();
        SupplierDTO supplierDTO;
        try {
            supplierDTO = supplierService.findByUserId(uId);
        } catch (IllegalArgumentException e) {
            return Collections.singletonMap("errorCheck", new String[]{"공급업체 회원만 재고 등록이 가능합니다."});
        }

        Long sId = supplierDTO.getSId();

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);
            if (row == null) continue;


            DataFormatter formatter = new DataFormatter();

            String mName = formatter.formatCellValue(row.getCell(0));
            if (mName == null || mName.trim().isEmpty()) {
                System.out.println("엑셀 행 " + i + " : 자재명이 비어있음, 스킵");
                continue;
            }
            mName = mName.trim();
            String mCode = materialService.findCodeByName(mName);

            SupplierStockDTO supplierStockDTO = new SupplierStockDTO();
            supplierStockDTO.setMCode(mCode);
            supplierStockDTO.setMName(mName);
            supplierStockDTO.setSsNum(formatter.formatCellValue(row.getCell(2)));
            supplierStockDTO.setSsMinOrderQty(formatter.formatCellValue(row.getCell(3)));
            supplierStockDTO.setLeadTime(formatter.formatCellValue(row.getCell(4)));
            supplierStockDTO.setSId(sId);

            supplierStockDTOS.add(supplierStockDTO);
            mCodes.add(mCode);
        }

        for (SupplierStockDTO supplierStockDTO : supplierStockDTOS) {
            supplierStockService.registerSStock(supplierStockDTO);
        }

        result.put("mCodes", mCodes.toArray(new String[0]));
        return result;
    }

    @PostMapping("/modify")
    public String modify(@ModelAttribute SupplierStockDTO supplierStockDTO, RedirectAttributes redirectAttributes, Long ssId) {
        log.info("ss modify post.....#@" + supplierStockDTO);
        supplierStockService.modifySupplierStock(supplierStockDTO, ssId);
        redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:sInventoryList";
    }

    @PostMapping("/remove")
    public String remove(@ModelAttribute SupplierStockDTO supplierStockDTO, RedirectAttributes redirectAttributes, @RequestParam List<Long> ssIds) {
        log.info("pp remove post.....#@" + supplierStockDTO);
        supplierStockService.removeSupplierStock(ssIds);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:sInventoryList";
    }

    @PostMapping("/piAgree")
    public String piAgree(@ModelAttribute ProgressInspectionDTO progressInspectionDTO, RedirectAttributes redirectAttributes,  @RequestParam List<Long> psIds) {
        progressInspectionService.piAgree(progressInspectionDTO, psIds);
        noticeService.addNotice("pi");
        redirectAttributes.addFlashAttribute("message", "검수 완료 처리 되었습니다.");
        return "redirect:progressInspection";
    }

    @PostMapping("/piRemove")
    public String piRemove(@ModelAttribute ProgressInspectionDTO progressInspectionDTO, @RequestParam List<Long> psIdss, RedirectAttributes redirectAttributes) {
        log.info("psIds: {}", psIdss);
        progressInspectionService.piRemove(progressInspectionDTO, psIdss);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:progressInspection";
    }

    @PostMapping("/orderReady")
    public String orderReady(@ModelAttribute OrderByDTO orderByDTO, @RequestParam List<String> oCodes, RedirectAttributes redirectAttributes) {
        log.info("psIds: {}", oCodes);
        orderByService.setOrderReady(orderByDTO, oCodes);
        redirectAttributes.addFlashAttribute("message", "납품 준비 완료 처리가 완료되었습니다.");
        return "redirect:purchaseOrderList";
    }

    @GetMapping("/requestDelivery")
    public void requestDelivery(PageRequestDTO pageRequestDTO, Model model,  Authentication auth) {
        log.info("##SUPPLIER :: REQUEST DELIVERY PAGE GET....##");
        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        UserBySecurityDTO principal = (UserBySecurityDTO) auth.getPrincipal();
        String uId = principal.getUId();
        String role = principal.getUserJob();

        PageResponseDTO<DeliveryRequestDTO> responseDTO;

        if ("관리자".equals(role)) {
            responseDTO = pageService.deliveryRequestWithAll(pageRequestDTO);
        } else {

            SupplierDTO supplierDTO = supplierService.findByUserId(uId);
            Long sId = supplierDTO.getSId();
            log.info("#### sId: " + sId);

            responseDTO = pageService.supplierDeliveryRequestWithAll(pageRequestDTO, sId);

        }
        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<String> mNameList = deliveryRequestRepository.findDistinctMaterialNames();
        model.addAttribute("mNameList", mNameList);

        List<CurrentStatus> drStateList = deliveryRequestRepository.findDistinctDrStates();
        model.addAttribute("drStateList", drStateList);

        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedDrState", pageRequestDTO.getDrState() != null ? pageRequestDTO.getDrState() : "");

        model.addAttribute("responseDTO", responseDTO);
        log.info("SDelivery Request ResponseDTO : " + responseDTO);
    }

    @PostMapping("/drAgree")
    public String piAgree(@ModelAttribute DeliveryRequestDTO deliveryRequestDTO, RedirectAttributes redirectAttributes,  @RequestParam List<String> drCodes) {
        try {
            // 서비스 메서드 호출
            deliveryRequestService.drAgree(deliveryRequestDTO, drCodes);
            redirectAttributes.addFlashAttribute("message", "납품 완료 처리 되었습니다.");
        } catch (RuntimeException e) {
            // 예외 발생 시 경고 메시지를 전달
            redirectAttributes.addFlashAttribute("message",  e.getMessage());
        }
        return "redirect:/supplier/requestDelivery";
    }

    @PostMapping("/drRemove")
    public String piRemove(@RequestParam List<String> drCodes, RedirectAttributes redirectAttributes) {
        log.info("psIds: {}", drCodes);
        deliveryRequestService.drRemove(drCodes);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:/supplier/requestDelivery";
    }

    @GetMapping("/transactionHistory")
    public void transactionHistory(PageRequestDTO pageRequestDTO, Model model,  Authentication auth) {
        log.info("##SUPPLIER :: TRANSACTION HISTORY PAGE GET....##");
        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        UserBySecurityDTO principal = (UserBySecurityDTO) auth.getPrincipal();
        String uId = principal.getUId();
        String role = principal.getUserJob();

        PageResponseDTO<DeliveryRequestDTO> responseDTO;

        if ("관리자".equals(role)) {
            responseDTO = pageService.deliveryRequestWithAll(pageRequestDTO);
        } else {

            SupplierDTO supplierDTO = supplierService.findByUserId(uId);
            Long sId = supplierDTO.getSId();
            log.info("#### sId: " + sId);

            responseDTO = pageService.supplierDeliveryRequestWithAll(pageRequestDTO, sId);

        }
        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<String> mNameList = deliveryRequestRepository.findDistinctMaterialNames();
        model.addAttribute("mNameList", mNameList);

        List<CurrentStatus> drStateList = deliveryRequestRepository.findDistinctDrStates();
        model.addAttribute("drStateList", drStateList);

        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedDrState", pageRequestDTO.getDrState() != null ? pageRequestDTO.getDrState() : "");

        model.addAttribute("responseDTO", responseDTO);
        log.info("SDelivery Request ResponseDTO : " + responseDTO);
    }

    @GetMapping("/returnManage")
    public void returnManage(PageRequestDTO pageRequestDTO, Model model,  Authentication auth) {
        log.info("##SUPPLIER :: REQUEST DELIVERY PAGE GET....##");
        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        UserBySecurityDTO principal = (UserBySecurityDTO) auth.getPrincipal();
        String uId = principal.getUId();
        String role = principal.getUserJob();

        PageResponseDTO<ReturnByDTO> responseDTO;

        if ("관리자".equals(role)) {
            responseDTO = pageService.returnByWithAll(pageRequestDTO);
        } else {

            SupplierDTO supplierDTO = supplierService.findByUserId(uId);
            Long sId = supplierDTO.getSId();
            log.info("#### sId: " + sId);

            responseDTO = pageService.supplierReturnByWithAll(pageRequestDTO, sId);

        }
        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        model.addAttribute("responseDTO", responseDTO);
        log.info("SDelivery Request ResponseDTO : " + responseDTO);

        Set<String> mNames = Optional.ofNullable(responseDTO.getDtoList())
                .orElse(Collections.emptyList())
                .stream()
                .map(ReturnByDTO::getMName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

//        Set<CurrentStatus> rStates = responseDTO.getDtoList().stream()
//                .map(ReturnByDTO::getRState)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toSet());

        model.addAttribute("mNames", mNames);
//        model.addAttribute("rStates", rStates);

        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
//        model.addAttribute("selectedRState", pageRequestDTO.getRState() != null ? pageRequestDTO.getRState() : "");

    }

    @PostMapping("/Redelivery")
    public String reDelivery(@ModelAttribute ReturnByDTO returnByDTO,
                             RedirectAttributes redirectAttributes) {


        try {
            returnService.reDelivery(returnByDTO);
            redirectAttributes.addFlashAttribute("message", "재납품 처리가 완료되었습니다.");
        } catch (Exception e) {
            log.error("재납품 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("message", "재납품 처리 중 오류가 발생했습니다.");
        }

        return "redirect:/supplier/returnManage";
    }

    @GetMapping("/downloadTemplate/{isTemplate}")
    public ResponseEntity<Resource> download(@PathVariable("isTemplate") boolean isTemplate) {
        try {
            // 실제 파일명은 필요에 따라 조건 처리
            String fileName = "testSStock.xlsx";

            // classpath에서 리소스 로드
            Resource resource = new ClassPathResource(awsPath + fileName);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/returnRemove")
    public String returnRemove(@RequestParam List<Long> rIds, RedirectAttributes redirectAttributes) {
        log.info("psIds: {}", rIds);
        returnService.removeReturn(rIds);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:/supplier/returnManage";
    }
}
