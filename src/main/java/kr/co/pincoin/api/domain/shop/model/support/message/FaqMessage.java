package kr.co.pincoin.api.domain.shop.model.support.message;

import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.domain.shop.model.support.message.enums.FaqCategory;
import kr.co.pincoin.api.infra.shop.entity.support.message.FaqMessageEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public class FaqMessage {
    private final Long id;

    private final FaqCategory category;

    private final User owner;

    private final Store store;

    private final LocalDateTime created;

    private final LocalDateTime modified;

    private String title;

    private String description;

    private Set<String> keywords;

    private String content;

    private Integer position;

    private Boolean isRemoved;

    @Builder
    private FaqMessage(Long id,
                       String title,
                       String description,
                       String keywords,
                       String content,
                       FaqCategory category,
                       Integer position,
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
        this.category = category;
        this.position = position;
        this.owner = owner;
        this.store = store;
        this.created = created;
        this.modified = modified;
        this.isRemoved = isRemoved;

        validateFaq();
    }

    public FaqMessageEntity toEntity() {
        return FaqMessageEntity.builder()
                .id(this.getId())
                .title(this.getTitle())
                .description(this.getDescription())
                .keywords(String.join(",", this.getKeywords())) // Set<String>을 comma-separated string으로 변환
                .content(this.getContent())
                .category(this.getCategory())
                .position(this.getPosition())
                .owner(this.getOwner().toEntity())
                .store(this.getStore().toEntity())
                .build();
    }

    public static FaqMessage of(String title,
                                String content,
                                FaqCategory category,
                                User owner,
                                Store store) {
        return FaqMessage.builder()
                .title(title)
                .content(content)
                .category(category)
                .owner(owner)
                .store(store)
                .build();
    }

    public void
    updateTitle(String title) {
        validateTitle(title);
        this.title = title;
    }

    public void
    updateContent(String content) {
        validateContent(content);
        this.content = content;
    }

    public void
    updateDescription(String description) {
        this.description = description;
    }

    public void
    updateKeywords(String keywords) {
        this.keywords = parseKeywords(keywords);
    }

    public void
    updatePosition(Integer position) {
        if (position == null || position < 0) {
            throw new IllegalArgumentException("Position must be non-negative");
        }
        this.position = position;
    }

    public void
    moveToTop() {
        this.position = 0;
    }

    public void
    moveToBottom(int maxPosition) {
        if (maxPosition < 0) {
            throw new IllegalArgumentException("Max position must be non-negative");
        }
        this.position = maxPosition + 1;
    }

    public void
    moveUp() {
        if (this.position > 0) {
            this.position--;
        }
    }

    public void
    moveDown() {
        this.position++;
    }

    public void
    softDelete() {
        this.isRemoved = true;
    }

    public void
    restore() {
        this.isRemoved = false;
    }

    public boolean
    belongsToStore(Long storeId) {
        return this.store != null && this.store.getId().equals(storeId);
    }

    public boolean
    hasKeyword(String keyword) {
        return this.keywords != null &&
                this.keywords.contains(keyword.toLowerCase());
    }

    private Set<String>
    parseKeywords(String keywords) {
        if (keywords == null || keywords.trim().isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(
                keywords.toLowerCase().split(",\\s*")));
    }

    private void
    validateFaq() {
        validateTitle(this.title);
        validateContent(this.content);

        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        if (store == null) {
            throw new IllegalArgumentException("Store cannot be null");
        }
        if (position != null && position < 0) {
            throw new IllegalArgumentException("Position must be non-negative");
        }
    }

    private void
    validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("Title cannot exceed 200 characters");
        }
    }

    private void
    validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        if (content.length() > 2000) {
            throw new IllegalArgumentException("Content cannot exceed 2000 characters");
        }
    }
}