package org.zerock.b01.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.*;

import java.time.LocalDate;

public interface AllSearch {
    Page<ProductListAllDTO> productSearchWithAll(String[] types, String keyword, String pCode, String pName, Pageable pageable);
    Page<PlanListAllDTO> planSearchWithAll(String[] types, String keyword, String uId, String ppCode, String pName, String ppState, LocalDate ppStart, LocalDate ppEnd, Pageable pageable);
    Page<UserByAllDTO> userBySearchWithAll(String[] types, String keyword, String uName,
                                           String userJob, String userRank, LocalDate modDate, String status, String uId, Pageable pageable);
    Page<SupplierAllDTO> supplierSearchWithAll(String[] types, String keyword, String sName, String sRegNum, String sBusinessType, LocalDate sRegDate, String sStatus, String pageType, Pageable pageable);
    Page<UserByAllDTO> userBySearchWithAllList(String[] types, String keyword, String uName,
                                           String userJob, String userRank, LocalDate modDate, String status, String uId, Pageable pageable);

    Page<MaterialDTO> materialSearchWithAll(String[] types, String keyword, String pName, String componentType, String mName,
                                            String mCode, String mType, Pageable pageable);

    Page<BomDTO> bomSearchWithAll(String[] types, String keyword, String componentType, String mName, String pName, String uId, Pageable pageable);

    Page<OrderByListAllDTO> orderBySearchWithAll(String[] types, String keyword, String label, String oCode, LocalDate oRegDate, LocalDate oExpectDate, String sName, String mName, String oState, String uId, Pageable pageable);

    Page<InventoryStockDTO> inventoryStockSearchWithAll(String[] types, String keyword,
                                                        String pName, String componentType, String mName, String isLocation, LocalDate isRegDate, String uId, Pageable pageable);

    Page<DeliveryRequestDTO> deliveryRequestSearchWithAll(String[] types, String keyword, String mName, String sName, String drState, Pageable pageable);

    Page<InputDTO> inputSearchWithAll(String[] types, String keyword, String mName, String ipState, Pageable pageable);

    Page<DppListAllDTO> dppSearchWithAll(String[] types, String keyword, String dppCode, String ppCode, String mName, String mCode, String sName, Long dppNum, String pName, LocalDate dppRegDate, String dppState, String uId, Pageable pageable);

    Page<OutPutDTO> outputSearchWithAll(String[] types, String keyword, String pName, String mName, String opState, Pageable pageable);

    Page<SupplierStockDTO> supplierStockSearchWithAll(String[] types, String keyword, String pName, String mName, Long sId, Pageable pageable);

    Page<ProgressInspectionDTO> progressInspectionSearchWithAll(String[] types, String keyword, String mName, LocalDate psDate, String psState, Long sId, Pageable pageable);

    Page<OrderByListAllDTO> orderBySearchSidWithAll(String[] types, String keyword, LocalDate oRegDate, LocalDate oExpectDate, String sName, String mName, String oState, Long sId, Pageable pageable);

    Page<DeliveryRequestDTO> supplierDeliveryRequestSearchWithAll(String[] types, String keyword, String mName, String sName, Long sId, String drState, Pageable pageable);


    Page<ProgressInspectionDTO> supplierProgressInspectionSearchWithAll(String[] types, String keyword, String mName, LocalDate psDate, String psState, Long sId, Pageable pageable);

    Page<ReturnByDTO> supplierReturnByWithAll(String[] types, String keyword, String mName, Long sId, Pageable pageable);
}
