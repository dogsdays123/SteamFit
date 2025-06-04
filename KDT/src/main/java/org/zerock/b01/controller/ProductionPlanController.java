package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.PlanListAllDTO;
import org.zerock.b01.dto.formDTO.ProductionPlanFormDTO;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.ProductionPlanRepository;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '생산부서'))")
@RequestMapping("/productionPlan")
public class ProductionPlanController {

    private final ProductService productService;
    private final PageService pageService;
    private final ProductionPlanRepository productionPlanRepository;
    private final ProductRepository productRepository;

    @Value("${org.zerock.upload.awsPath}")
    private String awsPath;

    private final ProductionPlanService productionPlanService;
    private final NoticeService noticeService;

    @GetMapping("/ppRegister")
    public String register(Model model) {
        log.info("##PP REGISTER PAGE GET....##");
        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);
        log.info("$$$$" + productList);

        // 반환할 뷰 이름을 명시합니다.
        return "productionPlan/ppRegister";
    }

    @GetMapping("/ppOrderPlan")
    public void orderPlan() {
        log.info("##ORDER PLAN PAGE GET....##");
    }

    @GetMapping("/ppList")
    public void planList(PageRequestDTO pageRequestDTO, Model model) {

        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<PlanListAllDTO> responseDTO =
                pageService.planListWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        log.info("테스트 ");
        List<ProductionPlan> planList = productionPlanService.getPlans();
        model.addAttribute("planList", planList);
        model.addAttribute("selectedPPCode", pageRequestDTO.getPpCode() != null ? pageRequestDTO.getPpCode() : "");
        model.addAttribute("selectedPName", pageRequestDTO.getPName() != null ? pageRequestDTO.getPName() : "");
        model.addAttribute("responseDTO", responseDTO);

        log.info("^&^&" + responseDTO);
    }


    @GetMapping("/downloadTemplate/{isTemplate}")
    public ResponseEntity<Resource> download(@PathVariable("isTemplate") boolean isTemplate) {
        try {
            // 실제 파일명은 필요에 따라 조건 처리
            String fileName = "testPlan.xlsx";

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

    //생산계획 직접등록
    @PostMapping("/addProductPlanSelf")
    public String uploadProductPlanSelf(@ModelAttribute ProductionPlanFormDTO form,
                                        Model model, RedirectAttributes redirectAttributes,
                                        HttpServletRequest request) throws IOException {

        List<ProductionPlanDTO> productionPlanDTOs = form.getPlans();

        // pCodes와 pNames 배열을 순회하여 Product 객체를 만들어 products 리스트에 추가
        for (ProductionPlanDTO productionPlanDTO : productionPlanDTOs) {
            productionPlanService.registerProductionPlan(productionPlanDTO, productionPlanDTO.getUId());
        }

        noticeService.addNotice("pp");
        redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
        return "redirect:ppRegister";
    }

    //생산계획 자동 등록
    @PostMapping("/addProductPlan")
    @ResponseBody
    public Map<String, Object> uploadProductPlan(String uId, @RequestParam("file") MultipartFile[] files, @RequestParam("where") String where, Model model, RedirectAttributes redirectAttributes) throws IOException {

        Map<String, Object> response = new HashMap<>();
        String mg = "";

        for (MultipartFile file : files) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            mg = registerProductPlanOnController(uId, worksheet);
        }

        noticeService.addNotice("pp");
        response.put("mg", mg);

        return response;
    }


    private String registerProductPlanOnController(String uId, XSSFSheet worksheet) {
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

            ProductionPlanDTO entity = new ProductionPlanDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            if (row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
                String productName = formatter.formatCellValue(row.getCell(0));
                String pCode = productRepository.findPCodeByPName(productName).orElse(null);
                entity.setPName(productName);

                if(pCode == null) {
                    return productName + "은(는) 등록되지 않은 상품입니다.";
                }

                entity.setPppCode(pCode);
                entity.setPName(productName);
            }

            //날짜 형식이라 포맷을 해줘야함
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            LocalDate productionStartDate = LocalDate.parse(sdf.format(row.getCell(1).getDateCellValue()));
            LocalDate productionEndDate = LocalDate.parse(sdf.format(row.getCell(2).getDateCellValue()));
            Integer productionQuantity = Integer.parseInt(formatter.formatCellValue(row.getCell(3)));

            entity.setPpStart(productionStartDate);
            entity.setPpEnd(productionEndDate);
            entity.setPpNum(productionQuantity);

            String productionPlanCode = productionPlanService.registerProductionPlan(entity, uId);
            log.info("데이터 넘겨주기 2 = " + productionPlanCode);
        }
        return "성공";
    }

    // 생산 계획 목록 수정
    @PostMapping("/modify")
    public String modify(@ModelAttribute ProductionPlanDTO productionPlanDTO,
                         RedirectAttributes redirectAttributes,
                         String uName,
                         HttpSession session) {
        log.info("pp modify post.....#@" + productionPlanDTO);

        // 1. 기존 계획 조회
//        ProductionPlanDTO oldPlan = productionPlanService.getOldPlans(productionPlanDTO.getPpCode());
//
//        // 2. 수량 변경 여부 확인
//        if (oldPlan != null && oldPlan.getPpNum() != productionPlanDTO.getPpNum()) {
//            log.info("🔄 생산 수량 변경 감지됨: {} → {}", oldPlan.getPpNum(), productionPlanDTO.getPpNum());
//
//            // 3. 수량 변경 시 후속 처리 (예: 조달/발주 재검토 요청 or 상태 업데이트)
//            productionPlanService.handlePlanQuantityChange(
//                    productionPlanDTO.getPpCode(),
//                    oldPlan.getPpNum(),
//                    productionPlanDTO.getPpNum(),
//                    uName
//            );
//
//            session.setAttribute("warnMessage", "생산 계획이 변경되었습니다. 이에 따라 관련 조달 계획과 발주 사항에 대한 재검토가 필요합니다.");
//        }

        // 4. 생산 계획 수정 처리
        productionPlanService.modifyProductionPlan(productionPlanDTO, uName);

        // 5. 완료 메시지
        noticeService.addNotice("ppm");
        redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:ppList";
    }

    @PostMapping("/remove")
    public String remove(@ModelAttribute ProductionPlanDTO productionPlanDTO, RedirectAttributes redirectAttributes, @RequestParam List<String> ppCodes) {
        log.info("pp remove post.....#@" + productionPlanDTO);
        productionPlanService.removeProductionPlan(ppCodes);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:ppList";
    }
}
