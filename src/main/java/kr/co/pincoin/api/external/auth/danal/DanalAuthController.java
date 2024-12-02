package kr.co.pincoin.api.external.auth.danal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/danal")
@RequiredArgsConstructor
public class DanalAuthController {

  private final DanalAuthService danalAuthService;

  @Value("${danal.cp-id}")
  private String cpId;

  @Value("${danal.cp-pwd}")
  private String cpPwd;

  @Value("${danal.cp-title}")
  private String cpTitle;

  @Value("${danal.target-url}")
  private String targetUrl;

  @PostMapping("/request")
  public ResponseEntity<?> requestAuth() {
    Map<String, String> transR = new HashMap<>();

    // 휴대폰 인증 시 고정값
    transR.put("TXTYPE", "ITEMSEND");
    transR.put("SERVICE", "UAS");
    transR.put("AUTHTYPE", "36");

    // 필수 정보
    transR.put("CPID", cpId);
    transR.put("CPPWD", cpPwd);
    transR.put("CPTITLE", cpTitle);
    transR.put("TARGETURL", targetUrl);

    // 선택 정보
    transR.put("ORDERID", generateOrderId());

    try {
      Map<String, String> result = danalAuthService.callTrans(transR);

      if ("0000".equals(result.get("RETURNCODE"))) {
        return ResponseEntity.ok(result);
      } else {
        return ResponseEntity.badRequest().body(result);
      }
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
    }
  }

  private String generateOrderId() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}
