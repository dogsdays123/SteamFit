package org.zerock.b01.serviceImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.InventoryStockDTO;
import org.zerock.b01.dto.ProgressInspectionDTO;
import org.zerock.b01.repository.OrderByRepository;
import org.zerock.b01.repository.ProgressInspectionRepository;
import org.zerock.b01.repository.SupplierStockRepository;
import org.zerock.b01.service.ProgressInspectionService;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class ProgressInspectionServiceImpl implements ProgressInspectionService {
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    OrderByRepository orderByRepository;
    @Autowired
    SupplierStockRepository ssRepository;
    @Autowired
    ProgressInspectionRepository piRepository;
    @Autowired
    private ProgressInspectionRepository progressInspectionRepository;

    @Override
    public Boolean register(ProgressInspectionDTO psDTO){
        OrderBy orderBy = orderByRepository.findById(psDTO.getOCode()).orElseThrow();
        SupplierStock supplierStock = (ssRepository.findBySupplierId
                (orderBy.getDeliveryProcurementPlan().getSupplier().getSId(), orderBy.getDeliveryProcurementPlan().getMaterial().getMCode()));

        ProgressInspection progressInspection = modelMapper.map(psDTO, ProgressInspection.class);
        progressInspection.setOrderBy(orderBy);
        progressInspection.setSupplierStock(supplierStock);

        if(piRepository.findByOCode(psDTO.getOCode()) == null){
            piRepository.save(progressInspection);

            if(orderBy.getOState() == CurrentStatus.HOLD_PROGRESS){
                orderBy.setOState(CurrentStatus.UNDER_INSPECTION);
                orderByRepository.save(orderBy);
            }
            return true;
        } else {
            return false;
        }

    }

    @Override
    @Transactional
    public void piAgree(ProgressInspectionDTO piDTO, List<Long> psIds){
        for (Long psId : psIds) {
            ProgressInspection progressInspection = progressInspectionRepository.findById(psId)
                    .orElseThrow(() -> new IllegalArgumentException("진척 검수 정보를 찾을 수 없습니다. ID: " + psId));

            OrderBy orderBy = progressInspection.getOrderBy();
            orderBy.setOState(CurrentStatus.SUCCESS_INSPECTION);
        }
    }

    @Override
    public void piRemove(ProgressInspectionDTO piDTO, List<Long> psIdss){
        if (psIdss == null || psIdss.isEmpty()) {
            throw new IllegalArgumentException("삭제할 진척 검수 정보가 없습니다.");
        }

        for (Long psId : psIdss) {
            log.info("psId: " + psId);
            progressInspectionRepository.deleteById(psId);
        }
    }
}
