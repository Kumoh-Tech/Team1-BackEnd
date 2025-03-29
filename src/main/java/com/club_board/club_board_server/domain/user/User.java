package com.club_board.club_board_server.domain.user;
import com.club_board.club_board_server.dto.myPage.userInfo.UpdateUserCommand;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.time.LocalDate;
@NoArgsConstructor
@Entity
@Getter
@SQLDelete(sql="UPDATE user SET is_deleted=true  WHERE user_id=?")
@Where(clause = "is_deleted = false")
public class User {

    @Id @GeneratedValue
    @Column(name="user_id")
    private Long id;

    private String username;

    private String password;

    private String department;

    private String student_id;

    private int grade;

    private String name;

    private String phoneNumber;

    private boolean is_department_public=true;

    private boolean is_grade_public=true;

    private boolean is_student_public=false;

    private boolean is_phone_public=false;

    private LocalDate registrationDate;

    private LocalDate lastLoginDate;

    @Setter
    private String profileImageUrl;

    @Setter
    private boolean isOverdue;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean is_deleted=false;

    @Builder
    public User(String username, String password, String name, String department, String student_id, int grade, String phoneNumber, LocalDate registrationDate,Role role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.department = department;
        this.student_id = student_id;
        this.grade = grade;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
        this.role=role;
    }

    public void issuePassword(String password) {
        this.password = password;
    }

    public void updateUserInfo(UpdateUserCommand command) {
        this.password = command.getPassword();
        this.department = command.getDepartment();
        this.grade = command.getGrade();
        this.phoneNumber = command.getPhoneNumber();
        this.is_department_public = command.isDepartmentPublic();
        this.is_grade_public = command.isGradePublic();
        this.is_student_public = command.isStudentIdPublic();
        this.is_phone_public = command.isPhonePublic();
    }
}

