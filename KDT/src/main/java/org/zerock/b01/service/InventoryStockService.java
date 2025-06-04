package org.zerock.b01.service;

import org.zerock.b01.dto.BomDTO;
import org.zerock.b01.dto.InventoryStockDTO;

import java.util.List;

public interface InventoryStockService {
    List<InventoryStockDTO> getInventoryStockList();
    void registerIS(InventoryStockDTO inventoryStockDTO);
    void modifyIS(InventoryStockDTO inventoryStockDTO, Long isId);
    void removeIS(List<Long> isIds);
    List<InventoryStockDTO> findStockByMaterialCode(String mCode);
}
