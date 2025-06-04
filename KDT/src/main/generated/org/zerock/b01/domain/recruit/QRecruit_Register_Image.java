package org.zerock.b01.domain.recruit;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecruit_Register_Image is a Querydsl query type for Recruit_Register_Image
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecruit_Register_Image extends EntityPathBase<Recruit_Register_Image> {

    private static final long serialVersionUID = 2048789947L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecruit_Register_Image recruit_Register_Image = new QRecruit_Register_Image("recruit_Register_Image");

    public final StringPath re_img_id = createString("re_img_id");

    public final NumberPath<Integer> re_img_ord = createNumber("re_img_ord", Integer.class);

    public final StringPath re_img_title = createString("re_img_title");

    public final QRecruit_Register recruit_register;

    public QRecruit_Register_Image(String variable) {
        this(Recruit_Register_Image.class, forVariable(variable), INITS);
    }

    public QRecruit_Register_Image(Path<? extends Recruit_Register_Image> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecruit_Register_Image(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecruit_Register_Image(PathMetadata metadata, PathInits inits) {
        this(Recruit_Register_Image.class, metadata, inits);
    }

    public QRecruit_Register_Image(Class<? extends Recruit_Register_Image> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.recruit_register = inits.isInitialized("recruit_register") ? new QRecruit_Register(forProperty("recruit_register"), inits.get("recruit_register")) : null;
    }

}

