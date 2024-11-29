package kr.co.pincoin.api.infra.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseDateTime {
  @CreatedDate
  @Column(name = "created", updatable = false)
  private LocalDateTime created;

  @LastModifiedDate
  @Column(name = "modified")
  private LocalDateTime modified;
}
