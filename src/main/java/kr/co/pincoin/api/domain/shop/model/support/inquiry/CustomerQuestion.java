package kr.co.pincoin.api.domain.shop.model.support.inquiry;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.domain.shop.model.support.inquiry.enums.QuestionCategory;
import kr.co.pincoin.api.infra.shop.entity.support.inquiry.CustomerQuestionEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CustomerQuestion {
  private final Long id;

  private final QuestionCategory category;

  private final User owner;

  private final Order order;

  private final Store store;

  private final LocalDateTime created;

  private final LocalDateTime modified;

  private String title;

  private String description;

  private Set<String> keywords;

  private String content;

  private Boolean isRemoved;

  private int answerCount;

  @Builder
  private CustomerQuestion(
      Long id,
      String title,
      String description,
      String keywords,
      String content,
      QuestionCategory category,
      User owner,
      Order order,
      Store store,
      LocalDateTime created,
      LocalDateTime modified,
      Boolean isRemoved) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.keywords = parseKeywords(keywords);
    this.content = content;
    this.category = category;
    this.owner = owner;
    this.order = order;
    this.store = store;
    this.created = created;
    this.modified = modified;
    this.isRemoved = isRemoved;
    this.answerCount = 0;
  }

  public CustomerQuestionEntity toEntity() {
    return CustomerQuestionEntity.builder()
        .id(this.getId())
        .title(this.getTitle())
        .description(this.getDescription())
        .keywords(String.join(",", this.getKeywords())) // Set<String>을 comma-separated string으로 변환
        .content(this.getContent())
        .category(this.getCategory())
        .owner(this.getOwner().toEntity())
        .order(this.getOrder().toEntity())
        .store(this.getStore().toEntity())
        .build();
  }

  public static CustomerQuestion of(
      String title, String content, QuestionCategory category, User owner, Store store) {
    return CustomerQuestion.builder()
        .title(title)
        .content(content)
        .category(category)
        .owner(owner)
        .store(store)
        .build();
  }

  public void updateTitle(String title) {
    this.title = title;
  }

  public void updateContent(String content) {
    validateContent(content);
    this.content = content;
  }

  public void updateDescription(String description) {
    this.description = description;
  }

  public void updateKeywords(String keywords) {
    this.keywords = parseKeywords(keywords);
  }

  public void incrementAnswerCount() {
    this.answerCount++;
  }

  public void softDelete() {
    this.isRemoved = true;
  }

  public void restore() {
    this.isRemoved = false;
  }

  public boolean isClosed() {
    return this.isRemoved;
  }

  public boolean hasOrder() {
    return this.order != null;
  }

  public boolean belongsToStore(Long storeId) {
    return this.store != null && this.store.getId().equals(storeId);
  }

  public boolean hasKeyword(String keyword) {
    return this.keywords != null && this.keywords.contains(keyword.toLowerCase());
  }

  private Set<String> parseKeywords(String keywords) {
    if (keywords == null || keywords.trim().isEmpty()) {
      return new HashSet<>();
    }
    return new HashSet<>(Arrays.asList(keywords.toLowerCase().split(",\\s*")));
  }

  private void validateContent(String content) {
    if (content == null || content.trim().isEmpty()) {
      throw new IllegalArgumentException("Content cannot be empty");
    }
    if (content.length() > 2000) {
      throw new IllegalArgumentException("Content cannot exceed 2000 characters");
    }
  }
}
