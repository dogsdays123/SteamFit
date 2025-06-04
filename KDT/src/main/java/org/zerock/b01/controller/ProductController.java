package org.zerock.b01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
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
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.ProductListAllDTO;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.ProductionPlanRepository;
import org.zerock.b01.security.UserBySecurityDTO;
import org.zerock.b01.service.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || (authentication.principal.status == '승인' && authentication.principal.userJob == '생산부서'))")
@RequestMapping("/product")
public class ProductController {


    @Value("${org.zerock.upload.awsPath}")
    private String awsPath;

    private final UserByService userByService;
    private final ProductService productService;
    private final PageService pageService;
    private final MaterialService materialService;
    private final ProductionPlanRepository productionPlanRepository;
    private final MaterialRepository materialRepository;
    private final NoticeService noticeService;

    @GetMapping("/goodsList")
    public void productList(PageRequestDTO pageRequestDTO, Model model) {

        if (pageRequestDTO.getSize() == 0) {
            pageRequestDTO.setSize(10); // 기본값 10
        }

        PageResponseDTO<ProductListAllDTO> responseDTO =
                pageService.productListWithAll(pageRequestDTO);

        if (pageRequestDTO.getTypes() != null) {
            model.addAttribute("keyword", pageRequestDTO.getKeyword());
        }

        List<Product> productList = productService.getProducts();

        model.addAttribute("productList", productList);
        model.addAttribute("selectedPCode", pageRequestDTO.getPCode() != null ? pageRequestDTO.getPCode() : "");
        model.addAttribute("selectedPName", pageRequestDTO.getPName() != null ? pageRequestDTO.getPName() : "");
        model.addAttribute("responseDTO", responseDTO);
        log.info("^&^&" + responseDTO);
    }

    @GetMapping("/goodsRegister")
    public void productRegister() {
        log.info("##PRODUCT REGISTER PAGE GET....##");
    }

    @GetMapping("/downloadTemplate/{isTemplate}")
    public ResponseEntity<Resource> downloadProductPlan(@PathVariable("isTemplate") boolean isTemplate) {
        try {
            // 실제 파일명은 필요에 따라 조건 처리
            String fileName = "testProduct.xlsx";

            // classpath에서 리소스 로드
            Resource resource = new ClassPathResource("static/upload/" + fileName);

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

    //제품 직접 등록
    @PostMapping("/goodsRegister")
    public String productRegisterPost(String uId,
                                      @RequestParam("pNames[]") List<String> pNames,
                                      Model model, RedirectAttributes redirectAttributes,
                                      HttpServletRequest request) {

        log.info(" ^^^^ " + uId);

        List<ProductDTO> products = new ArrayList<>();

        // pCodes와 pNames 배열을 순회하여 Product 객체를 만들어 products 리스트에 추가
        for (int i = 0; i < pNames.size(); i++) {
            ProductDTO product = new ProductDTO();
            product.setPName(pNames.get(i)); // pName을 설정
            products.add(product); // 제품 리스트에 추가
        }

        log.info(" ^^^^ " + uId);
        String[] message = productService.registerProducts(products, uId);
        String messageString = String.join(",", message);

        noticeService.addNotice("p");
        redirectAttributes.addFlashAttribute("message", messageString);
        return "redirect:goodsRegister";
    }


    //제품 자동 등록
    @PostMapping("/addProduct")
    @ResponseBody
    public Map<String, Object> uploadProduct(String uId, @RequestParam("file") MultipartFile[] files,
                                             @RequestParam("check") String check,
                                             Model model, RedirectAttributes redirectAttributes) throws IOException {
        Map<String, Object> response = new HashMap<>();
        Map<String, String[]> duplicationProducts = new HashMap<>();

        // 엑셀 파일 처리
        for (MultipartFile file : files) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            duplicationProducts = registerProduct(worksheet, uId, check);
        }

        log.info("test " + Arrays.toString(duplicationProducts.get("checks")));

        response.put("isAvailable", duplicationProducts.isEmpty());
        response.put("pCodes", duplicationProducts.get("pCodes"));
        response.put("pNames", duplicationProducts.get("pNames"));
        log.info("$$$ " + Arrays.toString(duplicationProducts.get("pCodes")));

        if(!check.equals("true")){
            noticeService.addNotice("p");
        }
        return response;
    }

    private Map<String, String[]> registerProduct(XSSFSheet worksheet, String uId, String check) {

        List<ProductDTO> productDTOs = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);
            if (row == null) continue; // 빈 행 방지

            ProductDTO productDTO = new ProductDTO();

            // 1번 셀: 상품 이름 (PName)
            String productName = "";
            XSSFCell cell1 = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell1 != null) {
                productName = formatter.formatCellValue(cell1);
            }
            productDTO.setPName(productName);

            log.info("상품 이름: {}", productDTO.getPName());

            productDTOs.add(productDTO);
        }

        // 검증 여부에 따라 서비스 분기 처리
        if ("true".equals(check)) {
            return productService.ProductCheck(productDTOs);
        } else {
            return productService.registerProductsEasy(productDTOs, uId);
        }
    }


    @PostMapping("/modify")
    public String modify(@ModelAttribute ProductDTO productDTO, RedirectAttributes redirectAttributes, String uName) {
        log.info("pp modify post.....#@" + productDTO);
        productService.modifyProduct(productDTO, uName);
        redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:goodsList";
    }

    @PostMapping("/remove")
    public String remove(@ModelAttribute ProductDTO productDTO, RedirectAttributes redirectAttributes, @RequestParam List<String> pCodes) {
        log.info("pp remove post.....#@" + productDTO);
        for (String pCode : pCodes) {
            if (productionPlanRepository.existsByProduct_pCode(pCode)) {
                redirectAttributes.addFlashAttribute("message", "생산 계획이 등록된 상품이 있어 삭제할 수 없습니다.");
                return "redirect:goodsList";
            }
            if (materialRepository.existsByProduct_pCode(pCode)) {
                redirectAttributes.addFlashAttribute("message", "부품 등록된 상품이 있어 삭제할 수 없습니다.");
                return "redirect:goodsList";
            }
        }

        productService.removeProduct(pCodes);
        redirectAttributes.addFlashAttribute("message", "삭제가 완료되었습니다.");
        return "redirect:goodsList";
    }
}
