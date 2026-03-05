package com.cinemax.domain.user.dto;

import com.cinemax.global.enums.RoleType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 100, message = "이름은 100자 이하여야 합니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    private String password;

    @NotNull(message = "학번은 필수입니다.")
    private String studentNum;

    @NotBlank(message = "이메일 인증 코드는 필수입니다.")
    @Pattern(regexp = "^[0-9]{6}$", message = "이메일 인증 코드는 6자리 숫자여야 합니다.")
    private String emailVerificationCode;

    private RoleType role;

    @NotNull(message = "이용약관 동의는 필수입니다.")
    private Boolean termsAgreed;

    @NotNull(message = "개인정보처리방침 동의는 필수입니다.")
    private Boolean privacyAgreed;

    private Boolean marketingAgreed;

    @AssertTrue(message = "이용약관에 동의해야 합니다.")
    public boolean isTermsAgreedValid() {
        return Boolean.TRUE.equals(termsAgreed);
    }

    @AssertTrue(message = "개인정보처리방침에 동의해야 합니다.")
    public boolean isPrivacyAgreedValid() {
        return Boolean.TRUE.equals(privacyAgreed);
    }
}
