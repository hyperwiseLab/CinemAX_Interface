package com.cinemax.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private String studentNum;

    private String department;

    @Size(max = 500)
    private String selfIntroduction;

    private String tel;

    @Size(max = 500)
    private String profileImageUrl;
}


