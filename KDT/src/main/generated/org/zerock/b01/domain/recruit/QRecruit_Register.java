package org.zerock.b01.domain.recruit;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecruit_Register is a Querydsl query type for Recruit_Register
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecruit_Register extends EntityPathBase<Recruit_Register> {

    private static final long serialVersionUID = -1467958081L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecruit_Register recruit_Register = new QRecruit_Register("recruit_Register");

    public final org.zerock.b01.domain.QBaseEntity _super = new org.zerock.b01.domain.QBaseEntity(this);

    public final org.zerock.b01.domain.member.QBusiness_Member business_member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath reAdminEmail = createString("reAdminEmail");

    public final StringPath reAdminName = createString("reAdminName");

    public final StringPath reAdminPhone = createString("reAdminPhone");

    public final StringPath reApplyMethod = createString("reApplyMethod");

    public final StringPath reCompany = createString("reCompany");

    public final NumberPath<Long> recruitId = createNumber("recruitId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> reDeadline = createDateTime("reDeadline", java.time.LocalDateTime.class);

    public final StringPath reDutyDays = createString("reDutyDays");

    public final StringPath reEducation = createString("reEducation");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final StringPath reGender = createString("reGender");

    public final StringPath reIndustry = createString("reIndustry");

    public final StringPath reJobHistory = createString("reJobHistory");

    public final StringPath reJobTypeAlba = createString("reJobTypeAlba");

    public final StringPath reJobTypeFree = createString("reJobTypeFree");

    public final StringPath reJobTypeFull = createString("reJobTypeFull");

    public final StringPath reJobTypePart = createString("reJobTypePart");

    public final StringPath reJobTypeTrainee = createString("reJobTypeTrainee");

    public final StringPath reMaxAge = createString("reMaxAge");

    public final StringPath reMinAge = createString("reMinAge");

    public final NumberPath<Integer> reNumHiring = createNumber("reNumHiring", Integer.class);

    public final StringPath rePreference = createString("rePreference");

    public final StringPath reSalaryCheck = createString("reSalaryCheck");

    public final StringPath reSalaryDetail = createString("reSalaryDetail");

    public final StringPath reSalaryType = createString("reSalaryType");

    public final StringPath reSalaryValue = createString("reSalaryValue");

    public final StringPath reTimeNegotiable = createString("reTimeNegotiable");

    public final StringPath reTitle = createString("reTitle");

    public final StringPath reWorkDays = createString("reWorkDays");

    public final StringPath reWorkEndTime = createString("reWorkEndTime");

    public final StringPath reWorkStartTime = createString("reWorkStartTime");

    public QRecruit_Register(String variable) {
        this(Recruit_Register.class, forVariable(variable), INITS);
    }

    public QRecruit_Register(Path<? extends Recruit_Register> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecruit_Register(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecruit_Register(PathMetadata metadata, PathInits inits) {
        this(Recruit_Register.class, metadata, inits);
    }

    public QRecruit_Register(Class<? extends Recruit_Register> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.business_member = inits.isInitialized("business_member") ? new org.zerock.b01.domain.member.QBusiness_Member(forProperty("business_member"), inits.get("business_member")) : null;
    }

}

