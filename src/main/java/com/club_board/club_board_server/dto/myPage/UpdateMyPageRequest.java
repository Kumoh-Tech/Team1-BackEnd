package com.club_board.club_board_server.dto.myPage;
import lombok.Getter;

@Getter
public class UpdateMyPageRequest {
    private String name;
    private String department;
    private String studentId;
    private int grade;
    private String phoneNumber;
    private Boolean is_department_public;
    private Boolean is_grade_public;
    private Boolean is_phone_public;
}
