package kr.co.pincoin.api.infra.shop.entity.store;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.common.BaseDateTime;
import lombok.*;

@Entity
@Table(name = "shop_store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class StoreEntity extends BaseDateTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "code")
  private String code;

  @Column(name = "theme")
  private String theme;

  @Column(name = "phone")
  private String phone;

  @Column(name = "phone1")
  private String phone1;

  @Column(name = "kakao")
  private String kakao;

  @Column(name = "bank_account")
  private String bankAccount;

  @Column(name = "escrow_account")
  private String escrowAccount;

  @Column(name = "chunk_size")
  private Integer chunkSize;

  @Column(name = "block_size")
  private Integer blockSize;

  @Column(name = "signup_open")
  private Boolean signupOpen;

  @Column(name = "under_attack")
  private Boolean underAttack;
}
