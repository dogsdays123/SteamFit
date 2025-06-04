package org.zerock.b01.domain.recruit;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecruit_Apply is a Querydsl query type for Recruit_Apply
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecruit_Apply extends EntityPathBase<Recruit_Apply> {

    private static final long serialVersionUID = -1877800238L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecruit_Apply recruit_Apply = new QRecruit_Apply("recruit_Apply");

    public final NumberPath<Long> re_apply_id = createNumber("re_apply_id", Long.class);

    public final QRecruit_Register recruit_register;

    public final DatePath<java.time.LocalDate> regdate = createDate("regdate", java.time.LocalDate.class);

    public final org.zerock.b01.domain.trainer.QTrainer trainer;

    public QRecruit_Apply(String variable) {
        this(Recruit_Apply.class, forVariable(variable), INITS);
    }

    public QRecruit_Apply(Path<? extends Recruit_Apply> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecruit_Apply(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecruit_Apply(PathMetadata metadata, PathInits inits) {
        this(Recruit_Apply.class, metadata, inits);
    }

    public QRecruit_Apply(Class<? extends Recruit_Apply> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.recruit_register = inits.isInitialized("recruit_register") ? new QRecruit_Register(forProperty("recruit_register"), inits.get("recruit_register")) : null;
        this.trainer = inits.isInitialized("trainer") ? new org.zerock.b01.domain.trainer.QTrainer(forProperty("trainer"), inits.get("trainer")) : null;
    }

}

