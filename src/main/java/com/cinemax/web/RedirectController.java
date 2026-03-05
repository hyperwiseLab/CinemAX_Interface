package com.cinemax.web;

import com.cinemax.core.controller.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * QR 스캔 등으로 백엔드로 들어온 /api/v1/join/{code} 요청을
 * 프론트엔드 라우트(/join/{code})로 302 리다이렉트.
 */
@RestController
@RequestMapping("/join")
@Tag(name = "Redirect", description = "FRONT redirect endpoints")
public class RedirectController extends BaseController {

    @GetMapping("/{code}")
    @Operation(summary = "Join 코드 리다이렉트", description = "join 코드를 프론트엔드 경로로 리다이렉트합니다.")
    public ResponseEntity<Void> redirectJoin(@PathVariable("code") String code,
                                             @RequestParam(name = "frontend", required = false, defaultValue = "http://localhost:3001") String frontendBaseUrl) {
        String target = frontendBaseUrl.replaceAll("/$", "") + "/join/" + code;
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, target)
                .build();
    }
}


