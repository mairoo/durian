package kr.co.pincoin.api.external.auth.danal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/danal")
@RequiredArgsConstructor
@Slf4j
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
        // 인증 페이지 리다이렉트를 위한 정보 추가
        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("authUrl", "https://wauth.teledit.com/Danal/WebAuth/Web/Start.php");
        response.put("tid", result.get("TID"));

        return ResponseEntity.ok(response);
      } else {
        return ResponseEntity.badRequest().body(result);
      }
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/confirm")
  public ResponseEntity<?> confirmAuth(@RequestParam String tid) {
    Map<String, String> transR = new HashMap<>();

    transR.put("TXTYPE", "CONFIRM");
    transR.put("TID", tid);
    transR.put("CONFIRMOPTION", "0");
    transR.put("IDENOPTION", "0");

    try {
      Map<String, String> result = danalAuthService.callTrans(transR);

      if ("0000".equals(result.get("RETURNCODE"))) {
        return ResponseEntity.ok(result);
      } else {
        return ResponseEntity.badRequest().body(result);
      }
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(
          Map.of("error", e.getMessage())
      );
    }
  }

  @PostMapping("/callback")
  @CrossOrigin(origins = {"https://wauth.teledit.com", "https://uas.teledit.com", "null"},
      allowCredentials = "false",
      methods = RequestMethod.POST,
      maxAge = 1800)
  public ResponseEntity<?> handleCallback(@RequestParam String TID) {
    Map<String, String> transR = new HashMap<>();

    // CPCGI.jsp 참고하여 필수 파라미터 설정
    transR.put("TXTYPE", "CONFIRM");
    transR.put("TID", TID);
    transR.put("CONFIRMOPTION", "0");
    transR.put("IDENOPTION", "0");

    try {
      Map<String, String> result = danalAuthService.callTrans(transR);

      // result.CARRIER: 통신사
      // result.NAME: 이름
      // result.PHONE: 전화번호
      // result.IDEN: 생년월일
      // result.DI
      // result.CI
      // result.TID
      // result.ORDERID
      // result.RETURNCODE
      // result.RETURNMSG

      if ("0000".equals(result.get("RETURNCODE"))) {
        return ResponseEntity.ok(result);
      } else {
        log.error("다날휴대폰인증 실패 - Error: {}, Message: {}",
            result.get("RETURNCODE"),
            result.get("RETURNMSG"));
        return ResponseEntity.badRequest().body(result);
      }
    } catch (Exception e) {
      log.error("Danal Auth Callback Error", e);
      return ResponseEntity.internalServerError().body(
          Map.of("error", e.getMessage())
      );
    }
  }

  private String generateOrderId() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}
