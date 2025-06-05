package org.zerock.b01.serviceImpl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.allDTO.*;
import org.zerock.b01.service.AllSearch;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class AllSearchImpl extends QuerydslRepositorySupport implements AllSearch {

    public AllSearchImpl() {
        super(UserBy.class);
    }

    @Override
    public Page<UserByAllDTO> userBySearchWithAll(String[] types, String keyword, String uName,
                                                  String userJob, String userRank, LocalDate regDate,
                                                  String status, String uId, Pageable pageable){
        QUserBy userBy = QUserBy.userBy;
        JPQLQuery<UserBy> query = from(userBy);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // keyword로 여러 필드 검색
        if (keyword != null && !keyword.isEmpty()) {
            booleanBuilder.and(userBy.uId.contains(keyword))
                    .or(userBy.uName.contains(keyword));
        }

        if (uName != null && !uName.isEmpty()) {
            booleanBuilder.and(userBy.uName.contains(uName));
        }

        if (regDate != null) {
            booleanBuilder.and(userBy.regDate.goe(regDate.atStartOfDay()));
        }

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        query.orderBy(userBy.regDate.desc());
        List<UserBy> resultList = query.fetch();

        List<UserByAllDTO> dtoList = resultList.stream()
                .map(user -> UserByAllDTO.builder()
                        .uId(user.getUId())
                        .uName(user.getUName())
                        .modDate(user.getModDate())
                        .build())
                .collect(Collectors.toList());

        // 전체 개수
        // 카운트용 별도 쿼리 생성
        JPQLQuery<UserBy> countQuery = from(userBy).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }
}
