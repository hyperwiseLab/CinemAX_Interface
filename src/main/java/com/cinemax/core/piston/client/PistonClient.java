package com.cinemax.core.piston.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Piston(코드 실행 엔진) API 호출 클라이언트
 *
 * <p>공개 Piston API(emkc.org)가 화이트리스트 전용으로 전환되어,
 * 자체 셀프호스팅 Piston 인스턴스를 호출한다. 브라우저에서 직접 호출 시
 * CORS 문제가 발생하므로, 백엔드가 프록시하여 same-origin으로 처리한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PistonClient {

    private final WebClient pistonWebClient;

    /**
     * Piston에 코드 실행을 요청한다.
     * 요청/응답 body는 Piston의 원본 포맷을 그대로 전달(pass-through)한다.
     *
     * @param request Piston 실행 요청 (language, version, files[], stdin ...)
     * @return Piston 실행 결과 (run.stdout, run.stderr, run.code ...)
     */
    public JsonNode execute(JsonNode request) {
        return pistonWebClient.post()
                .uri("/api/v2/execute")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    /**
     * 설치된 런타임(언어) 목록을 조회한다.
     */
    public JsonNode runtimes() {
        return pistonWebClient.get()
                .uri("/api/v2/runtimes")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
