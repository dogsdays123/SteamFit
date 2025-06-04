package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.BomDTO;
import org.zerock.b01.dto.DeliveryRequestDTO;
import org.zerock.b01.dto.InputDTO;
import org.zerock.b01.repository.*;
import org.zerock.b01.service.InputService;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class InputServiceImpl implements InputService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private DeliveryRequestRepository deliveryRequestRepository;

    @Autowired
    private InputRepository inputRepository;

    @Autowired
    private OrderByRepository orderByRepository;

    @Autowired
    private InventoryStockRepository inventoryStockRepository;

    @Override
    public List<InputDTO> getInputs() {
        List<InPut> inputList = inputRepository.findAll();

        List<InputDTO> inputDTOList = new ArrayList<>();

        for(InPut input : inputList) {
            InputDTO inputDTO = new InputDTO();
            inputDTO.setIpState(input.getIpState());
            inputDTO.setIpCode(input.getIpCode());
            inputDTO.setIpNum(input.getIpNum());
            inputDTO.setIpFalseNum(input.getIpFalseNum());
            inputDTO.setIpTrueNum(input.getIpTrueNum());
            inputDTO.setDrState(input.getDeliveryRequest().getDrState());
            inputDTO.setDrCode(input.getDeliveryRequest().getDrCode());
            inputDTO.setDrNum(input.getDeliveryRequest().getDrNum());
            inputDTO.setOCode(input.getOrderBy().getOCode());
            inputDTO.setONum(input.getOrderBy().getONum());
            inputDTO.setRegDate(input.getRegDate().toLocalDate());
            inputDTO.setMCode(input.getDeliveryRequest().getMaterial().getMCode());
            inputDTO.setMName(input.getDeliveryRequest().getMaterial().getMName());
            inputDTOList.add(inputDTO);
        }

        return inputDTOList;
    }

    @Override
    public List<DeliveryRequestDTO> getDeliveryRequest() {
        List<DeliveryRequest> deliveryRequestList = deliveryRequestRepository.findAll();

        List<DeliveryRequestDTO> deliveryRequestDTOList = new ArrayList<>();

        for (DeliveryRequest deliveryRequest : deliveryRequestList) {
            DeliveryRequestDTO deliveryRequestDTO = new DeliveryRequestDTO();
            deliveryRequestDTO.setDrDate(deliveryRequest.getDrDate());
            deliveryRequestDTO.setDrState(deliveryRequest.getDrState());
            deliveryRequestDTO.setDrCode(deliveryRequest.getDrCode());
            deliveryRequestDTO.setDrNum(Integer.parseInt(deliveryRequest.getDrNum()));
            deliveryRequestDTO.setMCode(deliveryRequest.getMaterial().getMCode());
            deliveryRequestDTO.setMName(deliveryRequest.getMaterial().getMName());
            deliveryRequestDTO.setONum(deliveryRequest.getOrderBy().getONum());
            deliveryRequestDTO.setOCode(deliveryRequest.getOrderBy().getOCode());
            deliveryRequestDTO.setSId(deliveryRequest.getSupplier().getSId());
            deliveryRequestDTO.setSName(deliveryRequest.getSupplier().getSName());
            deliveryRequestDTOList.add(deliveryRequestDTO);
        }

        return deliveryRequestDTOList;
    }

    // Input 코드 생성
    private String generateNextIpCode(String latestIpCode) {

        String numberPart = latestIpCode.substring(3);
        int nextNumber = Integer.parseInt(numberPart) + 1;

        return String.format("IP-%03d", nextNumber);
    }

    @Override
    public void registerInput(InputDTO inputDTO) {
        InPut input = modelMapper.map(inputDTO, InPut.class);

        String latestIpCode = inputRepository.findTopByOrderByIpCodeDesc()
                .map(InPut::getIpCode)
                .orElse("IP-000"); //

        String newIpCode = generateNextIpCode(latestIpCode);
        input.setIpCode(newIpCode);

        DeliveryRequest deliveryRequest = deliveryRequestRepository.findByDeliveryRequestCode(inputDTO.getDrCode()).orElseThrow(() -> new RuntimeException("DR not found"));
        deliveryRequest.setDrState(CurrentStatus.INPUT_SUCCESS);
        input.setDeliveryRequest(deliveryRequest);

        OrderBy orderBy = orderByRepository.findByOrderByCode(inputDTO.getOCode()).orElseThrow(() -> new RuntimeException("OCode not found"));
        orderBy.getDeliveryProcurementPlan().setDppState(CurrentStatus.INPUT);
        orderBy.getDeliveryProcurementPlan().getProductionPlan().setPpState(CurrentStatus.INPUT);
        input.setOrderBy(orderBy);

        int drNum = Integer.parseInt(deliveryRequest.getDrNum());
        int ipNum = Integer.parseInt(input.getIpNum());
        int ipFalseNum = Integer.parseInt(input.getIpFalseNum());
        int totalReceived = ipNum + ipFalseNum;

        if (ipFalseNum == drNum) {
            input.setIpState(CurrentStatus.RETURNED_ALL); // 전량 반품
        } else if (ipFalseNum > 0 && totalReceived == drNum) {
            input.setIpState(CurrentStatus.PARTIAL_RETURN); // 부분 반품
        } else if (totalReceived < drNum) {
            input.setIpState(CurrentStatus.IN_PROGRESS); // 입고 진행 중
        } else {
            input.setIpState(CurrentStatus.INPUT_SUCCESS); // 입고 완료
        }

        inputRepository.save(input);
        updateInventoryStock(deliveryRequest, inputDTO);
        updateDeliveryRequestState(deliveryRequest, String.valueOf(ipNum), String.valueOf(drNum));
    }

    private void updateDeliveryRequestState(DeliveryRequest deliveryRequest, String ipNum, String drNum) {
        try {
            int ipNumber = Integer.parseInt(ipNum);
            int drNumber = Integer.parseInt(drNum);
            int remainingQuantity = drNumber - ipNumber;
            if (ipNumber >= drNumber) {
                deliveryRequest.setDrState(CurrentStatus.APPROVAL);
            } else {
                deliveryRequest.setDrState(CurrentStatus.IN_PROGRESS);
            }
            updateRemainingStock(deliveryRequest, remainingQuantity);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("입고 수량과 납품 수량은 숫자여야 합니다.");
        }
    }
    private void updateRemainingStock(DeliveryRequest deliveryRequest, int remainingQuantity) {
        deliveryRequest.setDrNum(String.valueOf(remainingQuantity));
        deliveryRequestRepository.save(deliveryRequest);
    }

    private void updateInventoryStock(DeliveryRequest deliveryRequest, InputDTO inputDTO) {
        int ipTrueNum = Integer.parseInt(inputDTO.getIpTrueNum());

        String materialCode = deliveryRequest.getMaterial().getMCode();

        InventoryStock inventoryStock = inventoryStockRepository.findByMaterialCode(materialCode)
                .stream()
                .findFirst()
                .orElseThrow(() -> {
                    System.out.println("재고 정보를 찾을 수 없습니다. materialCode: " + materialCode);
                    return new RuntimeException("재고 정보를 찾을 수 없습니다.");
                });

        int currentStock = Integer.parseInt(inventoryStock.getIsNum());
        inventoryStock.setIsNum(String.valueOf(currentStock + ipTrueNum));

        int currentAvailable = Integer.parseInt(inventoryStock.getIsAvailable());
        inventoryStock.setIsAvailable(String.valueOf(currentAvailable + ipTrueNum));

        inventoryStockRepository.save(inventoryStock);
    }

    @Override
    public void removeInput(List<String> ipIds){
        if (ipIds == null || ipIds.isEmpty()) {
            throw new IllegalArgumentException("삭제할 입고 정보가 없습니다.");
        }

        for (String ipId : ipIds) {
            inputRepository.deleteById(ipId);// 개별적으로 삭제
        }
    }
}
