package com.club_board.club_board_server.repository.file;

import com.club_board.club_board_server.domain.file.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

}
