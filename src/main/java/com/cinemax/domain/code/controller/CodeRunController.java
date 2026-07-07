package com.cinemax.domain.code.controller;

import com.cinemax.core.piston.client.PistonClient;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 코드 실행 API (Piston 프록시)
 *
 * <p>브라우저가 셀프호스팅 Piston을 직접 호출하면 CORS로 차단되므로,
 * 백엔드가 same-origin 프록시 역할을 한다. 요청/응답은 Piston 원본
 * 포맷을 그대로 전달하여 프론트엔드 변경을 최소화한다.
 */
@Slf4j
@RestController
@RequestMapping("/code")
@RequiredArgsConstructor
@Tag(name = "Code Run", description = "코드 실행 API (Piston 프록시)")
public class CodeRunController {

    private final PistonClient pistonClient;

    // 코드 실행 (Piston /api/v2/execute 프록시)
    @PostMapping("/run")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "코드 실행", description = "Piston 코드 실행 엔진에 코드 실행을 요청하고 결과를 반환합니다.")
    public ResponseEntity<JsonNode> run(@RequestBody JsonNode request) {
        JsonNode result = pistonClient.execute(request);
        return ResponseEntity.ok(result);
    }

    // 설치된 런타임(언어) 목록 조회
    @GetMapping("/runtimes")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(summary = "런타임 목록 조회", description = "Piston에 설치된 실행 가능 언어/버전 목록을 반환합니다.")
    public ResponseEntity<JsonNode> runtimes() {
        JsonNode result = pistonClient.runtimes();
        return ResponseEntity.ok(result);
    }
}
