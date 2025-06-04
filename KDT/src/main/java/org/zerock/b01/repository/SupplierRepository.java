package org.zerock.b01.repository;

import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Supplier;
import org.zerock.b01.domain.UserBy;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query("select s from Supplier s where s.sId=:sId")
    Supplier findSupplierBySId(Long sId);

    @Query("select s from Supplier s where s.sName=:sName")
    Supplier findSupplierBySName(String sName);

    @Query("select s from Supplier s where s.userBy=:userBy")
    Supplier findSupplierByUser(@Param("userBy") UserBy userBy);

    @Query("select s from Supplier s where s.sStatus = '승인'")
    List<Supplier> findSupWithOutDisAgree();

    @Query("select distinct s from Supplier s where s.userBy.uId=:uId")
    Optional<Supplier> findSupplierByUID(String uId);

    @Query("select distinct s from Supplier s where s.userBy.uId=:uId")
    Supplier findSupplierByUidOj(String uId);

    @Query("select distinct s from Supplier s where s.userBy.uId=:uId")
    List<Supplier> findSupplierByUIDList(String uId);
}
