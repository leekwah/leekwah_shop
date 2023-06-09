package com.shop.shop.entity;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberTest {
    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("Auditing Test")
    @WithMockUser(username = "user", roles = "USER")
    public void auditingTest() {
        Member newmember = new Member();
        memberRepository.save(newmember);

        em.flush();
        em.clear();

        Member member = memberRepository.findById(newmember.getId())
                .orElseThrow(EntityNotFoundException::new);

        System.out.println("Register Time : " + member.getRegTime());
        System.out.println("Update Time : " + member.getUpdateTime());
        System.out.println("Create Member : " + member.getCreatedBy());
        System.out.println("Modify Member : " + member.getModifiedBy());
    }
}