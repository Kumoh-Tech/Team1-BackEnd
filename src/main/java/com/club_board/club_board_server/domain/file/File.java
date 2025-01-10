package com.club_board.club_board_server.domain.file;

import com.club_board.club_board_server.domain.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
public class File {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name = "url", unique = true, nullable = false)
    private String url;

    @Setter
    @Column(name = "is_main", nullable = false)
    private Boolean isMain;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public File(String fileUrl) {
        this.url = fileUrl;
        this.isMain = false;
    }
}
