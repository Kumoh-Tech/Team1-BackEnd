package com.club_board.club_board_server.dto.myPage.userInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class UserInfoResponse {
    private String role;
    private String username;
    private String name;
    private String department;
    private String studentId;
    private int grade;
    private List<String> departments;
    private String phoneNumber;
    private boolean departmentPublic;
    private boolean studentIdPublic;
    private boolean gradePublic;
    private boolean phonePublic;

    @Builder
    public UserInfoResponse(String role, String username, String name, String department, String studentId, int grade, List<String> departments, String phoneNumber
    , boolean departmentPublic, boolean studentIdPublic, boolean gradePublic, boolean phonePublic) {
        this.role = role;
        this.username = username;
        this.name=name;
        this.department=department;
        this.studentId=studentId;
        this.grade=grade;
        this.departments=departments;
        this.phoneNumber=phoneNumber;
        this.departmentPublic=departmentPublic;
        this.studentIdPublic=studentIdPublic;
        this.gradePublic=gradePublic;
        this.phonePublic=phonePublic;
    }
}
