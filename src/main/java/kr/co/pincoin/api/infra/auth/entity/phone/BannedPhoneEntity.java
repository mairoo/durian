package kr.co.pincoin.api.infra.auth.entity.phone;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.common.BaseRemovalDateTime;
import lombok.*;

@Entity
@Table(name = "member_phonebanned")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class BannedPhoneEntity extends BaseRemovalDateTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "phone")
  private String phone;
}
