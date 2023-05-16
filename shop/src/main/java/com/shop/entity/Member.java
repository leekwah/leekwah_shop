package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true) // Member 엔티티는 이메일을 통해 구분해야하기 때문에, 이메일에는 unique 속성을 지정한다.
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING) // enum 타입을 엔티티 속성으로 지정할 수 있다. 하지만 enum의 순서가 바뀔 경우 문제가 발생할 수 있기 때문에, "EnumType.STRING" 옵션으로 String으로 저장한다.
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) { // Member 엔티티에 회원을 생성하는 메서드를 만들어서 관리하면, 코드가 변경되더라도 한 군데만 수정하면 되는 이점이 있다.
        Member member = new Member();

        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword()); // 스프링 시큐리티 클래스에서 설정한 BCryptPasswordEncoder Bean을 파라미터로 넘겨서 비밀번호를 암호화한다.
        member.setPassword(password);
        member.setRole(Role.USER);

        return member;
    }
}
