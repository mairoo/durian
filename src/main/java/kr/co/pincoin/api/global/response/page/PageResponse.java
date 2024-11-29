package kr.co.pincoin.api.global.response.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
  private final List<T> content;

  private final int page;

  private final int size;

  private final long totalElements;

  private final int totalPages;

  private final boolean first;

  private final boolean last;

  public static <T> PageResponse<T> from(Page<T> page) {
    return PageResponse.<T>builder()
        .content(page.getContent())
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .first(page.isFirst())
        .last(page.isLast())
        .build();
  }
}
