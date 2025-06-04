package org.zerock.b01.domain.board;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotice_Board is a Querydsl query type for Notice_Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotice_Board extends EntityPathBase<Notice_Board> {

    private static final long serialVersionUID = -1965656494L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotice_Board notice_Board = new QNotice_Board("notice_Board");

    public final org.zerock.b01.domain.QBaseEntity _super = new org.zerock.b01.domain.QBaseEntity(this);

    public final org.zerock.b01.domain.QAll_Member allMember;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath nContent = createString("nContent");

    public final NumberPath<Integer> nHits = createNumber("nHits", Integer.class);

    public final NumberPath<Long> noticeId = createNumber("noticeId", Long.class);

    public final StringPath nTitle = createString("nTitle");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public QNotice_Board(String variable) {
        this(Notice_Board.class, forVariable(variable), INITS);
    }

    public QNotice_Board(Path<? extends Notice_Board> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotice_Board(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotice_Board(PathMetadata metadata, PathInits inits) {
        this(Notice_Board.class, metadata, inits);
    }

    public QNotice_Board(Class<? extends Notice_Board> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.allMember = inits.isInitialized("allMember") ? new org.zerock.b01.domain.QAll_Member(forProperty("allMember")) : null;
    }

}

