package com.ohgiraffers.jwtrestapi.purchase.service;

import com.ohgiraffers.jwtrestapi.member.repository.MemberRepository;
import com.ohgiraffers.jwtrestapi.product.entity.Product;
import com.ohgiraffers.jwtrestapi.product.repository.ProductRepository;
import com.ohgiraffers.jwtrestapi.purchase.dto.OrderAndProductDTO;
import com.ohgiraffers.jwtrestapi.purchase.dto.PurchaseDTO;
import com.ohgiraffers.jwtrestapi.purchase.entity.Order;
import com.ohgiraffers.jwtrestapi.purchase.entity.OrderAndProduct;
import com.ohgiraffers.jwtrestapi.purchase.repository.OrderAndProductRepository;
import com.ohgiraffers.jwtrestapi.purchase.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {


	private final MemberRepository memberRepository;
	private final OrderRepository orderRepository;
	private final ModelMapper modelMapper;
	private final ProductRepository productRepository;
	private final OrderAndProductRepository orderAndProductRepository;


	@Transactional
	public String insertProduct(PurchaseDTO purchaseDTO) {
		log.info("[OrderService] insertPurchase() Start");
		log.info("[OrderService] purchaseDTO : {}", purchaseDTO);

		int result = 0;

		try {
			/* 1. 해당 주문을 진행하고 있는 회원의 PK 값 조회 */
			int memberCode = memberRepository.findMemberCodeByMemberId(purchaseDTO.getMemberId());

			/* 2. 주문 INSERT */
			Date now = new Date();
			// 주문 date 값을 포멧팅
			SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
			String orderDate = sdf.format(now);

			Order order = Order.builder()
					.productCode(purchaseDTO.getProductCode())
					.orderMember(memberCode)
					.orderPhone(purchaseDTO.getOrderPhone())
					.orderAddress(purchaseDTO.getOrderAddress())
					.orderDate(orderDate)
					.orderEmail(purchaseDTO.getOrderEmail())
					.orderReceiver(purchaseDTO.getOrderReceiver())
					.orderAmount(String.valueOf(purchaseDTO.getOrderAmount()))
					.build();

			// 위에 생성한 order 인스턴스
			orderRepository.save(order);

			/* 3. 상품(Product) 재고 Update */
			// 상품 한 행 식별
			Product product =
					productRepository.findById(Integer.valueOf(order.getProductCode())).get();

			// 재고 업데이트
			product = product.toBuilder()
					// 기존 재고 - 주문 시 양
					.productStock(product.getProductStock() - purchaseDTO.getOrderAmount())
					.build();

			// 업데이트 반영
			productRepository.save(product);

			result = 1;
		} catch (Exception e) {
			log.error("[Order] Exception 발생!! " , e);
		}

		log.info("[OrderService] insertPurchase() End");
		return (result > 0) ?  "주문 성공!!" : "주문 실패ㅠㅠ";
	}

	public List<OrderAndProductDTO> selectPurchaseList(String memberId) {
		log.info("[OrderService] selectPurchaseList() Start");

		// 우리에게 주어진 힌트가 memberId 이기 때문에 Id 로 Code(식별자) 조회
		int memberCode = memberRepository.findMemberCodeByMemberId(memberId);

		// 제품과 주문 엔티티 연관관계 형성
		List<OrderAndProduct> orderList = orderAndProductRepository.findByOrderMember(memberCode);

        log.info("[OrderService] purchaseList {}", orderList);

        log.info("[OrderService] selectPurchaseList() End");
        
        return orderList.stream().map(
				order -> modelMapper.map(order , OrderAndProductDTO.class)
		).collect(Collectors.toList());
	}

}
