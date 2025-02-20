package com.club_board.club_board_server.dto.myPage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateMyPageRequest {

    @NotBlank
    private String prePassword;

    @NotBlank
    @Pattern(message = "비밀번호는 최소 10자 이상~20자 이하, 영문 대문자, 소문자, 특수문자를 포함해야 합니다.",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@!#$%^&*()_+])[A-Za-z\\d@!#$%^&*()_+]{10,20}$")
    private String newPassword;

    @NotBlank
    private String department;

    @NotBlank
    private int grade;

    @NotBlank
    @Pattern(message = "전화번호 형식이 올바르지 않습니다.",
            regexp = "^\\d{3}-\\d{4}-\\d{4}$")
    private String phoneNumber;

    @NotBlank
    private Boolean departmentPublic;

    @NotBlank
    private Boolean studentIdPublic;

    @NotBlank
    private Boolean gradePublic;

    @NotBlank
    private Boolean phonePublic;
}
