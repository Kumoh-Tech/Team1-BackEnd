package com.club_board.club_board_server.domain.file;

import com.club_board.club_board_server.domain.post.Post;
import jakarta.persistence.*;

@Entity
public class File {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name = "file_name", unique = true)
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public File(String fileName) {
        this.fileName = fileName;
    }
}
