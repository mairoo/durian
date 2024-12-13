package kr.co.pincoin.api.domain.shop.model.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CategoryDetached {

    // 핵심 식별 정보
    private final Long id;
    private final String title;
    private final String slug;

    // 결제 관련 정보
    private final Boolean pg;

    // 연관 관계 ID
    private final Long parentId;
    private final Long storeId;

    // 생성/수정 시간
    private final LocalDateTime created;
    private final LocalDateTime modified;

    // 이미지 및 설명
    private final String thumbnail;
    private final String description;
    private final String description1;

    // 할인율 정보
    private final BigDecimal discountRate;
    private final BigDecimal pgDiscountRate;

    // 네이버 관련 정보
    private final String naverSearchTag;
    private final String naverBrandName;
    private final String naverMakerName;

    // Nested Set Model 정보
    private final Integer lft;
    private final Integer rght;
    private final Integer treeId;
    private final Integer level;

    public CategoryDetached(
        Long id,
        String title,
        String slug,
        Boolean pg,
        Long parentId,
        Long storeId,
        LocalDateTime created,
        LocalDateTime modified,
        String thumbnail,
        String description,
        String description1,
        BigDecimal discountRate,
        BigDecimal pgDiscountRate,
        String naverSearchTag,
        String naverBrandName,
        String naverMakerName,
        Integer lft,
        Integer rght,
        Integer treeId,
        Integer level) {

        this.id = id;
        this.title = title;
        this.slug = slug;
        this.pg = pg;
        this.parentId = parentId;
        this.storeId = storeId;
        this.created = created;
        this.modified = modified;
        this.thumbnail = thumbnail;
        this.description = description;
        this.description1 = description1;
        this.discountRate = discountRate;
        this.pgDiscountRate = pgDiscountRate;
        this.naverSearchTag = naverSearchTag;
        this.naverBrandName = naverBrandName;
        this.naverMakerName = naverMakerName;
        this.lft = lft;
        this.rght = rght;
        this.treeId = treeId;
        this.level = level;
    }
}