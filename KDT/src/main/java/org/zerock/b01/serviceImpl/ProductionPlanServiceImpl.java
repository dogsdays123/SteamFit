package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.allDTO.PlanListAllDTO;
import org.zerock.b01.dto.ProductionPlanDTO;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.ProductionPlanRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.ProductionPlanService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ProductionPlanServiceImpl implements ProductionPlanService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserByRepository userByRepository;

    @Autowired
    private AutoGenerateCode autoGenerateCode;

    @Override
    public ProductionPlan findProductionPlan(ProductionPlanDTO productionPlanDTO){
        ProductionPlan productionPlan = modelMapper.map(productionPlanDTO, ProductionPlan.class);
        return productionPlan;
    }

    //생산계획 등록
    @Override
    public String registerProductionPlan(ProductionPlanDTO productionPlanDTO, String uId) {

        ProductionPlan plan = modelMapper.map(productionPlanDTO, ProductionPlan.class);
        Product product = productionPlanRepository.findByProduct(productionPlanDTO.getPName());

        //제품 자동등록
        if(product == null){
            product = new Product();
            product.setPName(productionPlanDTO.getPName());
            //코드 자동생성
            product.setPCode(autoGenerateCode.generateCode("p", productionPlanDTO.getPName()));
            product.setUserBy(userByRepository.findByUId(uId));
            productRepository.save(product);
        }

        plan.setUserBy(userByRepository.findByUId(uId));
        log.info("&&&& " + uId);

        //만약 생산계획 코드가 없고, 새로 입력된 경우
        if (productionPlanDTO.getPpCode() == null) {
            log.info("NoHaveNew" + plan.getPpCode());
            //코드 자동생성
            plan.setPpCode(autoGenerateCode.generateCode("pp", ""));
        }

        plan.setProduct(product);
        plan.setPpState(CurrentStatus.ON_HOLD);
        log.info("****" + plan.getPpCode());
        productionPlanRepository.save(plan);

        return plan.getPpCode();
    }

    public List<ProductionPlan> getPlans(){
        return productionPlanRepository.findByPlans();
    }

    @Override
    public void modifyProductionPlan(ProductionPlanDTO productionPlanDTO, String uName){
        Optional<ProductionPlan> result = productionPlanRepository.findByProductionPlanCode(productionPlanDTO.getPpCode());
        ProductionPlan productionPlan = result.orElseThrow();
        productionPlan.change(productionPlanDTO.getPpNum(), productionPlanDTO.getPpStart(), productionPlanDTO.getPpEnd());
        productionPlanRepository.save(productionPlan);
    }

    @Override
    public ProductionPlanDTO getOldPlans(String ppCode) {
        Optional<ProductionPlan> optionalPlan = productionPlanRepository.findByProductionPlanCode(ppCode);
        ProductionPlan plan = optionalPlan.orElseThrow(() -> new IllegalArgumentException("생산 계획을 찾을 수 없습니다: " + ppCode));
        return modelMapper.map(plan, ProductionPlanDTO.class);
    }

    @Override
    public void handlePlanQuantityChange(String ppCode, int oldQty, int newQty, String updatedBy) {
        log.info("[수량 변경 처리] 생산계획: {}, {} → {}, 변경자: {}", ppCode, oldQty, newQty, updatedBy);

    }

    @Override
    public void removeProductionPlan(List<String> ppCodes){
        if (ppCodes == null || ppCodes.isEmpty()) {
            throw new IllegalArgumentException("삭제할 생산 계획 코드가 없습니다.");
        }

        for (String ppCode : ppCodes) {
            productionPlanRepository.deleteById(ppCode); // 개별적으로 삭제
        }
    }

    @Override
    public PageResponseDTO<ProductionPlanDTO> list(PageRequestDTO pageRequestDTO){
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        LocalDate ppStart = pageRequestDTO.getPpStart();
        LocalDate ppEnd = pageRequestDTO.getPpEnd();
        String ppCode = pageRequestDTO.getPpCode();
        String pName = pageRequestDTO.getPName();
        String ppState = pageRequestDTO.getPpState();
        String uId = pageRequestDTO.getUId();
        Pageable pageable = pageRequestDTO.getPageable();

        Page<PlanListAllDTO> result = productionPlanRepository.planSearchWithAll(types, keyword, uId, ppCode, pName, ppState, ppStart, ppEnd, pageable);

        List<ProductionPlanDTO> dtoList = result.getContent().stream().map(productionPlan ->modelMapper.map(productionPlan, ProductionPlanDTO.class)).collect(Collectors.toList());

        return PageResponseDTO.<ProductionPlanDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public Map<String, Map<String, Integer>> getMonthlyProductionSummary() {
        List<Object[]> resultList = productionPlanRepository.findMonthlyProductPlanCountsByProduct();

        Map<String, Map<String, Integer>> result = new LinkedHashMap<>();

        for (Object[] row : resultList) {
            String month = (String) row[0];
            String product = (String) row[1];
            BigDecimal countDecimal = (BigDecimal) row[2];  // BigDecimal로 받아야 함
            int count = countDecimal.intValue();            // 또는 longValue()

            result
                    .computeIfAbsent(month, k -> new LinkedHashMap<>())
                    .put(product, count);
        }

        return result;
    }
}
