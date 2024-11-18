package kr.co.pincoin.api.app.home.controller;

import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.of("ok");
    }
}
