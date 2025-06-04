package org.zerock.b01.domain.transaction;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEquipment is a Querydsl query type for Equipment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEquipment extends EntityPathBase<Equipment> {

    private static final long serialVersionUID = -740772621L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEquipment equipment = new QEquipment("equipment");

    public final StringPath eAs = createString("eAs");

    public final StringPath eBrand = createString("eBrand");

    public final StringPath eName = createString("eName");

    public final StringPath ePurPrice = createString("ePurPrice");

    public final NumberPath<Long> equipmentId = createNumber("equipmentId", Long.class);

    public final StringPath eStatus = createString("eStatus");

    public final DatePath<java.time.LocalDate> eUseEnd = createDate("eUseEnd", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> eUseStart = createDate("eUseStart", java.time.LocalDate.class);

    public final QProduct product;

    public QEquipment(String variable) {
        this(Equipment.class, forVariable(variable), INITS);
    }

    public QEquipment(Path<? extends Equipment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEquipment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEquipment(PathMetadata metadata, PathInits inits) {
        this(Equipment.class, metadata, inits);
    }

    public QEquipment(Class<? extends Equipment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

