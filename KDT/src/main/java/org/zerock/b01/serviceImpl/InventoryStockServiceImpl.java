package org.zerock.b01.serviceImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Bom;
import org.zerock.b01.domain.InventoryStock;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.BomDTO;
import org.zerock.b01.dto.InventoryStockDTO;
import org.zerock.b01.repository.InventoryStockRepository;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.service.InventoryStockService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class InventoryStockServiceImpl implements InventoryStockService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private InventoryStockRepository inventoryStockRepository;

    @Override
    public List<InventoryStockDTO> getInventoryStockList(){
        List<InventoryStock> inventoryStockList = inventoryStockRepository.findAll();

        List<InventoryStockDTO> inventoryStockDTOList = new ArrayList<>();
        for(InventoryStock inventoryStock : inventoryStockList){
            InventoryStockDTO inventoryStockDTO = new InventoryStockDTO();
            inventoryStockDTO.setMCode(inventoryStock.getMaterial().getMCode());
            inventoryStockDTO.setMName(inventoryStock.getMaterial().getMName());
            inventoryStockDTO.setIsLocation(inventoryStock.getIsLocation());
            inventoryStockDTO.setIsNum(Integer.parseInt(inventoryStock.getIsNum()));
            inventoryStockDTO.setIsAvailable(Integer.parseInt(inventoryStock.getIsAvailable()));
            inventoryStockDTO.setPCode(inventoryStock.getMaterial().getProduct().getPCode());
            inventoryStockDTO.setPName(inventoryStock.getMaterial().getProduct().getPName());
            inventoryStockDTO.setIsComponentType(inventoryStock.getMaterial().getMComponentType());
            inventoryStockDTO.setRegDate(inventoryStock.getRegDate().toLocalDate());

            inventoryStockDTOList.add(inventoryStockDTO);
        }
        return inventoryStockDTOList;
    }

    @Override
    public void registerIS(InventoryStockDTO inventoryStockDTO){

        boolean exists = inventoryStockRepository.existsByMaterial_mCode(inventoryStockDTO.getMCode());
        if (exists) {
            throw new IllegalStateException("이미 등록된 자재입니다.");
        }

        InventoryStock inventoryStock = modelMapper.map(inventoryStockDTO, InventoryStock.class);

        Material material = materialRepository.findByMaterialCode(inventoryStockDTO.getMCode()).orElseThrow(() -> new RuntimeException("Material not found"));

        inventoryStock.setMaterial(material);

        inventoryStockRepository.save(inventoryStock);
    }

    @Override
    public void modifyIS(InventoryStockDTO inventoryStockDTO, Long isId){
        Optional<InventoryStock> result = inventoryStockRepository.findById(isId);
        InventoryStock inventoryStock = result.orElseThrow();

        Optional<Material> materialOptional = materialRepository.findById(inventoryStockDTO.getMCode());
        Material material = materialOptional.orElseThrow(() -> new EntityNotFoundException("Material not found"));
        inventoryStock.change(String.valueOf(inventoryStockDTO.getIsNum()), String.valueOf(inventoryStockDTO.getIsAvailable()), inventoryStockDTO.getIsLocation(), material);
        inventoryStockRepository.save(inventoryStock);
    }

    @Override
    public void removeIS(List<Long> isIds){
        if (isIds == null || isIds.isEmpty()) {
            throw new IllegalArgumentException("삭제할 자재 정보가 없습니다.");
        }

        for (Long isId : isIds) {
            inventoryStockRepository.deleteById(isId);
        }
    }

    @Override
    public List<InventoryStockDTO> findStockByMaterialCode(String mCode) {
        return inventoryStockRepository.findByMaterialCode(mCode)
                .stream()
                .filter(stock -> stock.getMaterial() != null) // Null 방지
                .map(stock -> new InventoryStockDTO(
                        stock.getMaterial().getMCode(),
                        stock.getMaterial().getMName(),
                        Integer.parseInt(stock.getIsNum()),
                        Integer.parseInt(stock.getIsAvailable())
                ))
                .collect(Collectors.toList());
    }
}
