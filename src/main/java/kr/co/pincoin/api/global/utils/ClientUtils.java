package kr.co.pincoin.api.global.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

/** 클라이언트 요청 정보를 추출하는 유틸리티 클래스 */
public class ClientUtils {
  private static final String USER_AGENT_HEADER = "User-Agent";

  private static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";

  /**
   * HttpServletRequest에서 클라이언트 정보를 추출
   *
   * @param request HTTP 요청
   * @return 클라이언트 정보를 포함한 ClientInfo 객체
   */
  public static ClientInfo getClientInfo(HttpServletRequest request) {
    return new ClientInfo(
        getOrDefault(request.getHeader(USER_AGENT_HEADER)),
        getOrDefault(request.getHeader(ACCEPT_LANGUAGE_HEADER)),
        IpUtils.getClientIp(request));
  }

  /** null 체크를 포함한 안전한 값 반환 */
  private static String getOrDefault(String value) {
    return value != null ? value : "";
  }

  /** 클라이언트 요청 정보를 담는 DTO 클래스 */
  @Getter
  public static class ClientInfo {
    private final String userAgent;
    private final String acceptLanguage;
    private final String ipAddress;

    private ClientInfo(String userAgent, String acceptLanguage, String ipAddress) {
      this.userAgent = userAgent;
      this.acceptLanguage = acceptLanguage;
      this.ipAddress = ipAddress;
    }
  }
}
