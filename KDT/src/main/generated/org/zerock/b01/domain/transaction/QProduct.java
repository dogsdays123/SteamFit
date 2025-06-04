package org.zerock.b01.domain.transaction;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = -1585106540L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProduct product = new QProduct("product");

    public final org.zerock.b01.domain.QBaseEntity _super = new org.zerock.b01.domain.QBaseEntity(this);

    public final org.zerock.b01.domain.QAll_Member allMember;

    public final QCategory category;

    public final SetPath<ImageFile, QImageFile> imageSet = this.<ImageFile, QImageFile>createSet("imageSet", ImageFile.class, QImageFile.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath pAddr = createString("pAddr");

    public final StringPath pChatUrl = createString("pChatUrl");

    public final StringPath pContent = createString("pContent");

    public final NumberPath<java.math.BigDecimal> pPrice = createNumber("pPrice", java.math.BigDecimal.class);

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final NumberPath<Integer> pRoles = createNumber("pRoles", Integer.class);

    public final StringPath pStatus = createString("pStatus");

    public final StringPath pTitle = createString("pTitle");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public QProduct(String variable) {
        this(Product.class, forVariable(variable), INITS);
    }

    public QProduct(Path<? extends Product> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProduct(PathMetadata metadata, PathInits inits) {
        this(Product.class, metadata, inits);
    }

    public QProduct(Class<? extends Product> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.allMember = inits.isInitialized("allMember") ? new org.zerock.b01.domain.QAll_Member(forProperty("allMember")) : null;
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
    }

}

