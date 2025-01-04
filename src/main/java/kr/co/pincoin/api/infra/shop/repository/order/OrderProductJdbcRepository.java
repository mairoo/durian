package kr.co.pincoin.api.infra.shop.repository.order;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductJdbcRepository {
  private final JdbcTemplate jdbcTemplate;

  public void batchInsert(List<OrderProduct> orderProducts) {
    LocalDateTime now = LocalDateTime.now();

    jdbcTemplate.batchUpdate(
        """
            INSERT INTO shop_orderproduct (
                name, subtitle, code, list_price, selling_price, quantity,
                order_id, created, modified, is_removed
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            OrderProduct orderProduct = orderProducts.get(i);
            ps.setString(1, orderProduct.getName());
            ps.setString(2, orderProduct.getSubtitle());
            ps.setString(3, orderProduct.getCode());
            ps.setBigDecimal(4, orderProduct.getListPrice());
            ps.setBigDecimal(5, orderProduct.getSellingPrice());
            ps.setInt(6, orderProduct.getQuantity());
            if (orderProduct.getOrder() != null) {
              ps.setLong(7, orderProduct.getOrder().getId());
            } else {
              ps.setNull(7, Types.BIGINT);
            }
            ps.setTimestamp(8, Timestamp.valueOf(now));
            ps.setTimestamp(9, Timestamp.valueOf(now));
            ps.setBoolean(10, false);
          }

          @Override
          public int getBatchSize() {
            return orderProducts.size();
          }
        });
  }
}
