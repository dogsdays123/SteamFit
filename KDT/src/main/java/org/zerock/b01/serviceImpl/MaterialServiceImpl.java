package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.dto.ProductDTO;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.MaterialService;

import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.*;


@Log4j2
@Service
public class MaterialServiceImpl implements MaterialService {
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private UserByRepository userByRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    AutoGenerateCode autoGenerateCode;

    @Override
    public List<Material> getMaterials() {
        return materialRepository.findAll();
    }

    @Override
    public List<Material> getMaterialByPName(String pCode){
        return materialRepository.findByProductCode(pCode);
    }

    @Override
    public List<String> getComponentTypesByProductCode(String pCode) {
        return materialRepository.findComponentTypesByProductCode(pCode);
    }

    @Override
    public List<Material> getMaterialByComponentType(String componentType) {
        return materialRepository.findByComponentType(componentType);
    }

    @Override
    public String registerMaterial(MaterialDTO materialDTO, String uId){
        Material material = modelMapper.map(materialDTO, Material.class);
        Product product = materialRepository.findByProduct(materialDTO.getPName());
        material.setUserBy(userByRepository.findByUId(uId));
        log.info(uId);

        String errorMessage = null;

        if(materialRepository.findByOtherName(materialDTO.getPName(), materialDTO.getMName()) != null){
            errorMessage = "[" + materialDTO.getMName() + "]";
        }
        else{
            log.info("NewNoHave " + material.getMCode());
            String mCode =  autoGenerateCode.generateCode("m", "");
            material.setOneCode(mCode);
            material.setProduct(product);
            materialRepository.save(material);
        }

        return errorMessage;
    }

    @Override
    public Map<String, Object> registerMaterialEasy(List<MaterialDTO> materialDTOs, String uId, boolean check){
        List<Map<String, String>> duplicate = new ArrayList<>();
        List<String> errorCheck = new ArrayList<>();

        //돌아라돌아라
        for (MaterialDTO materialDTO : materialDTOs) {
            //만약 엑셀에 들어온 제품이 등록되지 않은 제품이라면 error로 저장
            if(productRepository.findByProductNameObj(materialDTO.getPName()) == null){
                errorCheck.add(materialDTO.getPName());
                continue;
            }

            //만약 이미 존재하는 부품 이름이라면 duplicate에 이름 저장
            if(materialRepository.findByOtherName(materialDTO.getPName(), materialDTO.getMName()) != null){
                Map<String, String> dupEntry = new HashMap<>();
                dupEntry.put("pName", materialDTO.getPName());
                dupEntry.put("mName", materialDTO.getMName());
                duplicate.add(dupEntry);
                continue;
            }

            //그게 아니면 정상영업합니다.
            if(!check){
                Material material = modelMapper.map(materialDTO, Material.class);
                material.setProduct(productRepository.findByProductNameObj(materialDTO.getPName()));
                material.setUserBy(userByRepository.findByUId(uId));

                material.setMCode(autoGenerateCode.generateCode("m", ""));
                materialRepository.save(material);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("errorCheck", errorCheck);
        result.put("duplicate", duplicate);

        return result;
    }

    @Override
    public void modifyMaterial(MaterialDTO materialDTO, String uName){
        Optional<Material> result = materialRepository.findByMaterialCode(materialDTO.getMCode());
        Material material = result.orElseThrow();
        material.change(materialDTO.getMComponentType(), materialDTO.getMType(), materialDTO.getMName(), materialDTO.getMMinNum(), materialDTO.getMUnitPrice(),
                materialDTO.getMDepth(), materialDTO.getMHeight(), materialDTO.getMWidth(),
                materialDTO.getMWeight());

        materialRepository.save(material);
    }

    @Override
    public void removeMaterial(List<String> pCodes){
        if (pCodes == null || pCodes.isEmpty()) {
            throw new IllegalArgumentException("삭제할 생산 계획 코드가 없습니다.");
        }

        for (String pCode : pCodes) {
            materialRepository.deleteById(pCode); // 개별적으로 삭제
        }
    }

    @Override
    public String findCodeByName(String mName) {
        Material material = materialRepository.findByMName(mName)
                .orElseThrow(() -> new IllegalArgumentException("해당 자재명이 존재하지 않습니다: " + mName));
        return material.getMCode();
    }

}
