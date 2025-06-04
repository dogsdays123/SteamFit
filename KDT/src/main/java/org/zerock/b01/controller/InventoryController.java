package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.*;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

//자재관리 컨트롤러
@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '자재부서'))")
@RequestMapping("/inventory")
public class InventoryController {


    private final MaterialService materialService;
    private final PageService pageService;
    private final MaterialRepository materialRepository;
    private final ProductRepository productRepository;
    @Value("${org.zerock.upload.readyPlanPath}")
    private String readyPath;

    private final UserByService userByService;
    private final ProductService productService;
    private final InventoryStockService inventoryStockService;
    private final NoticeService noticeService;

    @GetMapping("/inventoryRegister")
    public String inventoryRegister(Model model){
        log.info("##MATERIAL INVENTORY REGISTER PAGE GET....##");

        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);
        List<Material> materialList = materialService.getMaterials();
        model.addAttribute("materialList", materialList);
        return "inventory/inventoryRegister";

    }

    @PostMapping("/inventoryRegister")
    public String inventoryRegisterPost(String uId,
                                        @RequestParam("pNames[]") List<String> pNames,
                                        @RequestParam("cTypes[]") List<String> cTypes,
                                        @RequestParam("mNames[]") List<String> mNames,
                                        @RequestParam("isNums[]") List<String> isNums,
                                        @RequestParam("isLoca[]") List<String> isLoca,
                                        Model model, RedirectAttributes redirectAttributes,
                                        HttpServletRequest request){

        log.info(" ^^^^ " + uId);

        List<InventoryStockDTO> inventoryStockDTOS = new ArrayList<>();

        for (int i = 0; i < mNames.size(); i++) {
            InventoryStockDTO inventoryStockDTO = new InventoryStockDTO();
            inventoryStockDTO.setMName(mNames.get(i));
            inventoryStockDTO.setMCode(materialRepository.findByMaterialName(mNames.get(i)).orElseThrow().getMCode());

            int parsedNum = Integer.parseInt(isNums.get(i)); // 문자열 → 숫자
            inventoryStockDTO.setIsNum(parsedNum);
            inventoryStockDTO.setIsAvailable(parsedNum);
            inventoryStockDTO.setIsLocation(isLoca.get(i));
            inventoryStockDTOS.add(inventoryStockDTO);

        }

        try {
            for (InventoryStockDTO inventoryStockDTO : inventoryStockDTOS) {
                inventoryStockService.registerIS(inventoryStockDTO); // 중복 시 예외 발생
            }
            redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
            noticeService.addNotice("i");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage()); // 중복 알림
        }

        return "redirect:/inventory/inventoryRegister";

    }



    @GetMapping("/inventoryList")
    public void inventoryList(PageRequestDTO pageRequestDTO, Model model){
        log.info("##MATERIAL INVENTORY LIST PAGE GET....##");

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<InventoryStockDTO> responseDTO = pageService.inventoryStockWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<InventoryStockDTO> inventoryStockList = inventoryStockService.getInventoryStockList();
        model.addAttribute("inventoryStockList", inventoryStockList);
        model.addAttribute("responseDTO", responseDTO);

        log.info("IS List : " + inventoryStockList);
        log.info("IS ResponseDTO : " + responseDTO);

        Set<String> uniquePNames = inventoryStockList.stream()
                .map(InventoryStockDTO::getPName)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        model.addAttribute("pNameList", uniquePNames);

        Set<String> uniqueIsLocation = inventoryStockList.stream()
                        .map(InventoryStockDTO::getIsLocation)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        model.addAttribute("isLocationList", uniqueIsLocation);

        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedCType", pageRequestDTO.getComponentType() != null ? pageRequestDTO.getComponentType() : "");

    }

    @PostMapping("/addInventory")
    public String uploadProductPlan(String uId, @RequestParam("file") MultipartFile[] files, @RequestParam("where") String where, Model model, RedirectAttributes redirectAttributes) throws IOException {

        for (MultipartFile file : files) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            registerInventoryStockOnController(uId, worksheet);
            log.info("%%%%" + worksheet.getSheetName());
        }

        if (where.equals("dataUpload")) {
            redirectAttributes.addFlashAttribute("successMessage", "(특정)데이터 업로드가 성공적으로 완료되었습니다.");
            return "redirect:inventoryRegister";
        } else {
            log.info("데이터넘겨주기");
            redirectAttributes.addFlashAttribute("successMessage", "데이터 업로드가 성공적으로 완료되었습니다.");
            return "redirect:inventoryRegister";
        }
    }


    private void registerInventoryStockOnController(String uId, XSSFSheet worksheet) {
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

            InventoryStockDTO entity = new InventoryStockDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

//            if (row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
//                String productionPlanCode = formatter.formatCellValue(row.getCell(0));
//                log.info("^^^^" + productionPlanCode);
//                entity.setPpCode(productionPlanCode);
//            }
            String productName = formatter.formatCellValue(row.getCell(0));
            String componentType = formatter.formatCellValue(row.getCell(1));
            String materialName = formatter.formatCellValue(row.getCell(2));
            String isNum = formatter.formatCellValue(row.getCell(3));
            String isAvailable = formatter.formatCellValue(row.getCell(4));
            String isLocation = formatter.formatCellValue(row.getCell(5));

            try {
                Optional<String> optionalPCode = productRepository.findPCodeByPName(productName);
                if (optionalPCode.isEmpty()) {
                    throw new IllegalArgumentException("해당 제품명을 가진 제품 코드가 없습니다: " + productName);
                }
                entity.setPCode(optionalPCode.get());

                // 자재 코드 찾기 (중복 체크 포함)
                List<String> mCodes = materialRepository.findMCodesByMName(materialName); // List로 수정
                if (mCodes.isEmpty()) {
                    throw new IllegalArgumentException("해당 자재명을 가진 자재 코드가 없습니다: " + materialName);
                }

                entity.setMCode(mCodes.get(0)); // 단일 값 설정

                entity.setPName(productName);
                entity.setIsComponentType(componentType);
                entity.setIsNum(Integer.parseInt(isNum));
                entity.setIsAvailable(Integer.parseInt(isAvailable));
                entity.setIsLocation(isLocation);

                inventoryStockService.registerIS(entity);

            } catch (IllegalStateException e) {
                log.warn("중복 자재로 등록되지 않음 (행 {}): {}", i + 1, e.getMessage());
            } catch (IllegalArgumentException e) {
                log.error("유효하지 않은 데이터로 등록 실패 (행 {}): {}", i + 1, e.getMessage());
            } catch (Exception e) {
                log.error("알 수 없는 오류 발생 (행 {}): {}", i + 1, e.getMessage());
            }
        }
    }

    @PostMapping("/modify")
    public String modify(@ModelAttribute InventoryStockDTO inventoryStockDTO, RedirectAttributes redirectAttributes, Long isId) {
        log.info("pp modify post.....#@" + inventoryStockDTO);
        inventoryStockService.modifyIS(inventoryStockDTO, isId);
        redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:inventoryList";
    }

    @PostMapping("/remove")
    public String remove(@ModelAttribute InventoryStockDTO inventoryStockDTO, RedirectAttributes redirectAttributes, @RequestParam List<Long> isIds) {
        log.info("pp remove post.....#@" + inventoryStockDTO);
        inventoryStockService.removeIS(isIds);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:inventoryList";
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
}
