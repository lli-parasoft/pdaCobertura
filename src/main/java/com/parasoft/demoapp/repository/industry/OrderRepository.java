package com.parasoft.demoapp.repository.industry;

import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    OrderEntity findOrderByOrderNumber(String number);

	List<OrderEntity> findAllByRequestedBy(String requestedBy);

    Page<OrderEntity> findAllByRequestedBy(String requestedBy, Pageable pageable);

    Page<OrderEntity> findAllByStatusNotAndStatusNot(OrderStatus status1, OrderStatus status2, Pageable pageable);

    int countByRequestedByAndReviewedByPRCH(String requestedBy, boolean b);

    int countByReviewedByAPV(boolean b);
}
