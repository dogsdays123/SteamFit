package org.zerock.b01.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Supplier;
import org.zerock.b01.dto.SupplierDTO;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.repository.SupplierRepository;
import org.zerock.b01.service.SupplierService;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper;
    @Override
    public List<Supplier> getSupplier(){
        return supplierRepository.findSupWithOutDisAgree();
    }

    @Override
    public SupplierDTO findByUserId(String uId) {
        Supplier supplier = supplierRepository
                .findSupplierByUID(uId)
                .orElseThrow(() -> new IllegalArgumentException("공급업체 정보를 찾을 수 없습니다."));
        return modelMapper.map(supplier, SupplierDTO.class);
    }

    @Override
    public void modifySupplier(SupplierDTO supplierDTO, UserByDTO userByDTO) {
        Supplier supplier = supplierRepository.findSupplierByUidOj(userByDTO.getUId());
        supplier.modifySupplier(supplierDTO);
    }
}
