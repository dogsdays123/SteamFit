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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.Bom;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.Supplier;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.formDTO.MaterialFormDTO;
import org.zerock.b01.dto.formDTO.ProductionPlanFormDTO;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

//자재관리 컨트롤러
@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '생산부서'))")
@RequestMapping("/material")
public class MaterialController {

    private final ProductService productService;
    private final MaterialService materialService;
    private final UserByService userByService;
    private final BomService bomService;
    private final PageService pageService;
    private final SupplierService supplierService;
    private final MaterialRepository materialRepository;
    private final NoticeService noticeService;

    @Value("${org.zerock.upload.awsPath}")
    private String awsPath;

    @GetMapping("/materialList")
    public void materialList(PageRequestDTO pageRequestDTO, Model model) {

        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);

        List<Supplier> supplierList = supplierService.getSupplier();
        model.addAttribute("supplierList", supplierList);


        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<MaterialDTO> responseDTO =
                pageService.materialListWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<Material> materialList = materialService.getMaterials();

        Set<String> pNameSet = materialList.stream()
                .filter(m -> m.getProduct() != null && m.getProduct().getPName() != null)
                .map(m -> m.getProduct().getPName())
                .collect(Collectors.toSet());

        model.addAttribute("pNameList", pNameSet);


        Set<String> componentTypeSet = materialList.stream()
                .filter(m -> m.getMComponentType() != null)
                .map(Material::getMComponentType)
                .collect(Collectors.toSet());

        model.addAttribute("componentTypeList", componentTypeSet);

        Set<String> mNameSet = materialList.stream()
                .filter(m -> m.getMName() != null)
                .map(Material::getMName)
                .collect(Collectors.toSet());

        model.addAttribute("mNameList", mNameSet);


        model.addAttribute("materialList", materialList);
        model.addAttribute("responseDTO", responseDTO);

        model.addAttribute("selectedPName", pageRequestDTO.getPName() != null ? pageRequestDTO.getPName() : "");
        model.addAttribute("selectedCType", pageRequestDTO.getComponentType() != null ? pageRequestDTO.getComponentType() : "");
        model.addAttribute("selectedMName", pageRequestDTO.getMName() != null ? pageRequestDTO.getMName() : "");
        model.addAttribute("selectedMType", pageRequestDTO.getMType() != null ? pageRequestDTO.getMType() : "");

