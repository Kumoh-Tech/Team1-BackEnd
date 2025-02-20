package com.club_board.club_board_server.dto.myPage;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MyPageResponse {
    //TODO: 역할을 DTO로 전달 OR 토큰으로 전달?
    private String username;
    private String name;
    private String department;
    private String studentId;
    private int grade;
    private String phoneNumber;
    private boolean departmentPublic;
    private boolean studentIdPublic;
    private boolean gradePublic;
    private boolean phonePublic;

    @Builder
    public MyPageResponse(String username,String name, String department, String studentId, int grade, String phoneNumber
    , boolean departmentPublic, boolean studentIdPublic, boolean gradePublic, boolean phonePublic) {
        this.username = username;
        this.name=name;
        this.department=department;
        this.studentId=studentId;
        this.grade=grade;
        this.phoneNumber=phoneNumber;
        this.departmentPublic=departmentPublic;
        this.studentIdPublic=studentIdPublic;
        this.gradePublic=gradePublic;
        this.phonePublic=phonePublic;
    }
}
