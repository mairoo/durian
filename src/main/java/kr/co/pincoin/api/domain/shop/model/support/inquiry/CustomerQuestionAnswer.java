package kr.co.pincoin.api.domain.shop.model.support.inquiry;

import kr.co.pincoin.api.infra.shop.entity.support.inquiry.CustomerQuestionAnswerEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CustomerQuestionAnswer {
    private final Long id;
    private final CustomerQuestion question;
    private final LocalDateTime created;
    private final LocalDateTime modified;
    private String content;

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "instanceBuilder")
    private CustomerQuestionAnswer(String content, CustomerQuestion question) {
        this.id = null;
        this.content = content;
        this.question = question;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();

        validateAnswer();
    }

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "jpaBuilder")
    private CustomerQuestionAnswer(Long id, String content, CustomerQuestion question,
                                   LocalDateTime created, LocalDateTime modified) {
        this.id = id;
        this.content = content;
        this.question = question;
        this.created = created;
        this.modified = modified;

        validateAnswer();
    }

    public static CustomerQuestionAnswer of(String content, CustomerQuestion question) {
        return CustomerQuestionAnswer.instanceBuilder()
                .content(content)
                .question(question)
                .build();
    }

    public static CustomerQuestionAnswer from(CustomerQuestionAnswerEntity entity) {
        return CustomerQuestionAnswer.jpaBuilder()
                .id(entity.getId())
                .content(entity.getContent())
                .question(CustomerQuestion.from(entity.getQuestion()))
                .created(entity.getCreated())
                .modified(entity.getModified())
                .build();
    }

    public void updateContent(String content) {
        validateContent(content);
        this.content = content;
    }

    public boolean isFirstAnswer() {
        return this.question != null &&
                this.question.getAnswerCount() == 0;
    }

    public boolean belongsToQuestion(Long questionId) {
        return this.question != null &&
                this.question.getId().equals(questionId);
    }

    public boolean isRecent() {
        return this.created.isAfter(LocalDateTime.now().minusDays(1));
    }

    public int getContentLength() {
        return this.content != null ? this.content.length() : 0;
    }

    private void validateAnswer() {
        validateContent(this.content);

        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        if (question.isClosed()) {
            throw new IllegalStateException("Cannot answer closed question");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer content cannot be empty");
        }

        if (content.length() > 1000) {
            throw new IllegalArgumentException("Answer content cannot exceed 1000 characters");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerQuestionAnswer that = (CustomerQuestionAnswer) o;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        return created.equals(that.created) &&
                question.getId().equals(that.question.getId());
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return 31 * created.hashCode() + question.getId().hashCode();
    }
}