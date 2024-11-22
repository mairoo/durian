package kr.co.pincoin.api.domain.shop.model.support.message;

import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.infra.shop.entity.support.message.NoticeMessageEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public class NoticeMessage {
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

    private Boolean isRemoved;

    @Builder
    private NoticeMessage(Long id,
                          String title,
                          String description,
                          String keywords,
                          String content,
                          Integer category,
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
        this.owner = owner;
        this.store = store;
        this.created = created;
        this.modified = modified;
        this.isRemoved = isRemoved;

        validateNotice();
    }

    public NoticeMessageEntity toEntity() {
        return NoticeMessageEntity.builder()
                .id(this.getId())
                .title(this.getTitle())
                .description(this.getDescription())
                .keywords(String.join(",", this.getKeywords())) // Set<String>을 comma-separated string으로 변환
                .content(this.getContent())
                .category(this.getCategory())
                .owner(this.getOwner().toEntity())
                .store(this.getStore().toEntity())
                .build();
    }

    public static NoticeMessage of(String title,
                                   String content,
                                   Integer category,
                                   User owner,
                                   Store store) {
        return NoticeMessage.builder()
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
    remove() {
        this.isRemoved = true;
    }

    public void
    restore() {
        this.isRemoved = false;
    }

    public boolean
    belongsToStore(Long storeId) {
        return this.store != null &&
                this.store.getId().equals(storeId);
    }

    public boolean
    hasKeyword(String keyword) {
        return this.keywords != null &&
                this.keywords.contains(keyword.toLowerCase());
    }

    public boolean
    matchesCategory(NoticeCategory category) {
        return this.category.equals(category.getValue());
    }

    public boolean
    isRecent() {
        return this.created.isAfter(
                LocalDateTime.now().minusDays(7));
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
    validateNotice() {
        validateTitle(this.title);
        validateContent(this.content);

        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (!NoticeCategory.isValid(category)) {
            throw new IllegalArgumentException("Invalid notice category");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        if (store == null) {
            throw new IllegalArgumentException("Store cannot be null");
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

    public enum NoticeCategory {
        GENERAL(1),
        SYSTEM(2),
        EVENT(3),
        UPDATE(4),
        MAINTENANCE(5),
        IMPORTANT(6);

        private final Integer value;

        NoticeCategory(Integer value) {
            this.value = value;
        }

        public static boolean isValid(Integer value) {
            for (NoticeCategory category : NoticeCategory.values()) {
                if (category.value.equals(value)) {
                    return true;
                }
            }
            return false;
        }

        public static NoticeCategory fromValue(Integer value) {
            for (NoticeCategory category : NoticeCategory.values()) {
                if (category.value.equals(value)) {
                    return category;
                }
            }
            throw new IllegalArgumentException("Invalid notice category value: " + value);
        }

        public Integer getValue() {
            return value;
        }
    }
}