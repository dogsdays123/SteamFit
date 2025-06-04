package org.zerock.b01.service;

import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ProductionPlanDTO;

import java.util.List;
import java.util.Map;

public interface ProductionPlanService {
    String registerProductionPlan(ProductionPlanDTO productionPlanDTO, String uName);
    ProductionPlan findProductionPlan(ProductionPlanDTO productionPlanDTO);
    List<ProductionPlan> getPlans();
    void modifyProductionPlan(ProductionPlanDTO productionPlanDTO, String uName);
    void removeProductionPlan(List<String> ppCodes);
    PageResponseDTO<ProductionPlanDTO> list(PageRequestDTO pageRequestDTO);
    Map<String, Map<String, Integer>> getMonthlyProductionSummary();
    ProductionPlanDTO getOldPlans(String ppCode);
    void handlePlanQuantityChange(String ppCode, int oldQty, int newQty, String updatedBy);
}