        log.info("###MATERIAL LIST: " + responseDTO);
    }

    @GetMapping("/materialRegister")
    public String materialRegister(Model model) {
        List<Product> productList = productService.getProducts();
        model.addAttribute("productList", productList);

        List<BomDTO> bomList = bomService.getBoms();
        model.addAttribute("bomList", bomList);

        List<Supplier> supplierList = supplierService.getSupplier();
        model.addAttribute("supplierList", supplierList);

        // 반환할 뷰 이름을 명시합니다.
        return "material/materialRegister";
    }

    //부품 직접 등록
    @PostMapping("/addMaterialSelf")
    public String addMaterialSelf(@ModelAttribute MaterialFormDTO form,
                                  Model model,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request) throws IOException {

        List<String> errors = new ArrayList<>();
        List<MaterialDTO> materialDTOs = form.getMaterials();
        String message;

        for(MaterialDTO materialDTO : materialDTOs) {
            String error = materialService.registerMaterial(materialDTO, materialDTO.getUId());
            if (error != null) {errors.add(error);}
        }

        if(!errors.isEmpty()) {
            message = String.join(", ", errors) + " 이 중복 되었습니다.\n중복되지 않은 부품은 등록됩니다.";
        } else{
            message = "등록이 완료되었습니다.";
            noticeService.addNotice("m");
        }

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:materialRegister";
    }

    //부품 자동 등록
    @PostMapping("/addMaterial")
    @ResponseBody
    public Map<String, Object> addMaterial(String uId, @RequestParam("file") MultipartFile[] files,
                                             @RequestParam("check") String check,
                                              Model model, RedirectAttributes redirectAttributes) throws IOException {

        Map<String, Object> response = new HashMap<>();
        List<Map<String, String>> totalDuplicateList = new ArrayList<>();
        List<String> totalErrorCheckList = new ArrayList<>();

        // 엑셀 파일 처리
        for (MultipartFile file : files) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            Map<String, Object> materialObj = registerMaterialForExel(worksheet, uId, check);

            // 중복과 에러 체크 리스트 병합
            totalDuplicateList.addAll((List<Map<String, String>>) materialObj.get("duplicate"));
            totalErrorCheckList.addAll((List<String>) materialObj.get("errorCheck"));
        }

        response.put("duplicate", totalDuplicateList);
        response.put("errorCheck", totalErrorCheckList);

        log.info("중복 부품: {}", totalDuplicateList);
        log.info("등록되지 않은 상품: {}", totalErrorCheckList);

        log.info("체크: {}", check);
        if(!check.equals("true")) {
            noticeService.addNotice("m");
        }
        return response;
    }

    private Map<String, Object> registerMaterialForExel(XSSFSheet worksheet, String uId, String check) {

        List<MaterialDTO> materialDTOs = new ArrayList<>();

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++){
            MaterialDTO materialDTO = new MaterialDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            materialDTO.setPName(formatter.formatCellValue(row.getCell(0)));
            materialDTO.setMComponentType(formatter.formatCellValue(row.getCell(1)));
            materialDTO.setMType(formatter.formatCellValue(row.getCell(2)));
            materialDTO.setMName(formatter.formatCellValue(row.getCell(3)));
            materialDTO.setMDepth(Float.parseFloat(formatter.formatCellValue(row.getCell(4))));
            materialDTO.setMHeight(Float.parseFloat(formatter.formatCellValue(row.getCell(5))));
            materialDTO.setMWidth(Float.parseFloat(formatter.formatCellValue(row.getCell(6))));
            materialDTO.setMWeight(Float.parseFloat(formatter.formatCellValue(row.getCell(7))));
            materialDTO.setMUnitPrice(formatter.formatCellValue(row.getCell(8)));
            materialDTO.setMMinNum(formatter.formatCellValue(row.getCell(9)));

            materialDTOs.add(materialDTO);
        }

        boolean result = Boolean.parseBoolean(check);

        return materialService.registerMaterialEasy(materialDTOs, uId, result);
    }

    @PostMapping("/modify")
    public String modify(@ModelAttribute MaterialDTO materialDTO, RedirectAttributes redirectAttributes, String uName) {
        log.info("pp modify post.....#@" + materialDTO);
        materialService.modifyMaterial(materialDTO, uName);
        redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:materialList";
    }

    @PostMapping("/remove")
    public String remove(@ModelAttribute MaterialDTO materialDTO, RedirectAttributes redirectAttributes, @RequestParam List<String> pCodes) {
        log.info("pp remove post.....#@" + materialDTO);
        materialService.removeMaterial(pCodes);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:materialList";
    }

    @GetMapping("/{pName}/cType")
    @ResponseBody
    public List<String> getComponentTypesByProductCode(@PathVariable String pName) {
        List<String> componentTypes = materialRepository.findComponentTypesByProductName(pName);
        return componentTypes != null ? componentTypes : Collections.emptyList();
    }

    @GetMapping("/{mType}/{pName}/mName")
    @ResponseBody
    public List<String> getMNamesByMType(@PathVariable String mType, @PathVariable String pName) {
        List<String> mNames = materialRepository.findMNameByETC(pName, mType);
        return mNames != null ? mNames : Collections.emptyList();
    }

    @GetMapping("/downloadTemplate/{isTemplate}")
    public ResponseEntity<Resource> download(@PathVariable("isTemplate") boolean isTemplate) {
        try {
            // 실제 파일명은 필요에 따라 조건 처리
            String fileName = "testMaterial.xlsx";

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
}
