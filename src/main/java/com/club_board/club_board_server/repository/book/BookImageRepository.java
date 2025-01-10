package com.club_board.club_board_server.repository.book;

import com.club_board.club_board_server.domain.book.BookImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookImageRepository extends JpaRepository<BookImage, Long> {
}
