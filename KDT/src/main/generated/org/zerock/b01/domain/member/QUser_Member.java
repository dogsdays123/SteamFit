package org.zerock.b01.domain.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser_Member is a Querydsl query type for User_Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser_Member extends EntityPathBase<User_Member> {

    private static final long serialVersionUID = 1769675205L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser_Member user_Member = new QUser_Member("user_Member");

    public final org.zerock.b01.domain.QAll_Member all_member;

    public final StringPath uAddress = createString("uAddress");

    public final DateTimePath<java.time.LocalDateTime> uBirthday = createDateTime("uBirthday", java.time.LocalDateTime.class);

    public final StringPath uNickname = createString("uNickname");

    public final NumberPath<Long> uResident = createNumber("uResident", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath uSocial = createString("uSocial");

    public QUser_Member(String variable) {
        this(User_Member.class, forVariable(variable), INITS);
    }

    public QUser_Member(Path<? extends User_Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser_Member(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser_Member(PathMetadata metadata, PathInits inits) {
        this(User_Member.class, metadata, inits);
    }

    public QUser_Member(Class<? extends User_Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.all_member = inits.isInitialized("all_member") ? new org.zerock.b01.domain.QAll_Member(forProperty("all_member")) : null;
    }

}

