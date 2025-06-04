package org.zerock.b01.domain.board;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoard_Reply is a Querydsl query type for Board_Reply
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard_Reply extends EntityPathBase<Board_Reply> {

    private static final long serialVersionUID = -1614025186L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoard_Reply board_Reply = new QBoard_Reply("board_Reply");

    public final org.zerock.b01.domain.QBaseEntity _super = new org.zerock.b01.domain.QBaseEntity(this);

    public final org.zerock.b01.domain.QAll_Member allMember;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final QNotice_Board noticeBoard;

    public final QQna_Board qnaBoard;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> replyId = createNumber("replyId", Long.class);

    public final StringPath replyText = createString("replyText");

    public QBoard_Reply(String variable) {
        this(Board_Reply.class, forVariable(variable), INITS);
    }

    public QBoard_Reply(Path<? extends Board_Reply> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoard_Reply(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoard_Reply(PathMetadata metadata, PathInits inits) {
        this(Board_Reply.class, metadata, inits);
    }

    public QBoard_Reply(Class<? extends Board_Reply> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.allMember = inits.isInitialized("allMember") ? new org.zerock.b01.domain.QAll_Member(forProperty("allMember")) : null;
        this.noticeBoard = inits.isInitialized("noticeBoard") ? new QNotice_Board(forProperty("noticeBoard"), inits.get("noticeBoard")) : null;
        this.qnaBoard = inits.isInitialized("qnaBoard") ? new QQna_Board(forProperty("qnaBoard"), inits.get("qnaBoard")) : null;
    }

}

