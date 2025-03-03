package com.club_board.club_board_server.dto.myPage.userInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateUserCommand {

    private final String password;
    private final String department;
    private final int grade;
    private final String phoneNumber;
    private final boolean departmentPublic;
    private final boolean gradePublic;
    private final boolean studentIdPublic;
    private final boolean phonePublic;

    @Builder
    public UpdateUserCommand(String password, String department, int grade, String phoneNumber,
                             boolean departmentPublic, boolean gradePublic, boolean studentIdPublic, boolean phonePublic) {
        this.password = password;
        this.department = department;
        this.grade = grade;
        this.phoneNumber = phoneNumber;
        this.departmentPublic = departmentPublic;
        this.gradePublic = gradePublic;
        this.studentIdPublic = studentIdPublic;
        this.phonePublic = phonePublic;
    }
}
