package org.zerock.b01.service;

import org.zerock.b01.dto.BomDTO;
import org.zerock.b01.dto.InventoryStockDTO;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.dto.SupplierStockDTO;

import java.util.List;
import java.util.Map;

public interface SupplierStockService {
    void registerSStock(SupplierStockDTO supplierStockDTO);
    List<SupplierStockDTO> getSupplierStockList(Long sId);
    void modifySupplierStock(SupplierStockDTO supplierStockDTO, Long ssId);
    void removeSupplierStock(List<Long> ssIds);
    List<String> findMaterialNamesBySupplierId(Long sId);
    List<String> findAllMaterialNames();
//    Map<String, String[]> registerSStockAuto(List<SupplierStockDTO> stockDTOS);
//    Map<String, String[]> checkSStock(List<SupplierStockDTO> supplierStockDTOS);
}
