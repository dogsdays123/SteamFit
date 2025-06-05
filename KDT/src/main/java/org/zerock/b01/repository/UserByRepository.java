package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.UserBy;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserByRepository extends JpaRepository<UserBy, String> {

    @EntityGraph(attributePaths = "roleSet")
    @Query("select u from UserBy u where u.uId = :uId")
    Optional<UserBy> getWithRoles(@Param("uId") String uId);

    @Query("select u from UserBy u where u.uId =:uId")
    Optional<UserBy> findByUId(String uId);
}
