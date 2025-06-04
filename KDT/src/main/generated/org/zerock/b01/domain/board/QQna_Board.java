package org.zerock.b01.domain.board;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQna_Board is a Querydsl query type for Qna_Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQna_Board extends EntityPathBase<Qna_Board> {

    private static final long serialVersionUID = -1046842920L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQna_Board qna_Board = new QQna_Board("qna_Board");

    public final org.zerock.b01.domain.QBaseEntity _super = new org.zerock.b01.domain.QBaseEntity(this);

    public final org.zerock.b01.domain.QAll_Member allMember;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath qContent = createString("qContent");

    public final NumberPath<Integer> qHits = createNumber("qHits", Integer.class);

    public final NumberPath<Long> qnaId = createNumber("qnaId", Long.class);

    public final StringPath qTitle = createString("qTitle");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public QQna_Board(String variable) {
        this(Qna_Board.class, forVariable(variable), INITS);
    }

    public QQna_Board(Path<? extends Qna_Board> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQna_Board(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQna_Board(PathMetadata metadata, PathInits inits) {
        this(Qna_Board.class, metadata, inits);
    }

    public QQna_Board(Class<? extends Qna_Board> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.allMember = inits.isInitialized("allMember") ? new org.zerock.b01.domain.QAll_Member(forProperty("allMember")) : null;
    }

}

