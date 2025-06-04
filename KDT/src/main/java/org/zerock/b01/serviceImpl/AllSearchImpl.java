package org.zerock.b01.serviceImpl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.hibernate.result.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.allDTO.*;
import org.zerock.b01.repository.SupplierStockRepository;
import org.zerock.b01.service.AllSearch;
import org.zerock.b01.service.OutputService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
public class AllSearchImpl extends QuerydslRepositorySupport implements AllSearch {

    public AllSearchImpl() {
        super(ProductionPlan.class);  // QuerydslRepositorySupport에 전달할 엔티티 클래스
    }

    // Product와 ProductionPlan을 조인하는 예시
    public List<ProductionPlan> searchPlans() {
        QProductionPlan plan = QProductionPlan.productionPlan;
        QProduct product = QProduct.product;

        JPAQueryFactory queryFactory = new JPAQueryFactory(getEntityManager());  // QuerydslRepositorySupport에서 EntityManager를 가져옵니다

        return queryFactory
                .selectFrom(plan)
                .join(plan.product, product)
                .where(plan.ppNum.gt(0))  // 예시 조건
                .fetch();
    }

    @Override
    public Page<ProductListAllDTO> productSearchWithAll(String[] types, String keyword, String pCode, String pName, Pageable pageable) {

        QProduct product = QProduct.product;
        JPQLQuery<Product> query = from(product);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            booleanBuilder.and(product.pName.contains(keyword));
        }

        if (pName != null && !pName.equals("all")) {
            booleanBuilder.and(product.pName.contains(pName));
        }

        if (pCode != null && !pCode.isEmpty()) {
            booleanBuilder.and(product.pCode.contains(pCode));
        }

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(product.regDate.desc());


        List<Product> resultList = query.fetch();

        // DTO로 변환
        List<ProductListAllDTO> dtoList = resultList.stream()
                .map(prod -> ProductListAllDTO.builder()
                        .pCode(prod.getPCode())
                        .pName(prod.getPName())
                        .pReg(prod.getRegDate() != null ? prod.getRegDate().toLocalDate() : null)
                        .uName(prod.getUserBy().getUName())
                        .build())
                .collect(Collectors.toList());

