package org.zerock.b01.domain.trainer;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTrainer_Thumbnails is a Querydsl query type for Trainer_Thumbnails
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTrainer_Thumbnails extends EntityPathBase<Trainer_Thumbnails> {

    private static final long serialVersionUID = -877371531L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTrainer_Thumbnails trainer_Thumbnails = new QTrainer_Thumbnails("trainer_Thumbnails");

    public final StringPath imgname = createString("imgname");

    public final NumberPath<Integer> ord = createNumber("ord", Integer.class);

    public final QTrainer trainer;

    public final NumberPath<Long> tthumbnailsId = createNumber("tthumbnailsId", Long.class);

    public QTrainer_Thumbnails(String variable) {
        this(Trainer_Thumbnails.class, forVariable(variable), INITS);
    }

    public QTrainer_Thumbnails(Path<? extends Trainer_Thumbnails> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTrainer_Thumbnails(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTrainer_Thumbnails(PathMetadata metadata, PathInits inits) {
        this(Trainer_Thumbnails.class, metadata, inits);
    }

    public QTrainer_Thumbnails(Class<? extends Trainer_Thumbnails> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.trainer = inits.isInitialized("trainer") ? new QTrainer(forProperty("trainer"), inits.get("trainer")) : null;
    }

}

