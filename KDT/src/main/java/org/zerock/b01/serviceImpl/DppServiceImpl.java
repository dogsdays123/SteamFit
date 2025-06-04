package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.DeliveryProcurementPlanDTO;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.repository.*;
import org.zerock.b01.service.DppService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Log4j2
@Service
public class DppServiceImpl implements DppService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    UserByRepository userByRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductionPlanRepository productionPlanRepository;
    @Autowired
    DeliveryProcurementPlanRepository deliveryProcurementPlanRepository;
    @Autowired
    MaterialRepository materialRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    AutoGenerateCode autoGenerateCode;

    @Override
    public void registerDpp(DeliveryProcurementPlanDTO dppDTO) {
        DeliveryProcurementPlan dpp = modelMapper.map(dppDTO, DeliveryProcurementPlan.class);

        dpp.setUserBy(userByRepository.findByUId(dppDTO.getUId()));
        dpp.setMaterial(materialRepository.findByMaterialCode(dppDTO.getMCode()).orElseThrow());
        dpp.setProductionPlan(productionPlanRepository.findByProductionPlanCode(dppDTO.getPpCode()).orElseThrow());
        dpp.setDppState(CurrentStatus.ON_HOLD);
        dpp.getProductionPlan().setPpState(CurrentStatus.DPP);
        dpp.setSupplier(supplierRepository.findSupplierBySName(dppDTO.getSName()));
        dpp.setOneCode(autoGenerateCode.generateCode("dpp", ""));

        deliveryProcurementPlanRepository.save(dpp);
    }
}
