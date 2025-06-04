package org.zerock.b01.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAll_Member is a Querydsl query type for All_Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAll_Member extends EntityPathBase<All_Member> {

    private static final long serialVersionUID = -1020906557L;

    public static final QAll_Member all_Member = new QAll_Member("all_Member");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath allId = createString("allId");

    public final NumberPath<Long> aPhone = createNumber("aPhone", Long.class);

    public final StringPath aPsw = createString("aPsw");

    public final BooleanPath aSocial = createBoolean("aSocial");

    public final BooleanPath del = createBoolean("del");

    public final StringPath email = createString("email");

    public final StringPath memberType = createString("memberType");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final SetPath<org.zerock.b01.domain.member.MemberRole, EnumPath<org.zerock.b01.domain.member.MemberRole>> roleSet = this.<org.zerock.b01.domain.member.MemberRole, EnumPath<org.zerock.b01.domain.member.MemberRole>>createSet("roleSet", org.zerock.b01.domain.member.MemberRole.class, EnumPath.class, PathInits.DIRECT2);

    public QAll_Member(String variable) {
        super(All_Member.class, forVariable(variable));
    }

    public QAll_Member(Path<? extends All_Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAll_Member(PathMetadata metadata) {
        super(All_Member.class, metadata);
    }

}

