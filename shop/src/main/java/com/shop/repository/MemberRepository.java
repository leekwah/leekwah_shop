package com.shop.repository;

import com.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email); // 회원 가입 시 중복된 이메일이 있는 지 확인하기 위한 쿼리 메서드를 작성한다.
}
