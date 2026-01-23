/* 
 * Copyright (c) SKU LIKELION 
 */
package com.skunivlikelion.homepage.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skunivlikelion.homepage.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findFirstByEmailOrStudentNumberOrPhoneNumber(
      String email, String studentNumber, String phoneNumber);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  @Query(
      """
SELECT u FROM User u
WHERE NOT EXISTS (SELECT cm FROM ClubMember cm WHERE cm.user = u)
AND (:keyword IS NULL OR :keyword = ''
 OR u.name LIKE CONCAT('%', :keyword, '%')
 OR u.department LIKE CONCAT('%', :keyword, '%'))
""")
  List<User> findGuestUsers(@Param("keyword") String keyword);

  @Query(
      """
SELECT u FROM User u
WHERE EXISTS (SELECT cm FROM ClubMember cm WHERE cm.user = u)
AND (:keyword IS NULL OR :keyword = ''
 OR u.name LIKE CONCAT('%', :keyword, '%')
 OR u.department LIKE CONCAT('%', :keyword, '%'))
""")
  List<User> findClubMemberUsers(@Param("keyword") String keyword);

  List<User> findAllByIdIn(List<Long> ids);

  long countByIdIn(List<Long> ids);
}
