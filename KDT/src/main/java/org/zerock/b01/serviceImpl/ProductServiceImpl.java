package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.DeliveryProcurementPlanDTO;
import org.zerock.b01.dto.ProductDTO;
import org.zerock.b01.dto.ProductionPlanDTO;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.ProductService;
import org.zerock.b01.service.UserByService;

import java.util.*;

@Log4j2
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private UserByService userByService;
    @Autowired
    private UserByRepository userByRepository;
    @Autowired
    AutoGenerateCode autoGenerateCode;

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public String[] registerProducts(List<ProductDTO> productDTOs, String uId){

        List<String> errorMessage = new ArrayList<>();
        List<String> productRegister = new ArrayList<>();
        productRegister.add("상품 등록이 완료되었습니다.");

        for(ProductDTO productDTO : productDTOs){
            Product product = modelMapper.map(productDTO, Product.class);
            product.setUserBy(userByRepository.findByUId(uId));

            //이미 존재한 제품
            if(productRepository.findByProductName(product.getPName()).isPresent()){
                productRegister.add("이미 등록된 상품입니다.");
                errorMessage.add(product.getPName());
            }
            //존재하지 않은 제품
            else {
                product.setPCode(autoGenerateCode.generateCode("p", productDTO.getPName()));
                productRepository.save(product);
            }
        }

        if(errorMessage.size() > 0){
            errorMessage.add(" 상품은 이미 등록된 상품입니다.");
            return errorMessage.toArray(new String[errorMessage.size()]);
        } else {
            return productRegister.toArray(new String[productRegister.size()]);
        }
    }

    @Override
    public Map<String, String[]> ProductCheck(List<ProductDTO> productDTOs) {

        List<String> duplicatedNames = new ArrayList<>();

        for (ProductDTO productDTO : productDTOs) {
            Product product = modelMapper.map(productDTO, Product.class);

            String pName = product.getPName();

            boolean isDuplicated = false;

            if (productRepository.findByProductName(pName).isPresent()) {
                isDuplicated = true;
            }

            if (isDuplicated) {
                duplicatedNames.add(pName);
            }
        }

        Map<String, String[]> result = new HashMap<>();
        result.put("pNames", duplicatedNames.toArray(new String[0]));

        return result;
    }

    @Override
    public Map<String, String[]> registerProductsEasy(List<ProductDTO> productDTOs, String uId) {

        UserBy user = userByRepository.findByUId(uId);
        String[] generateCodes = new String[productDTOs.size()];
        List<String[]> checksList = new ArrayList<>();
        Set<String> seenPCodeNames = new HashSet<>();
        Map<String, String[]> result = new HashMap<>();

        int index = 0;

        for (ProductDTO productDTO : productDTOs) {
            Product product = modelMapper.map(productDTO, Product.class);
            String[] checkAll = duplicationCheck(productDTO);

            // 중복이 있을 경우, 중복 값을 확인하고 계속해서 리스트에 추가
            if (checkAll[0].equals("true")) {
                log.info("testF " + productDTO);
                // 중복된 값이 이미 checksList에 존재하지 않으면 추가
                for (int i = 1; i < checkAll.length; i++) {
                    // 이미 존재하는 값은 추가하지 않도록 set을 사용해 방지
                    if (!seenPCodeNames.contains(checkAll[i])) {
                        checksList.add(new String[]{checkAll[i]});  // 중복 값을 배열로 추가
                        seenPCodeNames.add(checkAll[i]); // 중복된 값은 Set에 추가
                    }
                }
            } else {
                // 중복이 없으면 PCode 생성 및 저장
                if (productDTO.getPCode() == null || productDTO.getPCode().isEmpty()) {
                    generateCodes[index] = autoGenerateCode.generateCode("p", productDTO.getPName());
                    log.info("^^ " + index + generateCodes[index]);
                    product.setPCode(generateCodes[index]);
                    index++;
                }
                log.info("testT " + productDTO);

                product.setUserBy(user);
                productRepository.save(product);
            }
        }

        // generateCodes 배열을 결과에 추가
        result.put("checks", checksList.stream().flatMap(Arrays::stream).toArray(String[]::new));
        result.put("generateCodes", generateCodes);
        return result;
    }


    @Override
    public void modifyProduct(ProductDTO productDTO, String uName){
        Optional<Product> result = productRepository.findByProductId(productDTO.getPCode());
        Product product = result.orElseThrow();
        product.change(productDTO.getPName());
        productRepository.save(product);
    }

    @Override
    public void removeProduct(List<String> pCodes){
        if (pCodes == null || pCodes.isEmpty()) {
            throw new IllegalArgumentException("삭제할 생산 계획 코드가 없습니다.");
        }

        for (String pCode : pCodes) {
            productRepository.deleteById(pCode); // 개별적으로 삭제
        }
    }

    public String[] duplicationCheck(ProductDTO dto) {
        List<Product> products = productRepository.findByProducts();
        List<String> checkList = new ArrayList<>();  // 중복된 값을 저장할 리스트
        boolean hasDuplicate = false; // 중복 여부를 추적

        for (Product product : products) {
            if (product.getPName().equals(dto.getPName())) {
                checkList.add(product.getPName());
                hasDuplicate = true;
            }
        }

        // 맨 앞에 중복 여부 추가
        checkList.add(0, hasDuplicate ? "true" : "false");

        return checkList.toArray(new String[0]);
    }

}
