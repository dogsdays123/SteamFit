package org.zerock.b01.serviceImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.BomDTO;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.dto.ProductionPlanDTO;
import org.zerock.b01.repository.BomRepository;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.BomService;
import org.zerock.b01.service.NoticeService;

import java.util.*;

@Log4j2
@Service
public class BomServiceImpl implements BomService {
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private BomRepository bomRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private UserByRepository userByRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NoticeService noticeService;

    public List<BomDTO> getBoms() {
        List<Bom> bomList = bomRepository.findAll();

        List<BomDTO> bomDTOList = new ArrayList<>();
        for (Bom bom : bomList) {
            BomDTO bomDTO = new BomDTO();
            bomDTO.setPName(bom.getProduct().getPName());
            bomDTO.setMName(bom.getMaterial().getMName());
            bomDTO.setMComponentType(bom.getMaterial().getMComponentType());
            bomDTO.setBRequireNum(bom.getBRequireNum());

            bomDTOList.add(bomDTO);
        }

        return bomDTOList;
    }

    @Override
    public String registerBOM(BomDTO bomDTO, String uId){
        Bom bom = modelMapper.map(bomDTO, Bom.class);
        Product product = productRepository.findByProductName(bomDTO.getPName()).orElseThrow();
        Material material = materialRepository.findByMaterialName(bomDTO.getMName()).orElseThrow();
        bom.setUserBy(userByRepository.findByUId(uId));

        String errorMessage = null;

        if(bomRepository.findByOthers(bomDTO.getPName(), bomDTO.getMName()) != null){
            errorMessage = "[" + bomDTO.getMName() + "]";
        }
        else{
            bom.setMaterial(material);
            bom.setProduct(product);
            bomRepository.save(bom);
        }

        return errorMessage;
    }

    @Override
    public Map<String, Object> registerBomEasy(List<BomDTO> bomDTOs, String uId, boolean check){
        List<Map<String, String>> duplicate = new ArrayList<>();
        List<String> errorCheck = new ArrayList<>();
        UserBy user = userByRepository.findByUId(uId);

        //돌아라돌아라
        for (BomDTO bomDTO : bomDTOs) {
            Bom bom = modelMapper.map(bomDTO, Bom.class);
            bom.setUserBy(user);

            //붐에 중복된 값과, 저장할 값을 각각 찾을
            Bom duplicateB = bomRepository.findByOthers(bomDTO.getPName(), bomDTO.getMName());
            Material saveM =materialRepository.findByOtherName(bomDTO.getPName(), bomDTO.getMName());

            //만약 엑셀에 들어온 제품이 등록되지 않은 제품이라면 error로 저장
            if (productRepository.findByProductNameObj(bomDTO.getPName()) == null) {
                errorCheck.add(bomDTO.getPName());
                continue;
            }

            //중복된 경우가 있으면 중복체크
            if(duplicateB != null){
                Map<String, String> dupEntry = new HashMap<>();
                dupEntry.put("pName", bomDTO.getPName());
                dupEntry.put("mName", bomDTO.getMName());
                duplicate.add(dupEntry);
                continue;
            }

            if(!check){
                bom.setProduct(productRepository.findByProductNameObj(bomDTO.getPName()));
                bom.setMaterial(saveM);
                bomRepository.save(bom);
                }
            }

        Map<String, Object> result = new HashMap<>();
        result.put("errorCheck", errorCheck);
        result.put("duplicate", duplicate);

        return result;
    }

    @Override
    public void modifyBOM(BomDTO bomDTO, Long bId){
        Optional<Bom> result = bomRepository.findById(bId);
        Bom bom = result.orElseThrow();

        Optional<Material> materialOptional = materialRepository.findByMaterialName(bomDTO.getMName());
        Material material = materialOptional.orElseThrow(() -> new EntityNotFoundException("Material not found"));
        bom.change(bomDTO.getBRequireNum(), material);
        bomRepository.save(bom);
    }

    @Override
    public void removeBOM(List<Long> bIds){
        if (bIds == null || bIds.isEmpty()) {
            throw new IllegalArgumentException("삭제할 BOM이 없습니다.");
        }

        for (Long bId : bIds) {
            bomRepository.deleteById(bId);// 개별적으로 삭제
        }
    }
}
