package org.zerock.b01.service;

import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.*;

public interface PageService {
    PageResponseDTO<PlanListAllDTO> planListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<ProductListAllDTO> productListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<MaterialDTO> materialListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<UserByAllDTO> userByWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<SupplierAllDTO> supplierWithAll(PageRequestDTO pageRequestDTO, String pageType);
    PageResponseDTO<UserByAllDTO> userByWithAllList(PageRequestDTO pageRequestDTO);
    PageResponseDTO<BomDTO> bomListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<OrderByListAllDTO> orderByWithAll(PageRequestDTO pageRequestDTO, String labels);

    PageResponseDTO<InventoryStockDTO> inventoryStockWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<DeliveryRequestDTO> deliveryRequestWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<InputDTO> inputWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<OutPutDTO> outputWithAll(PageRequestDTO pageRequestDTO);

    PageResponseDTO<DppListAllDTO> dppListWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<SupplierStockDTO> supplierStockWithAll(PageRequestDTO pageRequestDTO, Long sId);
    PageResponseDTO<SupplierStockDTO> adminSupplierStockWithAll(PageRequestDTO pageRequestDTO);
    PageResponseDTO<ProgressInspectionDTO> progressInspectionWithAll(PageRequestDTO pageRequestDTO, Long sId);
    PageResponseDTO<ProgressInspectionDTO> adminProgressInspectionWithAll(PageRequestDTO pageRequestDTO);


    PageResponseDTO<OrderByListAllDTO> orderByWithSidAll(PageRequestDTO pageRequestDTO, Long sId);
    PageResponseDTO<DeliveryRequestDTO> supplierDeliveryRequestWithAll(PageRequestDTO pageRequestDTO, Long sId);

    PageResponseDTO<ProgressInspectionDTO> supplierProgressInspectionWithAll(PageRequestDTO pageRequestDTO, Long sId);
    PageResponseDTO<ReturnByDTO> supplierReturnByWithAll(PageRequestDTO pageRequestDTO, Long sId);
    PageResponseDTO<ReturnByDTO> returnByWithAll(PageRequestDTO pageRequestDTO);
}
