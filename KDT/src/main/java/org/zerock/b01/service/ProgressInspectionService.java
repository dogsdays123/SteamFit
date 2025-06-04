package org.zerock.b01.service;

import org.zerock.b01.dto.InventoryStockDTO;
import org.zerock.b01.dto.ProgressInspectionDTO;

import java.util.List;

public interface ProgressInspectionService {
    Boolean register(ProgressInspectionDTO piDTO);
    void piAgree(ProgressInspectionDTO piDTO, List<Long> psIds);
    void piRemove(ProgressInspectionDTO piDTO, List<Long> psIdss);
}
