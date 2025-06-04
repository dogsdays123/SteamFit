package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.DeliveryRequestDTO;
import org.zerock.b01.dto.InputDTO;
import org.zerock.b01.dto.ProgressInspectionDTO;
import org.zerock.b01.repository.*;
import org.zerock.b01.service.DeliveryRequestService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Log4j2
@Service
public class DeliveryRequestServiceImpl implements DeliveryRequestService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private DeliveryRequestRepository deliveryRequestRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private OrderByRepository orderByRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    AutoGenerateCode autoGenerateCode;
    @Autowired
    private SupplierStockRepository supplierStockRepository;


    @Override
    public void registerDeliveryRequest(DeliveryRequestDTO deliveryRequestDTO) {
        DeliveryRequest deliveryRequest = modelMapper.map(deliveryRequestDTO, DeliveryRequest.class);

        OrderBy orderBy = orderByRepository.findByOrderByCode(deliveryRequestDTO.getOCode()).orElseThrow(()->new RuntimeException("OrderBy not found"));
        deliveryRequest.setOrderBy(orderBy);

        orderBy.getDeliveryProcurementPlan().getProductionPlan().setPpState(CurrentStatus.DELIVERY_REQUEST);
        orderBy.getDeliveryProcurementPlan().setDppState(CurrentStatus.DELIVERY_REQUEST);
        orderBy.setOState(CurrentStatus.DELIVERY_REQUESTED);
        orderByRepository.save(orderBy);

        Material material = materialRepository.findByMaterialCode(deliveryRequestDTO.getMCode()).orElseThrow(() -> new RuntimeException("Material not found"));
        deliveryRequest.setMaterial(material);

        Supplier supplier = supplierRepository.findSupplierBySId(deliveryRequestDTO.getSId());
        deliveryRequest.setSupplier(supplier);

        if (deliveryRequestDTO.getDrCode() == null) {

            deliveryRequest.setDrCode(autoGenerateCode.generateCode("dr",""));
        }
        deliveryRequest.setDrState(CurrentStatus.ON_HOLD);
        deliveryRequestRepository.save(deliveryRequest);


    }

    @Override
    @Transactional
    public void drAgree(DeliveryRequestDTO deliveryRequestDTO, List<String> drCodes){
        for (String drCode : drCodes) {
            DeliveryRequest deliveryRequest = deliveryRequestRepository.findByDeliveryRequestCode(drCode).
                    orElseThrow(()->new RuntimeException("DeliveryRequest not found"));

            deliveryRequest.setDrState(CurrentStatus.DELIVERY_DELIVERED);
            OrderBy orderBy = deliveryRequest.getOrderBy();
            orderBy.setOState(CurrentStatus.DELIVERY_DELIVERED);

            String mCode = deliveryRequest.getMaterial().getMCode();
            Long sId = deliveryRequest.getSupplier().getSId();
            int deliveryQtyInt = Integer.parseInt(deliveryRequest.getDrNum());

            List<SupplierStock> stocks = supplierStockRepository.findByMaterialMCodeAndSupplierSId(mCode, sId);

            for (SupplierStock stock : stocks) {
                int currentStock = Integer.parseInt(stock.getSsNum());

                if (currentStock < deliveryQtyInt) {
                    throw new RuntimeException("재고 부족: 현재 재고 " + currentStock + ", 요청 납품 수량 " + deliveryQtyInt);
                }

                stock.setSsNum(String.valueOf(currentStock - deliveryQtyInt));
            }

            deliveryRequestRepository.save(deliveryRequest);
            orderByRepository.save(orderBy);
        }
    }

    @Override
    public void drRemove(List<String> drCodes){
        if (drCodes == null || drCodes.isEmpty()) {
            throw new IllegalArgumentException("삭제할 납품 정보가 없습니다.");
        }

        for (String drCode : drCodes) {
            deliveryRequestRepository.deleteById(drCode);
        }
    }
}
