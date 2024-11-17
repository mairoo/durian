package kr.co.pincoin.api.domain.shop.model.support.review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TestimonialAnswer {
    private final Long id;
    private final Testimonial testimonial;
    private final LocalDateTime created;
    private final LocalDateTime modified;
    private String content;

    @Builder
    private TestimonialAnswer(Long id,
                              String content,
                              Testimonial testimonial,
                              LocalDateTime created,
                              LocalDateTime modified) {
        this.id = id;
        this.content = content;
        this.testimonial = testimonial;
        this.created = created;
        this.modified = modified;

        validateAnswer();
    }

    public static TestimonialAnswer of(String content,
                                       Testimonial testimonial) {
        return TestimonialAnswer.builder()
                .content(content)
                .testimonial(testimonial)
                .build();
    }

    public void updateContent(String content) {
        validateContent(content);
        this.content = content;
    }

    public boolean isFirstAnswer() {
        return this.testimonial != null &&
                this.testimonial.getAnswerCount() == 0;
    }

    public boolean belongsToTestimonial(Long testimonialId) {
        return this.testimonial != null &&
                this.testimonial.getId().equals(testimonialId);
    }

    public boolean isRecent() {
        return this.created.isAfter(
                LocalDateTime.now().minusHours(24));
    }

    public int getContentLength() {
        return this.content != null ? this.content.length() : 0;
    }

    public boolean isModified() {
        return !this.created.equals(this.modified);
    }

    public boolean isByStore() {
        return this.testimonial != null &&
                this.testimonial.getStore() != null;
    }

    private void validateAnswer() {
        validateContent(this.content);

        if (testimonial == null) {
            throw new IllegalArgumentException("Testimonial cannot be null");
        }

        if (testimonial.isHidden()) {
            throw new IllegalStateException("Cannot answer hidden testimonial");
        }

        if (testimonial.getIsRemoved()) {
            throw new IllegalStateException("Cannot answer removed testimonial");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer content cannot be empty");
        }

        if (content.length() > 1000) {
            throw new IllegalArgumentException(
                    "Answer content cannot exceed 1000 characters");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestimonialAnswer that = (TestimonialAnswer) o;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        return created.equals(that.created) &&
                testimonial.getId().equals(that.testimonial.getId());
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return 31 * created.hashCode() + testimonial.getId().hashCode();
    }
}
