package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.InputDTO;
import org.zerock.b01.dto.OutPutDTO;
import org.zerock.b01.repository.*;
import org.zerock.b01.service.OutputService;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class OutputServiceImpl implements OutputService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private OutputRepository outputRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Autowired
    private InventoryStockRepository inventoryStockRepository;

    @Override
    public List<OutPutDTO> getOutputs() {
        List<OutPut> outPutList = outputRepository.findAll();

        List<OutPutDTO> outPutDTOList = new ArrayList<>();

        for(OutPut output : outPutList) {
            OutPutDTO outputDTO = new OutPutDTO();
            outputDTO.setOpState(output.getOpState());
            outputDTO.setOpCode(output.getOpCode());
            outputDTO.setMCode(output.getMaterial().getMCode());
            outputDTO.setMName(output.getMaterial().getMName());
            outputDTO.setPpCode(output.getProductionPlan().getPpCode());
            outputDTO.setRegDate(output.getRegDate().toLocalDate());
            outputDTO.setOpANum(output.getOpANum());
            outPutDTOList.add(outputDTO);

        }

        return outPutDTOList;
    }

    private String generateNextOpCode(String latestOpCode) {

        String numberPart = latestOpCode.substring(3);
        int nextNumber = Integer.parseInt(numberPart) + 1;

        return String.format("OP-%03d", nextNumber);
    }

    @Override
    public void registerOutput(OutPutDTO outPutDTO) {
        OutPut output = modelMapper.map(outPutDTO, OutPut.class);

        Material material = materialRepository.findByMaterialCode(outPutDTO.getMCode()).orElseThrow(() -> new RuntimeException("Material not found"));
        output.setMaterial(material);

        ProductionPlan productionPlan = productionPlanRepository.findByProductionPlanCode(outPutDTO.getPpCode()).orElseThrow(() -> new RuntimeException("ProductionPlan not found"));
        output.setProductionPlan(productionPlan);

        String latestOpCode = outputRepository.findTopByOrderByOpCodeDesc()
                .map(OutPut::getOpCode)
                .orElse("OP-000"); //

        String newOpCode = generateNextOpCode(latestOpCode);
        output.setOpCode(newOpCode);

        int opANum = Integer.parseInt(outPutDTO.getOpANum());

        String mCode = outPutDTO.getMCode();

        List<InventoryStock> inventoryStockList = inventoryStockRepository.findByMaterialCode(mCode);

        if (inventoryStockList.isEmpty()) {
            throw new RuntimeException("Inventory stock not found for material code: " + mCode);
        }

        for (InventoryStock inventoryStock : inventoryStockList) {
            int availableQuantity = Integer.parseInt(inventoryStock.getIsAvailable());

            if (opANum > availableQuantity) {
                throw new RuntimeException("출고 수량이 가용 수량을 초과할 수 없습니다.");
            }
            inventoryStock.setIsAvailable(String.valueOf(availableQuantity - opANum));
            inventoryStockRepository.save(inventoryStock);
        }
        output.setOpState(CurrentStatus.ON_HOLD);
        outputRepository.save(output);
    }

    @Override
    public void removeOutput(List<String> opIds){
        if (opIds == null || opIds.isEmpty()) {
            throw new IllegalArgumentException("삭제할 입고 정보가 없습니다.");
        }

        for (String opId : opIds) {
            outputRepository.deleteById(opId);// 개별적으로 삭제
        }
    }

    @Transactional
    @Override
    public void confirmOutput(List<String> opIds){
        if (opIds == null || opIds.isEmpty()) {
            throw new IllegalArgumentException("삭제할 입고 정보가 없습니다.");
        }

        for (String opId : opIds) {
            OutPut outPut = outputRepository.findById(opId)
                    .orElseThrow(() -> new RuntimeException("출고 항목을 찾을 수 없습니다: " + opId));

            outPut.getProductionPlan().setPpState(CurrentStatus.OUTPUT);
            outPut.setOpState(CurrentStatus.OUTPUT_SUCCESS);

            int opANum = Integer.parseInt(outPut.getOpANum());
            String mCode = outPut.getMaterial().getMCode();

            InventoryStock inventoryStock = inventoryStockRepository.findByMaterialCode(mCode)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("해당 자재의 재고가 존재하지 않습니다: " + mCode));

            int currentStock = Integer.parseInt(inventoryStock.getIsNum());
            if (currentStock < opANum) {
                throw new RuntimeException("출고 수량이 현재 재고보다 많습니다.");
            }

            inventoryStock.setIsNum(String.valueOf(currentStock - opANum));
        }
    }
}
