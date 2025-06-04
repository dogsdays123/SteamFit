package org.zerock.b01.service;

import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.dto.ProductDTO;

import java.util.List;
import java.util.Map;

public interface MaterialService {
    List<Material> getMaterials();
    String registerMaterial(MaterialDTO materialDTO, String uId);
    void modifyMaterial(MaterialDTO materialDTO, String uName);
    void removeMaterial(List<String> mCodes);
    Map<String, Object> registerMaterialEasy(List<MaterialDTO> materialDTOs, String uId, boolean check);
    List<Material> getMaterialByPName(String pCode);
    List<String> getComponentTypesByProductCode(String pCode);
    List<Material> getMaterialByComponentType(String componentType);
    String findCodeByName(String mName);
}
