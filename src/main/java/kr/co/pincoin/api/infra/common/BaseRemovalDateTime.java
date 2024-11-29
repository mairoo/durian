package kr.co.pincoin.api.infra.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseRemovalDateTime extends BaseDateTime {
    @Column(name = "is_removed")
    private boolean isRemoved;

    public void softDelete() {
        this.isRemoved = true;
    }

    public void restore() {
        this.isRemoved = false;
    }
}
