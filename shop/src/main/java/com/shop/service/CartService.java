package com.shop.service;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public Long addCart(CartItemDto cartItemDto, String email) {

        Item item = itemRepository.findById(cartItemDto.getItemId()) // 장바구니에 담을 상품 엔티티를 조회한다.
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email); // 현재 로그인한 회원 엔티티를 조회한다.

        Cart cart = cartRepository.findByMemberId(member.getId()); // 현재 로그인한 회원의 장바구니 엔티티를 조회한다.
        if (cart == null) { // 카트가 없는 경우(상품이 처음 담긴 경우) 해당 회원의 장바구니를 생성한다.
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId()); // 현재 상품이 장바구니에 이미 들어가있는지 조회한다.

        if (savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount()); // 장바구니에 이미 있던 상품인 경우 기존 수량에 현재 장바구니에 담을 수량만큼을 더해준다.
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount()); // 장바구니 엔티티, 상품 엔티티, 장바구니에 담을 수량을 이용하여 CartItem 엔티티를 생성한다.
            cartItemRepository.save(cartItem); // 장바구니에 들어갈 상품을 저장한다.

            return cartItem.getId();
        }
    }

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId()); // 현재 로그인한 회원의 장바구니 엔티티를 조회한다.

        if (cart == null) { // 장바구니에 상품을 한 번도 안담았을 경우, 장바구니 엔티티가 없으므로 빈 리스트를 반환한다.
            return  cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId()); // 장바구니에 담겨있는 상품 정보를 조회한다.

        return cartDetailDtoList;
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email) {
        Member curMember = memberRepository.findByEmail(email); // 현재 로그인한 회원을 조회한다.
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember(); // 장바구니 상품을 저장한 회원을 조회한다.

        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) { // 현재 로그인한 회원과 장바구니 상품을 저장한 회원이 다를 경우 false를 반환한다.
            return false;
        }

        return true; // 같다면 true를 반환한다.
    }

    public void updateCartItemCount(Long cartItemId, int count) { // 장바구니 상품의 수량을 업데이트하는 메서드이다.
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }
}