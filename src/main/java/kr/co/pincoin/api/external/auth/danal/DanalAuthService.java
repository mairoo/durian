package kr.co.pincoin.api.external.auth.danal;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import kr.co.danal.jsinbi.HttpClient;
import org.springframework.stereotype.Service;

@Service
public class DanalAuthService {

  private static final String DN_SERVICE_URL = "https://uas.teledit.com/uas/";
  private static final int DN_CONNECT_TIMEOUT = 5000;
  private static final int DN_TIMEOUT = 30000;
  private static final String CHARSET = "EUC-KR";

  public Map<String, String> callTrans(Map<String, String> data) {
    String reqStr = data2str(data);
    String resStr = "";

    HttpClient httpClient = new HttpClient();
    httpClient.setConnectionTimeout(DN_CONNECT_TIMEOUT);
    httpClient.setTimeout(DN_TIMEOUT);

    try {
      int status = httpClient.retrieve("POST", DN_SERVICE_URL, reqStr, CHARSET, CHARSET);

      if (status != 0) {
        resStr = "RETURNCODE=-1&RETURNMSG=NETWORK ERROR(" + status + ")";
      } else {
        resStr = httpClient.getResponseBody();
      }
    } catch (Exception e) {
      resStr = "RETURNCODE=-1&RETURNMSG=" + e.getMessage();
    }

    return str2data(resStr);
  }

  private Map<String, String> str2data(String str) {
    Map<String, String> map = new HashMap<>();
    StringTokenizer st = new StringTokenizer(str, "&");

    while (st.hasMoreTokens()) {
      String pair = st.nextToken();
      int index = pair.indexOf('=');

      if (index > 0) {
        map.put(pair.substring(0, index).trim(), pair.substring(index + 1));
      }
    }

    return map;
  }

  private String data2str(Map<String, String> data) {
    StringBuilder sb = new StringBuilder();

    for (Map.Entry<String, String> entry : data.entrySet()) {
      sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
    }

    if (!sb.isEmpty()) {
      return sb.substring(0, sb.length() - 1);
    }

    return "";
  }
}
