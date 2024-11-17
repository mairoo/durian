package kr.co.pincoin.api.domain.shop.model.support.message;

import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.infra.shop.entity.support.message.FaqMessageEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public class FaqMessage {
    private final Long id;
    private final Integer category;
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

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "instanceBuilder")
    private FaqMessage(String title, String description, String keywords,
                       String content, Integer category, Integer position,
                       User owner, Store store) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.keywords = parseKeywords(keywords);
        this.content = content;
        this.category = category;
        this.position = position != null ? position : 0;
        this.owner = owner;
        this.store = store;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
        this.isRemoved = false;

        validateFaq();
    }

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "jpaBuilder")
    private FaqMessage(Long id, String title, String description,
                       String keywords, String content, Integer category,
                       Integer position, User owner, Store store,
                       LocalDateTime created, LocalDateTime modified,
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

    public static FaqMessage of(String title, String content,
                                Integer category, User owner, Store store) {
        return FaqMessage.instanceBuilder()
                .title(title)
                .content(content)
                .category(category)
                .owner(owner)
                .store(store)
                .build();
    }

    public static FaqMessage from(FaqMessageEntity entity) {
        return FaqMessage.jpaBuilder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .keywords(entity.getKeywords())
                .content(entity.getContent())
                .category(entity.getCategory())
                .position(entity.getPosition())
                .owner(User.from(entity.getOwner()))
                .store(Store.from(entity.getStore()))
                .created(entity.getCreated())
                .modified(entity.getModified())
                .isRemoved(entity.getIsRemoved())
                .build();
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

    public void updatePosition(Integer position) {
        if (position == null || position < 0) {
            throw new IllegalArgumentException("Position must be non-negative");
        }
        this.position = position;
    }

    public void moveToTop() {
        this.position = 0;
    }

    public void moveToBottom(int maxPosition) {
        if (maxPosition < 0) {
            throw new IllegalArgumentException("Max position must be non-negative");
        }
        this.position = maxPosition + 1;
    }

    public void moveUp() {
        if (this.position > 0) {
            this.position--;
        }
    }

    public void moveDown() {
        this.position++;
    }

    public void remove() {
        this.isRemoved = true;
    }

    public void restore() {
        this.isRemoved = false;
    }

    public boolean isOwner(Long userId) {
        return this.owner != null && this.owner.getId().equals(userId);
    }

    public boolean belongsToStore(Long storeId) {
        return this.store != null && this.store.getId().equals(storeId);
    }

    public boolean hasKeyword(String keyword) {
        return this.keywords != null &&
                this.keywords.contains(keyword.toLowerCase());
    }

    public boolean matchesCategory(FaqCategory category) {
        return this.category.equals(category.getValue());
    }

    private Set<String> parseKeywords(String keywords) {
        if (keywords == null || keywords.trim().isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(
                keywords.toLowerCase().split(",\\s*")));
    }

    private void validateFaq() {
        validateTitle(this.title);
        validateContent(this.content);

        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (!FaqCategory.isValid(category)) {
            throw new IllegalArgumentException("Invalid FAQ category");
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

    public enum FaqCategory {
        GENERAL(1),
        PRODUCT(2),
        DELIVERY(3),
        PAYMENT(4),
        REFUND(5),
        TECHNICAL(6);

        private final Integer value;

        FaqCategory(Integer value) {
            this.value = value;
        }

        public static boolean isValid(Integer value) {
            for (FaqCategory category : FaqCategory.values()) {
                if (category.value.equals(value)) {
                    return true;
                }
            }
            return false;
        }

        public static FaqCategory fromValue(Integer value) {
            for (FaqCategory category : FaqCategory.values()) {
                if (category.value.equals(value)) {
                    return category;
                }
            }
            throw new IllegalArgumentException("Invalid FAQ category value: " + value);
        }

        public Integer getValue() {
            return value;
        }
    }
}