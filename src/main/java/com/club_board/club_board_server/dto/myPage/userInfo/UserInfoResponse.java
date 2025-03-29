package com.club_board.club_board_server.dto.myPage.userInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class UserInfoResponse {
    private String role;
    private String username;
    private String name;
    private String studentId;
    private int grade;
    private String userDepartment;
    private String phoneNumber;
    private boolean departmentPublic;
    private boolean studentIdPublic;
    private boolean gradePublic;
    private boolean phonePublic;
    private List<String> departments;

    @Builder
    public UserInfoResponse(String role, String username, String name, String userDepartment, String studentId, int grade, String phoneNumber
    , boolean departmentPublic, boolean studentIdPublic, boolean gradePublic, boolean phonePublic,List<String> departments) {
        this.role = role;
        this.username = username;
        this.name=name;
        this.studentId=studentId;
        this.grade=grade;
        this.userDepartment= userDepartment;
        this.phoneNumber=phoneNumber;
        this.departmentPublic=departmentPublic;
        this.studentIdPublic=studentIdPublic;
        this.gradePublic=gradePublic;
        this.phonePublic=phonePublic;
        this.departments=departments;
    }
}
