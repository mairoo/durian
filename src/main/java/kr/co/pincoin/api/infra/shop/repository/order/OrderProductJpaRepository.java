package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductJpaRepository extends JpaRepository<OrderProductEntity, Long> {
    @Query("SELECT op FROM OrderProductEntity op " +
            "JOIN FETCH op.order o " +
            "JOIN FETCH o.user " +
            "WHERE o.orderNo = :orderNo " +
            "AND o.user.id = :userId")
    List<OrderProductEntity>
    findAllByOrderNoAndUserIdFetchOrderAndUser(@Param("orderNo") String orderNo,
                                               @Param("userId") Integer userId);
}
