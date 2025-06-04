package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.DeliveryProcurementPlan;
import org.zerock.b01.domain.OrderBy;
import org.zerock.b01.dto.OrderByDTO;
import org.zerock.b01.repository.DeliveryProcurementPlanRepository;
import org.zerock.b01.repository.OrderByRepository;
import org.zerock.b01.repository.SupplierStockRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.OrderByService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class OrderByServiceImpl implements OrderByService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    DeliveryProcurementPlanRepository dppRepository;
    @Autowired
    UserByRepository userByRepository;
    @Autowired
    OrderByRepository orderByRepository;
    @Autowired
    AutoGenerateCode autoGenerateCode;
    @Autowired
    SupplierStockRepository supplierStockRepository;

    public void orderByRegister(OrderByDTO orderByDTO, String uId) {
        DeliveryProcurementPlan dpp = dppRepository.findById(orderByDTO.getDppCode()).orElseThrow();
        OrderBy orderBy = modelMapper.map(orderByDTO, OrderBy.class);
        orderBy.setOCode(autoGenerateCode.generateCode("ob", ""));
        orderBy.setUserBy(userByRepository.findById(uId).orElseThrow());
        orderBy.setDeliveryProcurementPlan(dpp);
        orderBy.getDeliveryProcurementPlan().setDppState(CurrentStatus.ORDER_BY);
        orderBy.getDeliveryProcurementPlan().getProductionPlan().setPpState(CurrentStatus.ORDER_BY);
        if(Integer.parseInt(supplierStockRepository.findLeadTimeByETC(dpp.getSupplier().getSName(), dpp.getMaterial().getMCode())) < 10)
        {orderBy.setOState(CurrentStatus.HOLD_DELIVERY);}
        else {
            orderBy.setOState(CurrentStatus.HOLD_PROGRESS);
        }

        orderByRepository.save(orderBy);
    }

    @Override
    public Map<String, Double> getMonthlyOrderSummary() {
        List<Object[]> rawData = orderByRepository.findMonthlyTotals();
        Map<String, Double> summary = new LinkedHashMap<>();

        for (Object[] row : rawData) {
            String month = (String) row[0];
            Double total = ((Number) row[1]).doubleValue();
            summary.put(month, total);
        }

        return summary;
    }

    @Override
    @Transactional
    public void setOrderReady(OrderByDTO orderByDTO, List<String> oCodes){
        for(String oCode : oCodes){
            OrderBy orderBy = orderByRepository.findByOrderByCode(oCode)
                    .orElseThrow(() -> new IllegalArgumentException("해당 발주 정보가 존재하지 않습니다: " + oCode));
            orderBy.setOState(CurrentStatus.READY_SUCCESS);
            orderByRepository.save(orderBy);
        }
    }
}
