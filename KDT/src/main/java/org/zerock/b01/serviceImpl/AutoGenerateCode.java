package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.*;
import org.zerock.b01.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Log4j2
@Service
public class AutoGenerateCode {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductionPlanRepository productionPlanRepository;
    @Autowired
    MaterialRepository materialRepository;
    @Autowired
    DeliveryProcurementPlanRepository dppRepository;
    @Autowired
    OrderByRepository orderByRepository;
    @Autowired
    CountBy countBy;

    @Autowired
    DeliveryRequestRepository deliveryRequestRepository;

    public String generateCode(String type, String name) {

        String dateToString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String prefix = type + "-";
        int count = 0;

        List<Product> p = productRepository.findAll();
        List<ProductionPlan> pp = productionPlanRepository.findAll();
        List<Material> m = materialRepository.findAll();
        List<DeliveryProcurementPlan> dpp = dppRepository.findAll();
        List<OrderBy> ob = orderByRepository.findAll();
        List<DeliveryRequest> dr = deliveryRequestRepository.findAll();

        switch (type) {
            case "p":
                boolean exists = p.stream().anyMatch(product -> name.equals(product.getPName()));
                if (exists) {
                    return "오류";
                }
                if(!p.isEmpty()){
                    String maxCode = countBy.findMaxPCode(prefix);
                    String[] parts = maxCode.split("-");
                    if (parts.length == 3) {
                        count = Integer.parseInt(parts[2]) + 1;
                    }
                }
                break;
            case "pp":
                if(!pp.isEmpty()){
                    String maxCode = countBy.findMaxPpCode(prefix);
                    String[] parts = maxCode.split("-");
                    if (parts.length == 3) {
                        count = Integer.parseInt(parts[2]) + 1;
                    }
                }
                break;
            case "m":
                if(!m.isEmpty()){
                    String maxCode = countBy.findMaxMCode(prefix);
                    String[] parts = maxCode.split("-");
                    if (parts.length == 3) {
                        count = Integer.parseInt(parts[2]) + 1;
                    }
                }
                break;
            case "dpp":
                if(!dpp.isEmpty()){
                    String maxCode = countBy.findMaxDppCode(prefix);
                    String[] parts = maxCode.split("-");
                    if (parts.length == 3) {
                        count = Integer.parseInt(parts[2]) + 1;
                    }
                }
                break;
            case "ob":
                if(!ob.isEmpty()){
                    String maxCode = countBy.findMaxOCode(prefix);
                    String[] parts = maxCode.split("-");
                    if (parts.length == 3) {
                        count = Integer.parseInt(parts[2]) + 1;
                    }
                }
                break;
            case "dr":
                if(!dr.isEmpty()){
                    String maxCode = countBy.findMaxDrCode(prefix);
                    String[] parts = maxCode.split("-");
                    if (parts.length == 3) {
                        count = Integer.parseInt(parts[2]) + 1;
                    }
                }
                break;
            default:
        }

        // 코드 생성
        return  String.format("%s-%s-%03d", type.toUpperCase(), dateToString, count);
    }
}
