package org.zerock.b01.domain.trainer;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTrainer is a Querydsl query type for Trainer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTrainer extends EntityPathBase<Trainer> {

    private static final long serialVersionUID = 1460483761L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTrainer trainer = new QTrainer("trainer");

    public final org.zerock.b01.domain.QBaseEntity _super = new org.zerock.b01.domain.QBaseEntity(this);

    public final StringPath academy = createString("academy");

    public final StringPath academyFinal = createString("academyFinal");

    public final StringPath career = createString("career");

    public final NumberPath<Integer> careerPeriod = createNumber("careerPeriod", Integer.class);

    public final StringPath content = createString("content");

    public final SetPath<Trainer_Thumbnails, QTrainer_Thumbnails> imageSet = this.<Trainer_Thumbnails, QTrainer_Thumbnails>createSet("imageSet", Trainer_Thumbnails.class, QTrainer_Thumbnails.class, PathInits.DIRECT2);

    public final StringPath license = createString("license");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath prize = createString("prize");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final StringPath title = createString("title");

    public final NumberPath<Long> trainerId = createNumber("trainerId", Long.class);

    public final org.zerock.b01.domain.member.QUser_Member userMember;

    public final NumberPath<Integer> wantDay = createNumber("wantDay", Integer.class);

    public final StringPath wantDayType = createString("wantDayType");

    public final StringPath wantJob = createString("wantJob");

    public final StringPath wantLegion = createString("wantLegion");

    public final NumberPath<Integer> wantSal = createNumber("wantSal", Integer.class);

    public final StringPath wantSalType = createString("wantSalType");

    public final NumberPath<Double> wantTime = createNumber("wantTime", Double.class);

    public final StringPath wantType = createString("wantType");

    public QTrainer(String variable) {
        this(Trainer.class, forVariable(variable), INITS);
    }

    public QTrainer(Path<? extends Trainer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTrainer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTrainer(PathMetadata metadata, PathInits inits) {
        this(Trainer.class, metadata, inits);
    }

    public QTrainer(Class<? extends Trainer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userMember = inits.isInitialized("userMember") ? new org.zerock.b01.domain.member.QUser_Member(forProperty("userMember"), inits.get("userMember")) : null;
    }

}

