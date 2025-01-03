package kr.co.pincoin.api.infra.shop.repository.order;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductVoucherJdbcRepository {
  private final JdbcTemplate jdbcTemplate;

  public void batchInsert(List<OrderProductVoucher> vouchers) {
    jdbcTemplate.batchUpdate(
        "INSERT INTO shop_orderproductvoucher (code, order_product_id, voucher_id, remarks, revoked, created, modified, is_removed) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            OrderProductVoucher voucher = vouchers.get(i);
            ps.setString(1, voucher.getCode());
            ps.setLong(2, voucher.getOrderProduct().getId());
            ps.setLong(3, voucher.getVoucher().getId());
            ps.setString(4, voucher.getRemarks());
            ps.setBoolean(5, voucher.isRevoked());
            ps.setTimestamp(6, Timestamp.valueOf(voucher.getCreated()));
            ps.setTimestamp(7, Timestamp.valueOf(voucher.getModified()));
            ps.setBoolean(8, voucher.getIsRemoved());
          }

          @Override
          public int getBatchSize() {
            return vouchers.size();
          }
        });
  }
}
