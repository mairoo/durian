package kr.co.pincoin.api.infra.auth.entity.email;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.common.BaseRemovalDateTime;
import lombok.*;

@Entity
@Table(name = "member_emailbanned")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class BannedEmailEntity extends BaseRemovalDateTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "email")
  private String email;
}
