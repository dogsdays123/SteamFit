package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.ReturnByDTO;
import org.zerock.b01.repository.InputRepository;
import org.zerock.b01.repository.InventoryStockRepository;
import org.zerock.b01.repository.ReturnByRepository;
import org.zerock.b01.service.ReturnService;

import java.util.List;

@Log4j2
@Service
public class ReturnServiceImpl implements ReturnService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private InputRepository inputRepository;

    @Autowired
    private ReturnByRepository returnByRepository;
    @Autowired
    private InventoryStockRepository inventoryStockRepository;


    @Override
    public void returnInput(ReturnByDTO returnByDTO){

        ReturnBy returnBy = modelMapper.map(returnByDTO, ReturnBy.class);

        InPut input = inputRepository.findById(returnByDTO.getIpCode()).orElseThrow(()->new RuntimeException("Input not found"));
        input.setIpState(CurrentStatus.RETURNED_REQUESTED);
        returnBy.setInPut(input);
        returnBy.setRState(CurrentStatus.ON_HOLD);

        returnByRepository.save(returnBy);
    }


    @Override
    @Transactional
    public void reDelivery(ReturnByDTO returnByDTO) {
        // 1. ReturnBy 기존 객체 가져오기
        ReturnBy returnBy = returnByRepository.findById(returnByDTO.getRId()).orElseThrow(()->new RuntimeException("Return not found"));

        // 2. Input 엔티티 가져오기
        InPut input = inputRepository.findById(returnByDTO.getIpCode())
                .orElseThrow(() -> new RuntimeException("Input not found"));

        int falseNum = Integer.parseInt(input.getIpFalseNum());
        int trueNum = Integer.parseInt(input.getIpTrueNum());
        int total = trueNum + falseNum;

        // 3. Input 업데이트
        input.setIpTrueNum(String.valueOf(total));
        input.setIpFalseNum("0");
        input.setIpState(CurrentStatus.INPUT_SUCCESS);

        // 4. 재고 업데이트
        String materialCode = input.getDeliveryRequest().getMaterial().getMCode();
        InventoryStock inventoryStock = inventoryStockRepository
                .findByMaterial_mCode(materialCode)
                .orElseThrow(() -> new RuntimeException("해당 자재의 재고가 존재하지 않습니다."));

        int currentQty = Integer.parseInt(inventoryStock.getIsNum());
        inventoryStock.setIsNum(String.valueOf(currentQty + falseNum));
        int currentAvQty = Integer.parseInt(inventoryStock.getIsAvailable());
        inventoryStock.setIsAvailable(String.valueOf(currentAvQty + falseNum));

        // 5. ReturnBy 업데이트
        returnBy.setInPut(input);
        returnBy.setRState(CurrentStatus.DELIVERY_DELIVERED);
        returnBy.setRNum(0L); // 수량 0 처리 등

        // 6. 저장
        inventoryStockRepository.save(inventoryStock);
        inputRepository.save(input);
        returnByRepository.save(returnBy); // 이제는 update 처리됨
    }

    @Override
    public void removeReturn(List<Long> rIds){
        if (rIds == null || rIds.isEmpty()) {
            throw new IllegalArgumentException("삭제할 반품 정보가 없습니다.");
        }

        for (Long rId : rIds) {
            returnByRepository.deleteById(rId);// 개별적으로 삭제
        }
    }

}
