package kr.co.pincoin.api.infra.shop.entity.support.message;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.common.BaseDateTime;
import lombok.*;

@Entity
@Table(name = "shop_shortmessageservice")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class SmsEntity extends BaseDateTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "phone_from")
  private String phoneFrom;

  @Column(name = "phone_to")
  private String phoneTo;

  @Column(name = "content")
  private String content;

  @Column(name = "success")
  private Boolean success;
}
