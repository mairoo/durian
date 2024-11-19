package kr.co.pincoin.api.global.utils;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {
    public static String getClientIp(HttpServletRequest request) {
        String[] IP_HEADERS = {
                "CF-Connecting-IP", // Cloudflare
                "X-Real-IP", // Nginx
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_CLIENT_IP"
        };

        for (String header : IP_HEADERS) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                if (header.equals("X-Forwarded-For")) {
                    return ip.split(",")[0];
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }
}
