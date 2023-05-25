package com.shop.shop.entity;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Cart;
import com.shop.entity.Member;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;


    public Member createMember() { // 회원 엔티티를 생성하는 메서드를 만든다.
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("김철수");
        memberFormDto.setAddress("서울시 강남구");
        memberFormDto.setPassword("1234");

        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest() {
        Member member = createMember();
        memberRepository.save(member);

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        em.flush(); // JPA는 영속성 컨텍스트에 데이터를 저장 후 트랜잭션이 끝날 때 flush() 메서드를 호출하여 데이터베이스에 반영한다. 즉, 회원 엔티티와 장바구니 엔티티를 영속성 컨텍스트에 저장 후 엔티티 매니저로부터 강제로 flush() 메서드를 호출하여 데이터베이스에 반영한다.
        em.clear(); // JPA는 영속성 컨텍스트로부터 엔티티를 조회 후 영속성 컨텍스트에 엔티티가 없을 경우 데이터베이스를 조회한다. 실제 데이터베이스에서 장바구니 엔티티를 가지고 올 때, 회원 엔티티도 같이 가지고 오는지 보기 위해서 영속성 컨텍스를 비워준다.

        Cart savedCart = cartRepository.findById(cart.getId()) // 저장된 장바구니 엔티티를 조회한다.
                .orElseThrow(EntityNotFoundException::new); // 만약 아니라면 EntityNotFoundException 예외를 발생시킨다.
        assertEquals(savedCart.getMember().getId(), member.getId()); // 처음에 저장한 member 엔티티의 id와 savedCart에 매핑된 member 엔티티의 id를 비교한다.

    }
}