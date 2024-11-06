package com.club_board.club_board_server.dto.myPage;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MyPageResponse {
    private String name;
    private String department;
    private String studentId;
    private int grade;
    private String phoneNumber;

    @Builder
    public MyPageResponse(String name, String department, String studentId, int grade, String phoneNumber) {
        this.name=name;
        this.department=department;
        this.studentId=studentId;
        this.grade=grade;
        this.phoneNumber=phoneNumber;
    }
}