        // 전체 개수
        // 카운트용 별도 쿼리 생성
        JPQLQuery<Product> countQuery = from(product).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<PlanListAllDTO> planSearchWithAll(String[] types, String keyword, String uId, String ppCode, String pName, String ppState, LocalDate ppStart, LocalDate ppEnd, Pageable pageable) {

        QProductionPlan productPlan = QProductionPlan.productionPlan;
        QProduct product = QProduct.product;
        JPQLQuery<ProductionPlan> query = from(productPlan);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if((types != null && types.length > 0) && keyword != null) {


            for(String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(productPlan.pName.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(productPlan.ppCode.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(productPlan.product.pCode.contains(keyword));
                        break;
                }
            }
            query.where(booleanBuilder);

        }

        log.info("테스트용 " + pName + " / " + uId);
        if (pName != null && !pName.isEmpty()) {
            booleanBuilder.and(productPlan.pName.contains(pName));
        }

        if (uId != null && !uId.isEmpty()) {
            booleanBuilder.and(productPlan.userBy.uId.contains(uId));
        }

        if (ppCode != null && !ppCode.isEmpty()) {
            booleanBuilder.and(productPlan.ppCode.contains(ppCode));
        }

        if (ppStart != null && ppEnd != null) {
            booleanBuilder.and(productPlan.ppStart.goe(ppStart));
            booleanBuilder.and(productPlan.ppEnd.loe(ppEnd));
        } else if (ppStart != null) {
            booleanBuilder.and(productPlan.ppStart.eq(ppStart));
        } else if (ppEnd != null) {
            booleanBuilder.and(productPlan.ppEnd.eq(ppEnd));
        }

        if (pName != null && !pName.isEmpty()) {
            booleanBuilder.and(productPlan.pName.contains(pName));
        }

        if (ppState != null && !ppState.isEmpty() && !ppState.equals("전체")) {
            try {
                CurrentStatus status = CurrentStatus.valueOf(ppState); // 문자열 → Enum
                booleanBuilder.and(productPlan.ppState.eq(status));    // Querydsl 조건
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status value: " + ppState);
                // 유효하지 않은 Enum 값 처리 (필요 시 무시하거나 예외 던질 수 있음)
            }
        }
        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(productPlan.regDate.desc());
        List<ProductionPlan> resultList = query.fetch();

        List<PlanListAllDTO> dtoList = resultList.stream()
                .map(plan -> PlanListAllDTO.builder()
                        .ppCode(plan.getPpCode())
                        .pName(plan.getPName())
                        .ppNum(plan.getPpNum())
                        .ppState(plan.getPpState().toString())
                        .ppStart(plan.getPpStart())
                        .ppEnd(plan.getPpEnd())
                        .uName(plan.getUserBy().getUName())
                        .regDate(plan.getRegDate().toLocalDate())
                        .build())
                .collect(Collectors.toList());

        // 전체 개수
        // 카운트용 별도 쿼리 생성
        JPQLQuery<ProductionPlan> countQuery = from(productPlan).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<UserByAllDTO> userBySearchWithAll(String[] types, String keyword, String uName,
                                                  String userJob, String userRank, LocalDate regDate,
                                                  String status, String uId, Pageable pageable){
        QUserBy userBy = QUserBy.userBy;
        JPQLQuery<UserBy> query = from(userBy);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // keyword로 여러 필드 검색
        if (keyword != null && !keyword.isEmpty()) {
            booleanBuilder.and(userBy.uId.contains(keyword))
                    .or(userBy.uName.contains(keyword));
        }

        if (uName != null && !uName.isEmpty()) {
            booleanBuilder.and(userBy.uName.contains(uName));
        }

        if (userJob != null && !userJob.isEmpty()) {
            if(!userJob.equals("전체")){
                booleanBuilder.and(userBy.userJob.contains(userJob));
            }
        }

        if (regDate != null) {
            booleanBuilder.and(userBy.regDate.goe(regDate.atStartOfDay()));
        }

        BooleanExpression rankCondition = userBy.userRank.isNull()
                .or(userBy.userRank.isEmpty());

        BooleanExpression jobCondition = userBy.userJob.isNotNull()
                .and(userBy.userType.ne("other"));

        BooleanExpression statusCondition =
                userBy.status.eq("대기중")
                        .or(userBy.status.isNull());

        // 전부 and로 묶기
        booleanBuilder.and(rankCondition)
                .and(jobCondition)
                .and(statusCondition);

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(userBy.regDate.desc());
        List<UserBy> resultList = query.fetch();

        List<UserByAllDTO> dtoList = resultList.stream()
                .map(user -> UserByAllDTO.builder()
                        .uName(user.getUName())
                        .userJob(user.getUserJob())
                        .userRank(user.getUserRank())
                        .modDate(user.getModDate())
                        .status(user.getStatus())
                        .uId(user.getUId())
                        .build())
                .collect(Collectors.toList());

        // 전체 개수
        // 카운트용 별도 쿼리 생성
        JPQLQuery<UserBy> countQuery = from(userBy).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<SupplierAllDTO> supplierSearchWithAll(String[] types, String keyword, String sName, String sRegNum, String sBusinessType, LocalDate sRegDate, String sStatus, String pageType, Pageable pageable){
        QSupplier supplier = QSupplier.supplier;
        JPQLQuery<Supplier> query = from(supplier);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // keyword로 여러 필드 검색
        if (keyword != null && !keyword.isEmpty()) {
            booleanBuilder.and(supplier.sName.contains(keyword));
        }

        if (sRegDate != null && !sBusinessType.isEmpty()) {
            booleanBuilder.and(supplier.regDate.goe(sRegDate.atStartOfDay()));
        }

        if (sBusinessType != null && !sBusinessType.isEmpty()) {
            booleanBuilder.and(supplier.sBusinessType.contains(sBusinessType));
        }

        if(pageType.equals("a")){
            booleanBuilder.and(
                    supplier.sStatus.isNull()
                            .or(supplier.sStatus.isEmpty())
                            .or(supplier.sStatus.eq("대기중"))
            );
        } else {
            booleanBuilder.and(
                    supplier.sStatus.eq("반려")
                            .or(supplier.sStatus.eq("승인"))
            );
        }

        if (sRegDate != null) {
            booleanBuilder.and(supplier.regDate.goe(sRegDate.atStartOfDay()));
        }


        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(supplier.regDate.desc());
        List<Supplier> resultList = query.fetch();

        List<SupplierAllDTO> dtoList = resultList.stream()
                .map(sup -> SupplierAllDTO.builder()
                        .uId(sup.getUserBy().getUId())
                        .sName(sup.getSName())
                        .sRegNum(sup.getSRegNum())
                        .sBusinessType(sup.getSBusinessType())
                        .sManager(sup.getSManager())
                        .sPhone(sup.getSPhone())
                        .regDate(sup.getRegDate())
                        .sStatus(sup.getSStatus())
                        .build())
                .collect(Collectors.toList());

        // 전체 개수
        // 카운트용 별도 쿼리 생성
        JPQLQuery<Supplier> countQuery = from(supplier).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<UserByAllDTO> userBySearchWithAllList(String[] types, String keyword, String uName,
                                                  String userJob, String userRank, LocalDate regDate,
                                                  String status, String uId,  Pageable pageable){
        QUserBy userBy = QUserBy.userBy;
        JPQLQuery<UserBy> query = from(userBy);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // keyword로 여러 필드 검색
        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();

            keywordBuilder.or(userBy.uId.contains(keyword));
            keywordBuilder.or(userBy.uName.contains(keyword));

            booleanBuilder.and(keywordBuilder);
        }

        if (uName != null && !uName.isEmpty()) {
            booleanBuilder.and(userBy.uName.contains(uName));
        }

        if (userJob != null && !userJob.isEmpty()) {
            booleanBuilder.and(userBy.userJob.contains(userJob).and(userBy.userJob.ne("협력회사")));
        } else{
            booleanBuilder.and(userBy.userJob.ne("협력회사"));
        }

        if (status != null && !status.isEmpty()) {
            booleanBuilder.and(userBy.status.contains(status));
        } else {
            BooleanBuilder statusBuilder = new BooleanBuilder();
            statusBuilder.or(userBy.status.contains("승인"));
            statusBuilder.or(userBy.status.contains("반려"));

            booleanBuilder.and(statusBuilder); // ✅ 전체 조건에 and로 묶기
        }

        if (regDate != null) {
            booleanBuilder.and(userBy.regDate.goe(regDate.atStartOfDay()));
        }


        BooleanExpression userJobCondition = userBy.userJob.isNotNull();

        // 전부 and로 묶기
        booleanBuilder.and(userJobCondition);

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(userBy.regDate.desc());
        List<UserBy> resultList = query.fetch();

        List<UserByAllDTO> dtoList = resultList.stream()
                .map(user -> UserByAllDTO.builder()
                        .uName(user.getUName())
                        .userJob(user.getUserJob())
                        .userRank(user.getUserRank())
                        .modDate(user.getModDate())
                        .status(user.getStatus())
                        .uId(user.getUId())
                        .build())
                .collect(Collectors.toList());

        // 전체 개수
        // 카운트용 별도 쿼리 생성
        JPQLQuery<UserBy> countQuery = from(userBy).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<MaterialDTO> materialSearchWithAll(String[] types, String keyword, String pName, String componentType, String mName,
                                                   String mCode, String mType, Pageable pageable) {

        QMaterial material = QMaterial.material;
        JPQLQuery<Material> query = from(material);
        BooleanBuilder booleanBuilder = new BooleanBuilder();


        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();

            keywordBuilder.or(material.mName.contains(keyword));
            keywordBuilder.or(material.mComponentType.contains(keyword));

            booleanBuilder.and(keywordBuilder);
        }

        if (pName != null && !pName.isEmpty() && !"전체".equals(pName)) {
            booleanBuilder.and(material.product.pName.contains(pName));
        }

        if (componentType != null && !componentType.isEmpty() && !"전체".equals(componentType)) {
            booleanBuilder.and(material.mComponentType.contains(componentType));
        }

        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            booleanBuilder.and(material.mName.contains(mName));
        }

        if (mCode != null && !mCode.isEmpty() && !"전체".equals(mCode)) {
            booleanBuilder.and(material.mCode.contains(mCode));
        }

        if (mType != null && !mType.isEmpty() && !"전체".equals(mType)) {
            booleanBuilder.and(material.mType.contains(mType));
        }

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(material.regDate.desc());
        List<Material> resultList = query.fetch();

        // DTO로 변환
        List<MaterialDTO> dtoList = resultList.stream()
                .map(prod -> MaterialDTO.builder()
                        .mCode(prod.getMCode())
                        .mName(prod.getMName())
                        .mType(prod.getMType())
                        .mMinNum(prod.getMMinNum())
                        .mHeight(prod.getMHeight())
                        .mWidth(prod.getMWidth())
                        .mDepth(prod.getMDepth())
                        .mWeight(prod.getMWeight())
                        .mUnitPrice(prod.getMUnitPrice())
                        .mComponentType(prod.getMComponentType())
                        .uId(prod.getUserBy().getUId())
                        .pName(prod.getProduct().getPName())
                        .regDate(prod.getRegDate().toLocalDate())
                        .build())
                .collect(Collectors.toList());

        JPQLQuery<Material> countQuery = from(material).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<BomDTO> bomSearchWithAll(String[] types, String keyword, String componentType, String mName, String pName, String uId, Pageable pageable) {

        QBom bom = QBom.bom;
        JPQLQuery<Bom> query = from(bom);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(bom.product.pName.contains(keyword));
            keywordBuilder.or(bom.material.mName.contains(keyword));
            keywordBuilder.or(bom.material.mComponentType.contains(keyword));
            booleanBuilder.and(keywordBuilder);
        }

        if (componentType != null && !componentType.isEmpty() && !"전체".equals(componentType)) {
            booleanBuilder.and(bom.material.mComponentType.contains(componentType));
        }

        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            booleanBuilder.and(bom.material.mName.contains(mName));
        }

        if (pName != null && !pName.isEmpty() && !"전체".equals(pName)) {
            booleanBuilder.and(bom.product.pName.contains(pName));
        }


        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(bom.regDate.desc());
        List<Bom> resultList = query.fetch();
        // DTO로 변환
        List<BomDTO> dtoList = resultList.stream()
                .map(prod -> BomDTO.builder()
                        .bId(prod.getBId())
                        .pName(prod.getProduct().getPName())
                        .mComponentType(prod.getMaterial().getMComponentType())
                        .mName(prod.getMaterial().getMName())
                        .bRequireNum(prod.getBRequireNum())
                        .regDate(prod.getRegDate().toLocalDate())
                        .uId(prod.getUserBy().getUId())
                        .build())
                .collect(Collectors.toList());

        JPQLQuery<Bom> countQuery = from(bom).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<InventoryStockDTO> inventoryStockSearchWithAll(String[] types, String keyword,
                                                               String pName, String componentType, String mName, String isLocation, LocalDate isRegDate, String uId, Pageable pageable) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QInventoryStock inventoryStock = QInventoryStock.inventoryStock;
        QMaterial material = QMaterial.material;
        QDeliveryProcurementPlan dpp = QDeliveryProcurementPlan.deliveryProcurementPlan;
        QProductionPlan pp = QProductionPlan.productionPlan;
//        JPQLQuery<InventoryStock> query = from(inventoryStock);

        JPQLQuery<Tuple> query = from(inventoryStock)
                .distinct()
                .join(inventoryStock.material, material)
                .leftJoin(dpp).on(inventoryStock.material.mCode.eq(dpp.material.mCode))
                .leftJoin(dpp.productionPlan, pp)
                .select(inventoryStock, dpp)
                .where(booleanBuilder)
                .groupBy(inventoryStock.isId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(inventoryStock.isNum.desc());

        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(inventoryStock.material.mName.contains(keyword));
            keywordBuilder.or(inventoryStock.material.mComponentType.contains(keyword));
            keywordBuilder.or(inventoryStock.material.product.pName.contains(keyword));
            booleanBuilder.and(keywordBuilder);
        }

        if (pName != null && !pName.isEmpty() && !"전체".equals(pName)) {
            log.info("Received pName: " + pName);
            booleanBuilder.and(inventoryStock.material.product.pName.contains(pName));
        }

        if (componentType != null && !componentType.isEmpty() && !"전체".equals(componentType)) {
            log.info("Received pName: " + componentType);
            booleanBuilder.and(inventoryStock.material.mComponentType.contains(componentType));
        }

        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            log.info("Received pName: " + mName);
            booleanBuilder.and(inventoryStock.material.mName.contains(mName));
        }

        if (isLocation != null && !isLocation.isEmpty() && !"전체".equals(isLocation)) {
            log.info("Received pName: " + isLocation);
//            booleanBuilder.and(inventoryStock.isLocation.contains(isLocation));
        }

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(inventoryStock.regDate.desc());

        List<Tuple> resultList = query.fetch();

        List<InventoryStockDTO> dtoList = resultList.stream()
                .map(tuple -> {
                    InventoryStock stock = tuple.get(inventoryStock);
                    DeliveryProcurementPlan plan = tuple.get(dpp);

                    return InventoryStockDTO.builder()
                            .isId(stock.getIsId())
                            .mCode(stock.getMaterial().getMCode())
                            .isAvailable(Integer.parseInt(stock.getIsAvailable()))
                            .isNum(Integer.parseInt(stock.getIsNum()))
                            .isLocation(stock.getIsLocation())

                            .pCode(stock.getMaterial().getProduct().getPCode())
                            .regDate(stock.getRegDate().toLocalDate())
                            .ppCode(plan != null ? plan.getProductionPlan().getPpCode() : null) // 필요시 추가
                            .build();
                })
                .collect(Collectors.toList());

        JPQLQuery<InventoryStock> countQuery = from(inventoryStock).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public  Page<DeliveryRequestDTO> deliveryRequestSearchWithAll(String[] types, String keyword,
                                                                  String mName, String sName, String drState, Pageable pageable){

        QDeliveryRequest deliveryRequest = QDeliveryRequest.deliveryRequest;
        JPQLQuery<DeliveryRequest> query = from(deliveryRequest);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(deliveryRequest.material.mName.contains(keyword));
            booleanBuilder.and(keywordBuilder);
        }


        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            log.info("Received pName: " + mName);
            booleanBuilder.and(deliveryRequest.material.mName.contains(mName));
        }

