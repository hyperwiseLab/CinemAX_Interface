package com.cinemax.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthEventType {
    
    // 로그인 관련 (1000번대)
    LOGIN_SUCCESS(1001, "로그인 성공"),
    LOGIN_FAILED(1002, "로그인 실패"),
    LOGOUT(1003, "로그아웃"),
    
    // 회원가입 관련 (2000번대)
    SIGNUP_SUCCESS(2001, "회원가입 성공"),
    SIGNUP_FAILED(2002, "회원가입 실패"),
    
    // 비밀번호 관련 (3000번대)
    PASSWORD_CHANGE_SUCCESS(3001, "비밀번호 변경 성공"),
    PASSWORD_CHANGE_FAILED(3002, "비밀번호 변경 실패"),
    PASSWORD_RESET_REQUEST(3003, "비밀번호 재설정 요청"),
    PASSWORD_RESET_SUCCESS(3004, "비밀번호 재설정 성공"),
    PASSWORD_RESET_FAILED(3005, "비밀번호 재설정 실패"),
    PASSWORD_RESET_REQUESTED(3006, "비밀번호 재설정 요청됨"),
    PASSWORD_CHANGED(3007, "비밀번호 변경됨"),
    PASSWORD_RESET_CODE_RESENT(3008, "비밀번호 재설정 코드 재발송"),
    PASSWORD_RESET_CANCELLED(3009, "비밀번호 재설정 취소됨"),
    
    // 이메일 인증 관련 (4000번대)
    EMAIL_VERIFICATION_SENT(4001, "이메일 인증 메일 발송"),
    EMAIL_VERIFICATION_SUCCESS(4002, "이메일 인증 성공"),
    EMAIL_VERIFICATION_FAILED(4003, "이메일 인증 실패"),
    EMAIL_VERIFICATION_EXPIRED(4004, "이메일 인증 만료"),
    
    // 토큰 관련 (5000번대)
    TOKEN_REFRESH_SUCCESS(5001, "토큰 갱신 성공"),
    TOKEN_REFRESH_FAILED(5002, "토큰 갱신 실패"),
    TOKEN_EXPIRED(5003, "토큰 만료"),
    TOKEN_INVALID(5004, "유효하지 않은 토큰"),
    
    // 계정 관련 (6000번대)
    ACCOUNT_LOCKED(6001, "계정 잠금"),
    ACCOUNT_UNLOCKED(6002, "계정 잠금 해제"),
    ACCOUNT_DISABLED(6003, "계정 비활성화"),
    ACCOUNT_ENABLED(6004, "계정 활성화"),
    
    // 보안 관련 (7000번대)
    SUSPICIOUS_ACTIVITY(7001, "의심스러운 활동 감지"),
    MULTIPLE_FAILED_LOGIN(7002, "다중 로그인 실패"),
    IP_BLOCKED(7003, "IP 차단"),
    IP_UNBLOCKED(7004, "IP 차단 해제");

    private final Integer code;
    private final String description;

    /**
     * 코드로 AuthEventType 찾기
     */
    public static AuthEventType fromCode(Integer code) {
        for (AuthEventType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown auth event type code: " + code);
    }

    /**
     * 코드로 AuthEventType 찾기 (null 안전)
     */
    public static AuthEventType fromCodeOrNull(Integer code) {
        if (code == null) {
            return null;
        }
        try {
            return fromCode(code);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
