package org.zerock.b01.service;

import org.zerock.b01.domain.Bom;
import org.zerock.b01.dto.BomDTO;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.dto.ProductionPlanDTO;

import java.util.List;
import java.util.Map;

public interface BomService {
    List<BomDTO> getBoms();
    String registerBOM(BomDTO bomDTO, String uId);
    Map<String, Object> registerBomEasy(List<BomDTO> bomDTOs, String uId, boolean check);
    void modifyBOM(BomDTO bomDTO, Long bId);
    void removeBOM(List<Long> bIds);
}
