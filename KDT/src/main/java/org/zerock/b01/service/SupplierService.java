package org.zerock.b01.service;
import org.zerock.b01.domain.Supplier;
import org.zerock.b01.dto.SupplierDTO;
import org.zerock.b01.dto.UserByDTO;

import java.util.List;

public interface SupplierService {

    SupplierDTO findByUserId(String uId);
    List<Supplier> getSupplier();
    void modifySupplier(SupplierDTO supplierDTO, UserByDTO userByDTO);
}
