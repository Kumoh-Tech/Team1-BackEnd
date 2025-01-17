package com.club_board.club_board_server.repository.book;

import com.club_board.club_board_server.domain.book.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE b.id=:bookId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Book> findBookWithPessimisticLock(@Param("bookId") Long bookId);
}
