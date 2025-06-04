package org.zerock.b01.service;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ProductDTO;
import org.zerock.b01.dto.ProductionPlanDTO;

import java.util.List;
import java.util.Map;

public interface ProductService {
    List<Product> getProducts();
    String[] registerProducts(List<ProductDTO> productDTOs, String uId);
    Map<String, String[]> registerProductsEasy(List<ProductDTO> productDTOs, String uId);
    Map<String, String[]> ProductCheck(List<ProductDTO> productDTOs);
    void modifyProduct(ProductDTO productDTO, String uName);
    void removeProduct(List<String> pCodes);
//    PageResponseDTO<ProductionPlanDTO> list(PageRequestDTO pageRequestDTO);
}
