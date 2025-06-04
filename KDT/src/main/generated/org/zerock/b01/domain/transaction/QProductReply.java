package org.zerock.b01.domain.transaction;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductReply is a Querydsl query type for ProductReply
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductReply extends EntityPathBase<ProductReply> {

    private static final long serialVersionUID = 676449718L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductReply productReply = new QProductReply("productReply");

    public final org.zerock.b01.domain.QBaseEntity _super = new org.zerock.b01.domain.QBaseEntity(this);

    public final org.zerock.b01.domain.QAll_Member allMember;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath pReplyText = createString("pReplyText");

    public final QProduct product;

    public final NumberPath<Long> productReplyId = createNumber("productReplyId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public QProductReply(String variable) {
        this(ProductReply.class, forVariable(variable), INITS);
    }

    public QProductReply(Path<? extends ProductReply> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductReply(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductReply(PathMetadata metadata, PathInits inits) {
        this(ProductReply.class, metadata, inits);
    }

    public QProductReply(Class<? extends ProductReply> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.allMember = inits.isInitialized("allMember") ? new org.zerock.b01.domain.QAll_Member(forProperty("allMember")) : null;
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

