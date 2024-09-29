package com.club_board.club_board_server.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;


@Getter
public class UserRegisterRequest {

    @NotNull
    @Pattern(message="올바르지 않는 아이디 형식입니다.",
            regexp = "/^A-Za-z0-9*@kumoh\\.ac\\.kr$/")
    private String username;

    @NotNull
    @Pattern(message = "비밀번호는 최소 10자 이상~20자 이하, 영문 대문자, 소문자, 특수문자를 포함해야합니다.",
           regexp = " /^(?=.[a-z])(?=.[A-Z])(?=.\\d)(?=.[\\W_])[A-Za-z\\d\\W_]{10,20}$/")
    private String password;

    @NotNull
    @Pattern(message = "비밀번호는 최소 10자 이상~20자 이하, 영문 대문자, 소문자, 특수문자를 포함해야합니다.",
            regexp = "/^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@!#$%^&*()_+])[A-Za-z\\d@!#$%^&*()_+]{10,20}$/")
    private String confirmPassword;

    @NotBlank
    private String name;

    @NotBlank
    private String department;

    @NotBlank
    @Pattern(regexp = "/^\\d{8}$/")
    private String student_id;

    @NotNull
    private int grade;

    @NotBlank
    @Pattern(regexp = "/^\\d{3}-\\d{4}-\\d{4}$/")
    private String phone_number;
}


