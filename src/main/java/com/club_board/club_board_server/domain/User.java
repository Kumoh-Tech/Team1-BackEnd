package com.club_board.club_board_server.domain;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Entity
@Getter
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
    private boolean is_phone_public=false;
    private LocalDate registrationDate;
    private LocalDate lastLoginDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Accession> accessions = new HashSet<>();

    public void addAccession(Accession accession) {
        this.accessions.add(accession);
        accession.setUser(this);
    }

    @Builder
    public User(String username, String password, String name, String department, String student_id,int grade,String phoneNumber,LocalDate registrationDate) {
        this.username = username;
        this.password = password;
        this.name=name;
        this.department = department;
        this.student_id = student_id;
        this.grade = grade;
        this.phoneNumber = phoneNumber;
        this.registrationDate=registrationDate;

    }

    public void issuePassword(String password) {
        this.password = password;
    }
}