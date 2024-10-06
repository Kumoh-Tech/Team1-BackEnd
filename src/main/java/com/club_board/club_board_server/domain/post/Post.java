package com.club_board.club_board_server.domain.post;

import com.club_board.club_board_server.domain.AuditEntity;
import com.club_board.club_board_server.domain.User;
import com.club_board.club_board_server.domain.file.File;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Post extends AuditEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "post_title")
    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @OneToMany
    @Column(name = "post_file_names")
    private List<File> fileNames;
}
