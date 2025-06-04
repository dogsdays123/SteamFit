package org.zerock.b01.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.SupplierStock;
import org.zerock.b01.dto.*;

import org.zerock.b01.dto.allDTO.*;
import org.zerock.b01.dto.allDTO.DppListAllDTO;


import org.zerock.b01.repository.*;
import org.zerock.b01.service.PageService;

import javax.sql.CommonDataSource;
import java.time.LocalDate;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class PageServiceImpl implements PageService {

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final MaterialRepository materialRepository;

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Autowired
    private final BomRepository bomRepository;

    @Autowired
    private final InventoryStockRepository inventoryStockRepository;

    @Autowired
    private CommonDataSource commonDataSource;
    @Autowired
    private DeliveryRequestRepository deliveryRequestRepository;

    @Autowired
    private InputRepository inputRepository;
    @Autowired
    private final DeliveryProcurementPlanRepository dppRepository;

    @Autowired
    private OutputRepository outputRepository;

    @Autowired
    private OrderByRepository orderByRepository;

    @Autowired
    private SupplierStockRepository supplierStockRepository;
    @Autowired
    private ProgressInspectionRepository progressInspectionRepository;
    @Autowired
    private ReturnByRepository returnByRepository;

    @Override
    public PageResponseDTO<PlanListAllDTO> planListWithAll(PageRequestDTO pageRequestDTO){
        log.info(">>>> 요청된 페이지 번호: {}", pageRequestDTO.getPage());
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String ppCode = pageRequestDTO.getPpCode();
        String pCode = pageRequestDTO.getPCode();
        String ppNum = pageRequestDTO.getPpNum();
        String pName = pageRequestDTO.getPName();
        String uName = pageRequestDTO.getUName();
        String ppState = pageRequestDTO.getPpState();
        String uId = pageRequestDTO.getUId();
        LocalDate ppStart = pageRequestDTO.getPpStart();
        LocalDate ppEnd = pageRequestDTO.getPpEnd();
        LocalDate regDate = pageRequestDTO.getPpRegDate();

        Pageable pageable = pageRequestDTO.getPageable("regDate");

        Page<PlanListAllDTO> result = productionPlanRepository
                .planSearchWithAll(types, keyword, uId, ppCode, pName,
                       ppState, ppStart, ppEnd, pageable);

        return PageResponseDTO.<PlanListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<ProductListAllDTO> productListWithAll(PageRequestDTO pageRequestDTO){
        log.info(">>>> 요청된 페이지 번호: {}", pageRequestDTO.getPpStart());
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String pCode = pageRequestDTO.getPCode();
        String pName = pageRequestDTO.getPName();
        String uName = pageRequestDTO.getUName();
        LocalDate regDate = pageRequestDTO.getPReg();

        Pageable pageable = pageRequestDTO.getPageable("regDate");

        Page<ProductListAllDTO> result = productRepository.productSearchWithAll(types, keyword, pCode, pName, pageable);

        return PageResponseDTO.<ProductListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<MaterialDTO> materialListWithAll(PageRequestDTO pageRequestDTO){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String pName = pageRequestDTO.getPName();
        String componentType = pageRequestDTO.getComponentType();
        String mName = pageRequestDTO.getMName();
        String mCode = pageRequestDTO.getMCode();
        String mType = pageRequestDTO.getMType();

        Pageable pageable = pageRequestDTO.getPageable("mCode");

        Page<MaterialDTO> result = materialRepository.materialSearchWithAll(types, keyword, pName, componentType, mName,
                mCode, mType, pageable);

        return PageResponseDTO.<MaterialDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<UserByAllDTO> userByWithAll(PageRequestDTO pageRequestDTO){
        log.info(">>>> user 페이지 번호: {}", pageRequestDTO.getPage());
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String uName = pageRequestDTO.getUName();
        String userJob = pageRequestDTO.getUserJob();
        String userRank = pageRequestDTO.getUserRank();
        LocalDate regDate = pageRequestDTO.getURegDate();
        String status = pageRequestDTO.getStatus();
        String uId = pageRequestDTO.getUId();

        Pageable pageable = pageRequestDTO.getPageable("uId");

        Page<UserByAllDTO> result = productRepository.userBySearchWithAll(types, keyword, uName, userJob, userRank, regDate, status, uId, pageable);

        return PageResponseDTO.<UserByAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<SupplierAllDTO> supplierWithAll(PageRequestDTO pageRequestDTO, String pageType){
        log.info(">>>> supplier 페이지 번호: {}", pageRequestDTO.getPage());
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String sName = pageRequestDTO.getUName();
        String sRegNum = pageRequestDTO.getUserJob();
        String sBusinessType = pageRequestDTO.getUserRank();
        LocalDate sRegDate = pageRequestDTO.getSRegDate();
        String uId = pageRequestDTO.getUId();
        String sStatus = pageRequestDTO.getStatus();

        Pageable pageable = pageRequestDTO.getPageable("sId");

        Page<SupplierAllDTO> result = productRepository.supplierSearchWithAll(types, keyword, sName, sRegNum, sBusinessType, sRegDate, sStatus, pageType, pageable);

        return PageResponseDTO.<SupplierAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<UserByAllDTO> userByWithAllList(PageRequestDTO pageRequestDTO){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String uName = pageRequestDTO.getUName();
        String userJob = pageRequestDTO.getUserJob();
        String userRank = pageRequestDTO.getUserRank();
        LocalDate regDate = pageRequestDTO.getURegDate();
        String status = pageRequestDTO.getStatus();
        String uId = pageRequestDTO.getUId();

        Pageable pageable = pageRequestDTO.getPageable("uId");

        Page<UserByAllDTO> result = productRepository.userBySearchWithAllList(types, keyword, uName, userJob, userRank, regDate, status, uId, pageable);

        return PageResponseDTO.<UserByAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<BomDTO> bomListWithAll(PageRequestDTO pageRequestDTO){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String componentType = pageRequestDTO.getComponentType();
        String mName = pageRequestDTO.getMName();
        String pName = pageRequestDTO.getPName();
        String uId = pageRequestDTO.getUId();

        Pageable pageable = pageRequestDTO.getPageable("uId");

        Page<BomDTO> result = bomRepository.bomSearchWithAll(types, keyword, componentType, mName, pName, uId, pageable);

        return PageResponseDTO.<BomDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<OrderByListAllDTO> orderByWithAll(PageRequestDTO pageRequestDTO, String labels){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        LocalDate oRegDate = pageRequestDTO.getORegDate();
        LocalDate oExpectDate = pageRequestDTO.getOExpectDate();
        String sName = pageRequestDTO.getSName();
        String mName = pageRequestDTO.getMName();
        String oState = pageRequestDTO.getOState();
        String uId = pageRequestDTO.getUId();
        String oCode = pageRequestDTO.getObCode();

        Pageable pageable = pageRequestDTO.getPageable("uId");

        Page<OrderByListAllDTO> result = orderByRepository.orderBySearchWithAll
                (types, keyword, labels, oCode, oRegDate, oExpectDate, sName, mName, oState, uId, pageable);

        return PageResponseDTO.<OrderByListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<InventoryStockDTO> inventoryStockWithAll(PageRequestDTO pageRequestDTO){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String pName = pageRequestDTO.getPName();
        String componentType = pageRequestDTO.getComponentType();
        String mName = pageRequestDTO.getMName();
        String isLocation = pageRequestDTO.getIsLocation();
        LocalDate regDate = pageRequestDTO.getIsRegDate();
        String uId = pageRequestDTO.getUId();

        Pageable pageable = pageRequestDTO.getPageable("regDate");

        Page<InventoryStockDTO> result = inventoryStockRepository.inventoryStockSearchWithAll(types, keyword,
                pName, componentType, mName, isLocation, regDate, uId, pageable);

        for (InventoryStockDTO inventoryStockDTO : result.getContent()) {
            Material material = materialRepository.findByMaterialCode(inventoryStockDTO.getMCode())
                    .orElseThrow(() -> new RuntimeException("Material not found"));
            inventoryStockDTO.setMName(material.getMName()); //

            if (material.getProduct() != null) {
                inventoryStockDTO.setPCode(material.getProduct().getPCode());
                inventoryStockDTO.setPName(material.getProduct().getPName());
                inventoryStockDTO.setIsComponentType(material.getMComponentType());
            }
        }

        return PageResponseDTO.<InventoryStockDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<DeliveryRequestDTO> deliveryRequestWithAll(PageRequestDTO pageRequestDTO){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String mName = pageRequestDTO.getMName();
        String sName = pageRequestDTO.getSName();
        String drState = pageRequestDTO.getDrState();
        LocalDate regDate = pageRequestDTO.getIsRegDate();

        Pageable pageable = pageRequestDTO.getPageable("regDate");

        Page<DeliveryRequestDTO> result = deliveryRequestRepository.deliveryRequestSearchWithAll(types, keyword,
                mName, sName, drState, pageable);

        for (DeliveryRequestDTO deliveryRequestDTO : result.getContent()) {
            Material material = materialRepository.findByMaterialCode(deliveryRequestDTO.getMCode())
                    .orElseThrow(() -> new RuntimeException("Material not found"));
            deliveryRequestDTO.setMName(material.getMName()); //

        }

        return PageResponseDTO.<DeliveryRequestDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<InputDTO> inputWithAll(PageRequestDTO pageRequestDTO){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String mName = pageRequestDTO.getMName();
        String ipState = pageRequestDTO.getIpState();

        Pageable pageable = pageRequestDTO.getPageable("regDate");

        Page<InputDTO> result = inputRepository.inputSearchWithAll(types, keyword, mName, ipState, pageable);

        for(InputDTO inputDTO : result.getContent()) {
            Material material = materialRepository.findByMaterialCode(inputDTO.getMCode())
                    .orElseThrow(() -> new RuntimeException("Material not found"));
            inputDTO.setMName(material.getMName()); //
        }
        return PageResponseDTO.<InputDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    public PageResponseDTO<DppListAllDTO> dppListWithAll(PageRequestDTO pageRequestDTO){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String dppCode = pageRequestDTO.getDppCode();
        String ppCode = pageRequestDTO.getPpCode();
        String mName = pageRequestDTO.getMName();
        String mCode = pageRequestDTO.getMCode();
        String sName = pageRequestDTO.getSName();
        String pName = pageRequestDTO.getPName();
        Long dppNum = pageRequestDTO.getDppNum();
        LocalDate dppDate = pageRequestDTO.getDppDate();
        String dppState = pageRequestDTO.getDppState();
        String uId = pageRequestDTO.getUId();

        Pageable pageable = pageRequestDTO.getPageable("uId");

        Page<DppListAllDTO> result = dppRepository.dppSearchWithAll(types, keyword, dppCode, ppCode, mName, mCode, sName, dppNum, pName, dppDate, dppState, uId, pageable);

        return PageResponseDTO.<DppListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<OutPutDTO> outputWithAll(PageRequestDTO pageRequestDTO){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String pName = pageRequestDTO.getPName();
        String mName = pageRequestDTO.getMName();
        String opState = pageRequestDTO.getOpState();

        Pageable pageable = pageRequestDTO.getPageable("regDate");

        Page<OutPutDTO> result = outputRepository.outputSearchWithAll(types, keyword, pName, mName, opState, pageable);

        for(OutPutDTO outPutDTO : result.getContent()) {
            Material material = materialRepository.findByMaterialCode(outPutDTO.getMCode())
                    .orElseThrow(() -> new RuntimeException("Material not found"));
            outPutDTO.setMName(material.getMName()); //
        }

        return PageResponseDTO.<OutPutDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<SupplierStockDTO> supplierStockWithAll(PageRequestDTO pageRequestDTO, Long sId){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String pName = pageRequestDTO.getPName();
        String mName = pageRequestDTO.getMName();

        Pageable pageable = pageRequestDTO.getPageable("regDate");

        Page<SupplierStockDTO> result = supplierStockRepository.supplierStockSearchWithAll(types, keyword, pName, mName, sId, pageable);

        for(SupplierStockDTO supplierStockDTO : result.getContent()) {
            Material material = materialRepository.findByMaterialCode(supplierStockDTO.getMCode())
                    .orElseThrow(() -> new RuntimeException("Material not found"));
            supplierStockDTO.setMName(material.getMName()); //
        }

        return PageResponseDTO.<SupplierStockDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<SupplierStockDTO> adminSupplierStockWithAll(PageRequestDTO pageRequestDTO) {
        return supplierStockWithAll(pageRequestDTO, null);
    }


    @Override
    public PageResponseDTO<ProgressInspectionDTO> progressInspectionWithAll(PageRequestDTO pageRequestDTO, Long sId){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String mName = pageRequestDTO.getMName();
        LocalDate psDate = pageRequestDTO.getPsDate();
        String psState = pageRequestDTO.getPsState();

        Pageable pageable = pageRequestDTO.getPageable("regDate");

        Page<ProgressInspectionDTO> result = progressInspectionRepository.progressInspectionSearchWithAll(types, keyword, mName, psDate, psState, sId, pageable);

        return PageResponseDTO.<ProgressInspectionDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<ProgressInspectionDTO> adminProgressInspectionWithAll(PageRequestDTO pageRequestDTO) {
        return progressInspectionWithAll(pageRequestDTO, null);
    }

    @Override
    public PageResponseDTO<OrderByListAllDTO> orderByWithSidAll(PageRequestDTO pageRequestDTO, Long sId){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String mName = pageRequestDTO.getMName();
        LocalDate oRegDate = pageRequestDTO.getORegDate();
        LocalDate oExpectDate = pageRequestDTO.getOExpectDate();
        String sName = pageRequestDTO.getSName();
        String oState = pageRequestDTO.getOState();

        Pageable pageable = pageRequestDTO.getPageable("regDate");

        Page<OrderByListAllDTO> result =
                orderByRepository.orderBySearchSidWithAll(types, keyword, oRegDate, oExpectDate, sName, mName, oState, sId, pageable);

        return PageResponseDTO.<OrderByListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<DeliveryRequestDTO> supplierDeliveryRequestWithAll(PageRequestDTO pageRequestDTO, Long sId) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String mName = pageRequestDTO.getMName();
        String sName = pageRequestDTO.getSName();
        String drState = pageRequestDTO.getDrState();
        LocalDate regDate = pageRequestDTO.getIsRegDate();

        Pageable pageable = pageRequestDTO.getPageable("regDate");

        Page<DeliveryRequestDTO> result = deliveryRequestRepository.supplierDeliveryRequestSearchWithAll(types, keyword,
                mName, sName, sId, drState, pageable);

        for (DeliveryRequestDTO deliveryRequestDTO : result.getContent()) {
            Material material = materialRepository.findByMaterialCode(deliveryRequestDTO.getMCode())
                    .orElseThrow(() -> new RuntimeException("Material not found"));
            deliveryRequestDTO.setMName(material.getMName()); //

        }

        return PageResponseDTO.<DeliveryRequestDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<ProgressInspectionDTO> supplierProgressInspectionWithAll(PageRequestDTO pageRequestDTO, Long sId){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String mName = pageRequestDTO.getMName();
        LocalDate psDate = pageRequestDTO.getPsDate();
        String psState = pageRequestDTO.getPsState();

        Pageable pageable = pageRequestDTO.getPageable("regDate");

        Page<ProgressInspectionDTO> result = progressInspectionRepository.supplierProgressInspectionSearchWithAll(types, keyword, mName, psDate, psState, sId, pageable);

        return PageResponseDTO.<ProgressInspectionDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<ReturnByDTO> supplierReturnByWithAll(PageRequestDTO pageRequestDTO, Long sId){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String mName = pageRequestDTO.getMName();

        Pageable pageable = pageRequestDTO.getPageable("regDate");

        Page<ReturnByDTO> result = returnByRepository.supplierReturnByWithAll(types, keyword, mName, sId, pageable);

        return PageResponseDTO.<ReturnByDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<ReturnByDTO> returnByWithAll(PageRequestDTO pageRequestDTO) {
        return supplierReturnByWithAll(pageRequestDTO, null);
    }
}
