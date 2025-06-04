package org.zerock.b01.domain.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBusiness_Member is a Querydsl query type for Business_Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBusiness_Member extends EntityPathBase<Business_Member> {

    private static final long serialVersionUID = -526388048L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBusiness_Member business_Member = new QBusiness_Member("business_Member");

    public final org.zerock.b01.domain.QAll_Member all_member;

    public final StringPath bAddress = createString("bAddress");

    public final NumberPath<Long> bAssets = createNumber("bAssets", Long.class);

    public final NumberPath<Long> bAverage = createNumber("bAverage", Long.class);

    public final NumberPath<Long> bEmployees = createNumber("bEmployees", Long.class);

    public final DateTimePath<java.time.LocalDateTime> bEstaDate = createDateTime("bEstaDate", java.time.LocalDateTime.class);

    public final StringPath bExponent = createString("bExponent");

    public final StringPath bHomepage = createString("bHomepage");

    public final StringPath bName = createString("bName");

    public final NumberPath<Long> bPhone = createNumber("bPhone", Long.class);

    public final NumberPath<Long> bRegnum = createNumber("bRegnum", Long.class);

    public final StringPath bSize = createString("bSize");

    public final NumberPath<Long> businessId = createNumber("businessId", Long.class);

    public QBusiness_Member(String variable) {
        this(Business_Member.class, forVariable(variable), INITS);
    }

    public QBusiness_Member(Path<? extends Business_Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBusiness_Member(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBusiness_Member(PathMetadata metadata, PathInits inits) {
        this(Business_Member.class, metadata, inits);
    }

    public QBusiness_Member(Class<? extends Business_Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.all_member = inits.isInitialized("all_member") ? new org.zerock.b01.domain.QAll_Member(forProperty("all_member")) : null;
    }

}

