package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.BomDTO;
import org.zerock.b01.dto.InventoryStockDTO;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.dto.SupplierStockDTO;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.SupplierStockRepository;
import org.zerock.b01.service.SupplierStockService;

import java.util.*;

@Log4j2
@Service
public class SupplierStockServiceImpl implements SupplierStockService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private SupplierStockRepository supplierStockRepository;


    @Override
    public List<SupplierStockDTO> getSupplierStockList(Long sId){
//        List<SupplierStock> supplierStockList = supplierStockRepository.findAll();
        List<SupplierStock> supplierStockList = supplierStockRepository.findAllBySupplierId(sId);

        List<SupplierStockDTO> supplierStockDTOList = new ArrayList<>();
        for(SupplierStock supplierStock : supplierStockList){
            SupplierStockDTO supplierStockDTO = new SupplierStockDTO();
            supplierStockDTO.setSsId(supplierStock.getSsId());
            supplierStockDTO.setSId(supplierStock.getSupplier().getSId());
            supplierStockDTO.setSsNum(supplierStock.getSsNum());
            supplierStockDTO.setRegDate(supplierStock.getRegDate().toLocalDate());
            supplierStockDTO.setMCode(supplierStock.getMaterial().getMCode());
            supplierStockDTO.setMName(supplierStock.getMaterial().getMName());
            supplierStockDTO.setLeadTime(supplierStock.getLeadTime());
            supplierStockDTO.setSsMinOrderQty(supplierStock.getSsMinOrderQty());
            supplierStockDTO.setUnitPrice(supplierStock.getUnitPrice());
            supplierStockDTOList.add(supplierStockDTO);
        }
        return supplierStockDTOList;
    }

    @Override
    public void registerSStock(SupplierStockDTO supplierStockDTO){

        boolean exists = supplierStockRepository.existsBySupplier_sIdAndMaterial_mCode(
                supplierStockDTO.getSId(), supplierStockDTO.getMCode()
        );
        if (exists) {
            throw new IllegalStateException("이미 등록된 자재입니다: " + supplierStockDTO.getMCode());
        }

        SupplierStock supplierStock = modelMapper.map(supplierStockDTO, SupplierStock.class);

        Material material = materialRepository.findByMaterialCode(supplierStockDTO.getMCode())
                .orElseThrow(() -> new IllegalArgumentException("해당 자재가 존재하지 않습니다: " + supplierStockDTO.getMCode()));

        supplierStock.setMaterial(material);

        supplierStockRepository.save(supplierStock);
    }

    @Override
    public void modifySupplierStock(SupplierStockDTO supplierStockDTO, Long ssId){
        Optional<SupplierStock> result = supplierStockRepository.findById(ssId);
        SupplierStock supplierStock = result.orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplierStock.change(supplierStockDTO.getSsNum(), supplierStockDTO.getSsMinOrderQty(), supplierStockDTO.getUnitPrice(), supplierStockDTO.getLeadTime());

        supplierStockRepository.save(supplierStock);

    }

    @Override
    public void removeSupplierStock(List<Long> ssIds){
        if (ssIds == null || ssIds.isEmpty()) {
            throw new IllegalArgumentException("삭제할 자재 정보가 없습니다.");
        }

        for (Long ssId : ssIds) {
            supplierStockRepository.deleteById(ssId);
        }
    }

    @Override
    public List<String> findAllMaterialNames() {
        return supplierStockRepository.findAllDistinctMaterialNames();
    }

    @Override
    public List<String> findMaterialNamesBySupplierId(Long sId) {
        return supplierStockRepository.findDistinctMaterialNamesBySupplierId(sId);
    }
}