        if (drState != null && !drState.isEmpty() && !"전체".equals(drState)) {
            log.info("Received pName: " + drState);
            try {
                CurrentStatus status = CurrentStatus.valueOf(drState); // 문자열을 enum 값으로 변환
                booleanBuilder.and(deliveryRequest.drState.eq(status)); // enum 값을 비교
            } catch (IllegalArgumentException e) {
                log.error("Invalid drState value: " + drState);
            }
        }

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(deliveryRequest.regDate.desc());

        List<DeliveryRequest> resultList = query.fetch();

        List<DeliveryRequestDTO> dtoList = resultList.stream()
                .map(prod -> DeliveryRequestDTO.builder()
                        .drCode(prod.getDrCode())
                        .drNum(Integer.parseInt(prod.getDrNum()))
                        .drDate(prod.getDrDate())
                        .drState(prod.getDrState())
                        .oCode(prod.getOrderBy().getOCode())
                        .oNum(prod.getOrderBy().getONum())
                        .oTotalPrice(prod.getOrderBy().getOTotalPrice())
                        .sId(prod.getSupplier().getSId())
                        .sName(prod.getSupplier().getSName())
                        .mCode(prod.getMaterial().getMCode())
                        .mName(prod.getMaterial().getMName())
                        .build())
                .collect(Collectors.toList());

