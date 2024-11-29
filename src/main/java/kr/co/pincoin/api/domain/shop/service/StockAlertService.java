package kr.co.pincoin.api.domain.shop.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StockAlertService {
  private static final BigDecimal NEXON_THRESHOLD = new BigDecimal("30000");

  private static final double MIN_STOCK_WEIGHT = 0.7;

  private static final double MAX_STOCK_WEIGHT = 0.3;
}
