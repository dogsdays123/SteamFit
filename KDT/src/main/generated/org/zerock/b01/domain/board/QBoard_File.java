package org.zerock.b01.domain.board;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoard_File is a Querydsl query type for Board_File
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard_File extends EntityPathBase<Board_File> {

    private static final long serialVersionUID = -606608440L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoard_File board_File = new QBoard_File("board_File");

    public final StringPath fileName = createString("fileName");

    public final QNotice_Board noticeBoard;

    public final NumberPath<Integer> ord = createNumber("ord", Integer.class);

    public final QQna_Board qnaBoard;

    public final StringPath uuid = createString("uuid");

    public QBoard_File(String variable) {
        this(Board_File.class, forVariable(variable), INITS);
    }

    public QBoard_File(Path<? extends Board_File> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoard_File(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoard_File(PathMetadata metadata, PathInits inits) {
        this(Board_File.class, metadata, inits);
    }

    public QBoard_File(Class<? extends Board_File> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.noticeBoard = inits.isInitialized("noticeBoard") ? new QNotice_Board(forProperty("noticeBoard"), inits.get("noticeBoard")) : null;
        this.qnaBoard = inits.isInitialized("qnaBoard") ? new QQna_Board(forProperty("qnaBoard"), inits.get("qnaBoard")) : null;
    }

}

