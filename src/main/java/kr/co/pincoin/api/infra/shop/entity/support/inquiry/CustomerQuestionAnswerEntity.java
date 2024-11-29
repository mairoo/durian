package kr.co.pincoin.api.infra.shop.entity.support.inquiry;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.common.BaseDateTime;
import lombok.*;

@Entity
@Table(name = "shop_questionanswer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class CustomerQuestionAnswerEntity extends BaseDateTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "content")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id")
  private CustomerQuestionEntity question;
}
