package kr.co.pincoin.api.app.admin.product.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminProductResponse {

    @JsonProperty("id")
    private final Long id;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("subtitle")
    private final String subtitle;

    @JsonProperty("code")
    private final String code;

    @JsonProperty("listPrice")
    private final BigDecimal listPrice;

    @JsonProperty("sellingPrice")
    private final BigDecimal sellingPrice;

    @JsonProperty("pg")
    private final Boolean pg;

    @JsonProperty("pgSellingPrice")
    private final BigDecimal pgSellingPrice;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("position")
    private final Integer position;

    @JsonProperty("status")
    private final ProductStatus status;

    @JsonProperty("stock")
    private final ProductStock stock;

    @JsonProperty("categoryId")
    private final Long categoryId;


    // 생성자 외부 접근 불허 / 자식 허용
    protected AdminProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.subtitle = product.getSubtitle();
        this.code = product.getCode();
        this.listPrice = product.getListPrice();
        this.sellingPrice = product.getSellingPrice();
        this.pg = product.getPg();
        this.pgSellingPrice = product.getPgSellingPrice();
        this.description = product.getDescription();
        this.position = product.getPosition();
        this.status = product.getStatus();
        this.stock = product.getStock();
        this.categoryId = product.getCategory().getId();
    }

    protected AdminProductResponse(ProductDetached product) {
        this.id = product.getId();
        this.name = product.getName();
        this.subtitle = product.getSubtitle();
        this.code = product.getCode();
        this.listPrice = product.getListPrice();
        this.sellingPrice = product.getSellingPrice();
        this.pg = product.getPg();
        this.pgSellingPrice = product.getPgSellingPrice();
        this.description = product.getDescription();
        this.position = product.getPosition();
        this.status = product.getStatus();
        this.stock = product.getStock();
        this.categoryId = product.getCategoryId();
    }

    // 도메인 모델 객체에서 응답 객체 초기화
    public static AdminProductResponse from(Product product) {
        return new AdminProductResponse(product);
    }

    public static AdminProductResponse from(ProductDetached product) {
        return new AdminProductResponse(product);
    }
}