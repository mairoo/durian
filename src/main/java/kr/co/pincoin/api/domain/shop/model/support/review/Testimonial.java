package kr.co.pincoin.api.domain.shop.model.support.review;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.infra.shop.entity.support.review.TestimonialEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Testimonial {
  private final Long id;

  private final User owner;

  private final Store store;

  private final LocalDateTime created;

  private final LocalDateTime modified;

  private String title;

  private String description;

  private Set<String> keywords;

  private String content;

  private Boolean isRemoved;

  private Boolean isHidden;

  private Integer answerCount;

  @Builder
  private Testimonial(
      Long id,
      String title,
      String description,
      String keywords,
      String content,
      User owner,
      Store store,
      LocalDateTime created,
      LocalDateTime modified,
      Boolean isRemoved) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.keywords = parseKeywords(keywords);
    this.content = content;
    this.owner = owner;
    this.store = store;
    this.created = created;
    this.modified = modified;
    this.isRemoved = isRemoved;
    this.isHidden = false;
    this.answerCount = 0;

    validateTestimonial();
  }

  public TestimonialEntity toEntity() {
    return TestimonialEntity.builder()
        .id(this.getId())
        .title(this.getTitle())
        .description(this.getDescription())
        .keywords(String.join(",", this.getKeywords())) // Set<String>을 comma-separated string으로 변환
        .content(this.getContent())
        .owner(this.getOwner().toEntity())
        .store(this.getStore().toEntity())
        .build();
  }

  public static Testimonial of(String title, String content, User owner, Store store) {
    return Testimonial.builder().title(title).content(content).owner(owner).store(store).build();
  }

  public void updateTitle(String title) {
    validateTitle(title);
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

  public void hide() {
    this.isHidden = true;
  }

  public void show() {
    this.isHidden = false;
  }

  public void softDelete() {
    this.isRemoved = true;
  }

  public void restore() {
    this.isRemoved = false;
  }

  public void incrementAnswerCount() {
    this.answerCount++;
  }

  public boolean belongsToStore(Long storeId) {
    return this.store != null && this.store.getId().equals(storeId);
  }

  public boolean hasKeyword(String keyword) {
    return this.keywords != null && this.keywords.contains(keyword.toLowerCase());
  }

  public boolean isRecent() {
    return this.created.isAfter(LocalDateTime.now().minusDays(7));
  }

  public boolean isModified() {
    return !this.created.equals(this.modified);
  }

  public boolean hasAnswers() {
    return this.answerCount > 0;
  }

  public boolean canBeModified() {
    return !this.isRemoved && !this.hasAnswers();
  }

  public boolean isHidden() {
    return this.isHidden;
  }

  public boolean isVisible() {
    return !this.isHidden && !this.isRemoved;
  }

  private Set<String> parseKeywords(String keywords) {
    if (keywords == null || keywords.trim().isEmpty()) {
      return new HashSet<>();
    }
    return new HashSet<>(Arrays.asList(keywords.toLowerCase().split(",\\s*")));
  }

  private void validateTestimonial() {
    validateTitle(this.title);
    validateContent(this.content);

    if (owner == null) {
      throw new IllegalArgumentException("Owner cannot be null");
    }
    if (store == null) {
      throw new IllegalArgumentException("Store cannot be null");
    }
  }

  private void validateTitle(String title) {
    if (title == null || title.trim().isEmpty()) {
      throw new IllegalArgumentException("Title cannot be empty");
    }
    if (title.length() > 200) {
      throw new IllegalArgumentException("Title cannot exceed 200 characters");
    }
  }

  private void validateContent(String content) {
    if (content == null || content.trim().isEmpty()) {
      throw new IllegalArgumentException("Content cannot be empty");
    }
    if (content.length() > 2000) {
      throw new IllegalArgumentException("Content cannot exceed 2000 characters");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    Testimonial that = (Testimonial) o;

    if (id != null && that.id != null) {
      return id.equals(that.id);
    }

    return created.equals(that.created)
        && owner.getId().equals(that.owner.getId())
        && store.getId().equals(that.store.getId());
  }

  @Override
  public int hashCode() {
    if (id != null) {
      return id.hashCode();
    }
    return 31 * (31 * created.hashCode() + owner.getId().hashCode()) + store.getId().hashCode();
  }
}