        JPQLQuery<DeliveryRequest> countQuery = from(deliveryRequest).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public  Page<InputDTO> inputSearchWithAll(String[] types, String keyword, String mName, String ipState, Pageable pageable){

        QInPut inPut = QInPut.inPut;
        JPQLQuery<InPut> query = from(inPut);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(inPut.deliveryRequest.material.mName.contains(keyword));
            booleanBuilder.and(keywordBuilder);
        }


        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            log.info("Received pName: " + mName);
            booleanBuilder.and(inPut.deliveryRequest.material.mName.contains(mName));
        }

        if (ipState != null && !ipState.isEmpty() && !"전체".equals(ipState)) {
            log.info("Received pName: " + ipState);
            try {
                CurrentStatus status = CurrentStatus.valueOf(ipState); // 문자열을 enum 값으로 변환
                booleanBuilder.and(inPut.ipState.eq(status)); // enum 값을 비교
            } catch (IllegalArgumentException e) {
                log.error("Invalid drState value: " + ipState);
            }
        }

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(inPut.regDate.desc());

        List<InPut> resultList = query.fetch();

        List<InputDTO> dtoList = resultList.stream()
                .map(prod -> InputDTO.builder()
                        .ipNum(prod.getIpNum())
                        .ipCode(prod.getIpCode())
                        .ipFalseNum(prod.getIpFalseNum())
                        .ipTrueNum(prod.getIpTrueNum())
                        .ipState(prod.getIpState())
                        .drNum(prod.getDeliveryRequest().getDrNum())
                        .drCode(prod.getDeliveryRequest().getDrCode())
                        .drState(prod.getDeliveryRequest().getDrState())
                        .drDate(prod.getDeliveryRequest().getDrDate())
                        .oCode(prod.getOrderBy().getOCode())
                        .oNum(prod.getOrderBy().getONum())
                        .regDate(prod.getRegDate().toLocalDate())
                        .mCode(prod.getDeliveryRequest().getMaterial().getMCode())
                        .mName(prod.getDeliveryRequest().getMaterial().getMName())
                        .build())
                .collect(Collectors.toList());

        JPQLQuery<InPut> countQuery = from(inPut).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    public Page<DppListAllDTO> dppSearchWithAll(String[] types, String keyword, String dppCode, String ppCode, String mName, String mCode, String sName, Long dppNum, String pName, LocalDate dppRegDate, String dppState, String uId, Pageable pageable){

        QDeliveryProcurementPlan dpp = QDeliveryProcurementPlan.deliveryProcurementPlan;
        JPQLQuery<DeliveryProcurementPlan> query = from(dpp);
        BooleanBuilder booleanBuilder = new BooleanBuilder();


        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(dpp.dppCode.contains(keyword));
            keywordBuilder.or(dpp.productionPlan.ppCode.contains(keyword));
            keywordBuilder.or(dpp.material.mName.contains(keyword));
            keywordBuilder.or(dpp.material.mCode.contains(keyword));
            booleanBuilder.and(keywordBuilder);
        }

        if (dppCode != null && !dppCode.isEmpty() && !"전체".equals(dppCode)) {
            booleanBuilder.and(dpp.dppCode.contains(dppCode));
        }

        if (ppCode != null && !ppCode.isEmpty() && !"전체".equals(ppCode)) {
            booleanBuilder.and(dpp.productionPlan.ppCode.contains(ppCode));
        }

        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            booleanBuilder.and(dpp.material.mName.contains(mName));
        }

        if (mCode != null && !mCode.isEmpty() && !"전체".equals(mCode)) {
            booleanBuilder.and(dpp.material.mCode.contains(mCode));
        }

        if (dppState != null && !dppState.isEmpty() && !"전체".equals(dppState)) {
            CurrentStatus status = CurrentStatus.valueOf(dppState);
            booleanBuilder.and(dpp.dppState.eq(status));
        }


        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(dpp.regDate.desc());
        List<DeliveryProcurementPlan> resultList = query.fetch();
        // DTO로 변환
        List<DppListAllDTO> dtoList = resultList.stream()
                .map(prod -> DppListAllDTO.builder()
                        .dppCode(prod.getDppCode())
                        .dppRequireNum(prod.getDppRequireNum())
                        .pName(prod.getProductionPlan().getPName())
                        .dppNum(prod.getDppNum())
                        .dppDate(prod.getDppDate())
                        .dppRegDate(prod.getRegDate())
                        .dppState(prod.getDppState().toString())
                        .ppCode(prod.getProductionPlan().getPpCode())
                        .mName(prod.getMaterial().getMName())
                        .mPerPrice(prod.getMaterial().getMUnitPrice())
                        .sName(
                                (prod.getMaterial() != null && prod.getSupplier() != null && prod.getSupplier().getSName() != null)
                                        ? prod.getSupplier().getSName()
                                        : "배치중"
                        )
                        .mCode(prod.getMaterial().getMCode())
                        .uId(prod.getUserBy().getUId())
                        .build())
                .collect(Collectors.toList());

        JPQLQuery<DeliveryProcurementPlan> countQuery = from(dpp).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }


    @Override
    public  Page<OutPutDTO> outputSearchWithAll(String[] types, String keyword, String pName, String mName, String opState, Pageable pageable) {

        QOutPut outPut = QOutPut.outPut;
        JPQLQuery<OutPut> query = from(outPut);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(outPut.material.mName.contains(keyword));
            booleanBuilder.and(keywordBuilder);
        }


        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            log.info("Received pName: " + mName);
            booleanBuilder.and(outPut.material.mName.contains(mName));
        }

        if (opState != null && !opState.isEmpty() && !"전체".equals(opState)) {
            log.info("Received pName: " + opState);
            try {
                CurrentStatus status = CurrentStatus.valueOf(opState); // 문자열을 enum 값으로 변환
                booleanBuilder.and(outPut.opState.eq(status)); // enum 값을 비교
            } catch (IllegalArgumentException e) {
                log.error("Invalid drState value: " + opState);
            }
        }

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(outPut.regDate.desc());

        List<OutPut> resultList = query.fetch();

        List<OutPutDTO> dtoList = resultList.stream()
                .map(prod -> OutPutDTO.builder()
                        .opState(prod.getOpState())
                        .opANum(prod.getOpANum())
                        .opCode(prod.getOpCode())
                        .mCode(prod.getMaterial().getMCode())
                        .mName(prod.getMaterial().getMName())
                        .ppCode(prod.getProductionPlan().getPpCode())
                        .regDate(prod.getRegDate().toLocalDate())
                        .build())
                .collect(Collectors.toList());

        JPQLQuery<OutPut> countQuery = from(outPut).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    public Page<OrderByListAllDTO> orderBySearchWithAll
            (String[] types, String keyword, String label, String oCode, LocalDate oRegDate, LocalDate oExpectDate, String sName, String mName, String oState, String uId, Pageable pageable){

        QOrderBy orderBy = QOrderBy.orderBy;
        JPQLQuery<OrderBy> query = from(orderBy);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QSupplierStock supplierStock = QSupplierStock.supplierStock;

        BooleanExpression leadTimeGte10 = orderBy.deliveryProcurementPlan.material.mCode.in(
                JPAExpressions
                        .select(supplierStock.material.mCode)
                        .from(supplierStock)
                        .where(supplierStock.leadTime.goe(String.valueOf(10)))
        );
        booleanBuilder.and(leadTimeGte10);

        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(orderBy.oCode.contains(keyword));
            keywordBuilder.or(orderBy.deliveryProcurementPlan.supplier.sName.contains(keyword));
            keywordBuilder.or(orderBy.deliveryProcurementPlan.material.mName.contains(keyword));
            booleanBuilder.and(keywordBuilder);
        }

        if(oCode != null && !oCode.isEmpty() && !"전체".equals(sName)){
            booleanBuilder.and(orderBy.oCode.contains(oCode));
        }

        if (sName != null && !sName.isEmpty() && !"전체".equals(sName)) {
            booleanBuilder.and(orderBy.deliveryProcurementPlan.supplier.sName.contains(sName));
        }


        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            booleanBuilder.and(orderBy.deliveryProcurementPlan.material.mName.contains(mName));
        }

        if (oState != null && !oState.isEmpty() && !"전체".equals(oState)) {
            CurrentStatus status = CurrentStatus.valueOf(oState);
            booleanBuilder.and(orderBy.oState.eq(status));
        }

        if(label.equals("ps"))
        booleanBuilder.and(
                orderBy.oState.eq(CurrentStatus.HOLD_PROGRESS)
                        .or(orderBy.oState.eq(CurrentStatus.UNDER_INSPECTION))
                        .or(orderBy.oState.eq(CurrentStatus.SUCCESS_INSPECTION))
        );

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(orderBy.regDate.desc());
        List<OrderBy> resultList = query.fetch();

        List<OrderByListAllDTO> dtoList = resultList.stream()
                .map(ob -> OrderByListAllDTO.builder()
                        .oCode(ob.getOCode())
                        .dppDate(ob.getDeliveryProcurementPlan().getDppDate())
                        .oNum(ob.getONum())
                        .oTotalPrice(ob.getOTotalPrice())
                        .oRegDate(ob.getRegDate())
                        .sName(ob.getDeliveryProcurementPlan().getSupplier().getSName())
                        .mName(ob.getDeliveryProcurementPlan().getMaterial().getMName())
                        .mCode(ob.getDeliveryProcurementPlan().getMaterial().getMCode())
                        .mDepth(ob.getDeliveryProcurementPlan().getMaterial().getMDepth())
                        .mHeight(ob.getDeliveryProcurementPlan().getMaterial().getMHeight())
                        .mWeight(ob.getDeliveryProcurementPlan().getMaterial().getMWeight())
                        .mWidth(ob.getDeliveryProcurementPlan().getMaterial().getMWidth())
                        .mUnitPrice(ob.getDeliveryProcurementPlan().getMaterial().getMUnitPrice())
                        .oExpectDate(ob.getOExpectDate())
                        .oState(ob.getOState().toString())
                        .uId(ob.getUserBy().getUId())
                        .sId(ob.getDeliveryProcurementPlan().getSupplier().getSId())
                        .build())
                .collect(Collectors.toList());

        JPQLQuery<OrderBy> countQuery = from(orderBy).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }


    @Override
    public  Page<SupplierStockDTO> supplierStockSearchWithAll(String[] types, String keyword, String pName, String mName, Long sId, Pageable pageable) {

        QSupplierStock supplierStock = QSupplierStock.supplierStock;
        JPQLQuery<SupplierStock> query = from(supplierStock);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (sId != null) {
            booleanBuilder.and(supplierStock.supplier.sId.eq(sId));
        }

        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(supplierStock.material.mName.contains(keyword));
            booleanBuilder.and(keywordBuilder);
        }


        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            log.info("Received pName: " + mName);
            booleanBuilder.and(supplierStock.material.mName.contains(mName));
        }

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(supplierStock.regDate.desc());

        List<SupplierStock> resultList = query.fetch();

        List<SupplierStockDTO> dtoList = resultList.stream()
                .map(prod -> SupplierStockDTO.builder()
                        .ssId(prod.getSsId())
                        .ssNum(prod.getSsNum())
                        .ssMinOrderQty(prod.getSsMinOrderQty())
                        .leadTime(prod.getLeadTime())
                        .unitPrice(prod.getUnitPrice())
                        .mCode(prod.getMaterial().getMCode())
                        .sId(prod.getSupplier().getSId())
                        .regDate(prod.getRegDate().toLocalDate())
                        .build())
                .collect(Collectors.toList());

        JPQLQuery<SupplierStock> countQuery = from(supplierStock).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<ProgressInspectionDTO> progressInspectionSearchWithAll(String[] types, String keyword, String mName, LocalDate psDate, String psState, Long sId, Pageable pageable) {

        QProgressInspection progressInspection = QProgressInspection.progressInspection;
        JPQLQuery<ProgressInspection> query = from(progressInspection);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (sId != null) {
            booleanBuilder.and(progressInspection.supplierStock.supplier.sId.eq(sId));
        }

        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(progressInspection.supplierStock.material.mName.contains(keyword));
            booleanBuilder.and(keywordBuilder);
        }


        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            log.info("Received pName: " + mName);
            booleanBuilder.and(progressInspection.supplierStock.material.mName.contains(mName));
        }

        if (psDate != null) {
            log.info("Received psDate: " + psDate);
            booleanBuilder.and(progressInspection.psDate.eq(psDate));
        }

        if(psState != null && !psState.isEmpty() && !"전체".equals(psState)){
            CurrentStatus status = CurrentStatus.valueOf(psState);
            booleanBuilder.and(progressInspection.psState.eq(status));
        }



        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(progressInspection.regDate.desc());

        List<ProgressInspection> resultList = query.fetch();

        List<ProgressInspectionDTO> dtoList = resultList.stream()
                .map(prod -> ProgressInspectionDTO.builder()
                        .psId(prod.getPsId())
                        .psNum(prod.getPsNum())
                        .oCode(prod.getOrderBy().getOCode())
                        .psDate(prod.getPsDate())
                        .psRemarks(prod.getPsRemarks())
                        .ssId(prod.getSupplierStock().getSsId())
                        .regDate(prod.getRegDate().toLocalDate())
                        .mCode(prod.getSupplierStock().getMaterial().getMCode())
                        .mName(prod.getSupplierStock().getMaterial().getMName())
                        .oState(prod.getOrderBy().getOState())
                        .build())
                .collect(Collectors.toList());

        JPQLQuery<ProgressInspection> countQuery = from(progressInspection).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<OrderByListAllDTO> orderBySearchSidWithAll(String[] types, String keyword, LocalDate oRegDate, LocalDate oExpectDate,
                                                           String sName, String mName, String oState, Long sId, Pageable pageable) {


        QOrderBy orderBy = QOrderBy.orderBy;
        JPQLQuery<OrderBy> query = from(orderBy);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QSupplierStock supplierStock = QSupplierStock.supplierStock;

        BooleanExpression leadTimeGte10 = orderBy.deliveryProcurementPlan.material.mCode.in(
                JPAExpressions
                        .select(supplierStock.material.mCode)
                        .from(supplierStock)
                        .where(supplierStock.leadTime.goe(String.valueOf(10)))
        );
        booleanBuilder.and(leadTimeGte10);

        if (sId != null) {
            booleanBuilder.and(orderBy.deliveryProcurementPlan.supplier.sId.eq(sId));
        }

        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(orderBy.oCode.contains(keyword));
            keywordBuilder.or(orderBy.deliveryProcurementPlan.supplier.sName.contains(keyword));
            keywordBuilder.or(orderBy.deliveryProcurementPlan.material.mName.contains(keyword));
            keywordBuilder.or(orderBy.deliveryProcurementPlan.userBy.uId.contains(keyword));
            booleanBuilder.and(keywordBuilder);
        }

        if (sName != null && !sName.isEmpty() && !"전체".equals(sName)) {
            booleanBuilder.and(orderBy.deliveryProcurementPlan.supplier.sName.contains(sName));
        }


        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            booleanBuilder.and(orderBy.deliveryProcurementPlan.material.mName.contains(mName));
        }

        if (oState != null && !oState.isEmpty() && !"전체".equals(oState)) {
            switch (oState){
                case "대기": booleanBuilder.and(orderBy.oState.in(CurrentStatus.ON_HOLD));
                    break;
                case "진행": booleanBuilder.and(orderBy.oState.in(CurrentStatus.IN_PROGRESS));
                    break;
                case "종료": booleanBuilder.and(orderBy.oState.in(CurrentStatus.FINISHED));
                    break;
                case "거절": booleanBuilder.and(orderBy.oState.in(CurrentStatus.REJECT));
                    break;
            }
        }

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(orderBy.regDate.desc());
        List<OrderBy> resultList = query.fetch();

        List<OrderByListAllDTO> dtoList = resultList.stream()
                .map(ob -> OrderByListAllDTO.builder()
                        .oCode(ob.getOCode())
                        .dppDate(ob.getDeliveryProcurementPlan().getDppDate())
                        .oNum(ob.getONum())
                        .oTotalPrice(ob.getOTotalPrice())
                        .oRegDate(ob.getRegDate())
                        .sName(ob.getDeliveryProcurementPlan().getSupplier().getSName())
                        .mName(ob.getDeliveryProcurementPlan().getMaterial().getMName())
                        .mCode(ob.getDeliveryProcurementPlan().getMaterial().getMCode())
                        .mUnitPrice(ob.getDeliveryProcurementPlan().getMaterial().getMUnitPrice())
                        .mWidth(ob.getDeliveryProcurementPlan().getMaterial().getMWidth())
                        .mHeight(ob.getDeliveryProcurementPlan().getMaterial().getMHeight())
                        .mDepth(ob.getDeliveryProcurementPlan().getMaterial().getMDepth())
                        .mWeight(ob.getDeliveryProcurementPlan().getMaterial().getMWeight())
                        .oExpectDate(ob.getOExpectDate())
                        .oState(ob.getOState().toString())
                        .uId(ob.getUserBy().getUId())
                        .sId(ob.getDeliveryProcurementPlan().getSupplier().getSId())
                        .build())
                .collect(Collectors.toList());

        JPQLQuery<OrderBy> countQuery = from(orderBy).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public  Page<DeliveryRequestDTO> supplierDeliveryRequestSearchWithAll(String[] types, String keyword,
                                                                  String mName, String sName, Long sId, String drState, Pageable pageable){

        QDeliveryRequest deliveryRequest = QDeliveryRequest.deliveryRequest;
        JPQLQuery<DeliveryRequest> query = from(deliveryRequest);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (sId != null) {
            booleanBuilder.and(deliveryRequest.supplier.sId.eq(sId));
        }

        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(deliveryRequest.material.mName.contains(keyword));
            booleanBuilder.and(keywordBuilder);
        }


        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            log.info("Received pName: " + mName);
            booleanBuilder.and(deliveryRequest.material.mName.contains(mName));
        }

        if (drState != null && !drState.isEmpty() && !"전체".equals(drState)) {
            try {
                CurrentStatus status = CurrentStatus.valueOf(drState);
                booleanBuilder.and(deliveryRequest.drState.eq(status));
            } catch (IllegalArgumentException e) {
                log.error("Invalid drState value: " + drState);
                // 예외 시 기본값 적용 가능
                booleanBuilder.and(deliveryRequest.drState.eq(CurrentStatus.ON_HOLD));
            }
        } else {
            booleanBuilder.and(deliveryRequest.drState.eq(CurrentStatus.ON_HOLD));
        }

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(deliveryRequest.regDate.desc());

        List<DeliveryRequest> resultList = query.fetch();

        List<DeliveryRequestDTO> dtoList = resultList.stream()
                .map(prod -> DeliveryRequestDTO.builder()
                        .drCode(prod.getDrCode())
                        .drNum(Integer.parseInt(prod.getDrNum()))
                        .drDate(prod.getDrDate())
                        .drState(prod.getDrState())
                        .oCode(prod.getOrderBy().getOCode())
                        .oNum(prod.getOrderBy().getONum())
                        .oTotalPrice(prod.getOrderBy().getOTotalPrice())
                        .sId(prod.getSupplier().getSId())
                        .sName(prod.getSupplier().getSName())
                        .mCode(prod.getMaterial().getMCode())
                        .mName(prod.getMaterial().getMName())
                        .build())
                .collect(Collectors.toList());

        JPQLQuery<DeliveryRequest> countQuery = from(deliveryRequest).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<ProgressInspectionDTO> supplierProgressInspectionSearchWithAll(String[] types, String keyword, String mName, LocalDate psDate, String psState, Long sId, Pageable pageable) {

        QProgressInspection progressInspection = QProgressInspection.progressInspection;
        JPQLQuery<ProgressInspection> query = from(progressInspection);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (sId != null) {
            booleanBuilder.and(progressInspection.supplierStock.supplier.sId.eq(sId));
        }

        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(progressInspection.supplierStock.material.mName.contains(keyword));
            booleanBuilder.and(keywordBuilder);
        }


        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            log.info("Received pName: " + mName);
            booleanBuilder.and(progressInspection.supplierStock.material.mName.contains(mName));
        }

        if (psDate != null) {
            log.info("Received psDate: " + psDate);
            booleanBuilder.and(progressInspection.psDate.eq(psDate));
        }

        booleanBuilder.and(progressInspection.orderBy.oState.eq(CurrentStatus.UNDER_INSPECTION));

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(progressInspection.regDate.desc());

        List<ProgressInspection> resultList = query.fetch();

        List<ProgressInspectionDTO> dtoList = resultList.stream()
                .map(prod -> ProgressInspectionDTO.builder()
                        .psId(prod.getPsId())
                        .psNum(prod.getPsNum())
                        .oCode(prod.getOrderBy().getOCode())
                        .psDate(prod.getPsDate())
                        .psRemarks(prod.getPsRemarks())
                        .ssId(prod.getSupplierStock().getSsId())
                        .regDate(prod.getRegDate().toLocalDate())
                        .mCode(prod.getSupplierStock().getMaterial().getMCode())
                        .mName(prod.getSupplierStock().getMaterial().getMName())
                        .oState(prod.getOrderBy().getOState())
                        .build())
                .collect(Collectors.toList());

        JPQLQuery<ProgressInspection> countQuery = from(progressInspection).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<ReturnByDTO> supplierReturnByWithAll(String[] types, String keyword, String mName, Long sId, Pageable pageable) {

        QReturnBy returnBy = QReturnBy.returnBy;
        QInPut inPut = QInPut.inPut;
        QDeliveryRequest deliveryRequest = QDeliveryRequest.deliveryRequest;
        QSupplier supplier = QSupplier.supplier;
        QMaterial material = QMaterial.material;

        JPQLQuery<ReturnBy> query = from(returnBy);

        query.leftJoin(returnBy.inPut, inPut);
        query.leftJoin(inPut.deliveryRequest, deliveryRequest);
        query.leftJoin(deliveryRequest.supplier, supplier);
        query.leftJoin(deliveryRequest.material, material);

        BooleanBuilder booleanBuilder = new BooleanBuilder();


        if (sId != null) {
            booleanBuilder.and(supplier.sId.eq(sId));
        }

        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(deliveryRequest.material.mName.contains(keyword));
            booleanBuilder.and(keywordBuilder);
        }


        if (mName != null && !mName.isEmpty() && !"전체".equals(mName)) {
            log.info("Received pName: " + mName);
            booleanBuilder.and(deliveryRequest.material.mName.contains(mName)); // ← 이렇게 바꾸세요
        }

//        booleanBuilder.and(progressInspection.orderBy.oState.eq(CurrentStatus.UNDER_INSPECTION));

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(returnBy.regDate.desc());

        List<ReturnBy> resultList = query.fetch();

        List<ReturnByDTO> dtoList = resultList.stream()
                .map(prod -> ReturnByDTO.builder()
                        .rId(prod.getRId())
                        .rNum(prod.getRNum())
                        .rState(prod.getRState())
                        .ipCode(prod.getInPut().getIpCode())
                        .mCode(prod.getInPut().getDeliveryRequest().getMaterial().getMCode())
                        .mName(prod.getInPut().getDeliveryRequest().getMaterial().getMName())
                        .regDate(prod.getRegDate().toLocalDate())
                        .build())
                .collect(Collectors.toList());

        JPQLQuery<ReturnBy> countQuery = from(returnBy);
        countQuery.leftJoin(returnBy.inPut, inPut);
        countQuery.leftJoin(inPut.deliveryRequest, deliveryRequest);
        countQuery.leftJoin(deliveryRequest.supplier, supplier);
        countQuery.where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }
}
