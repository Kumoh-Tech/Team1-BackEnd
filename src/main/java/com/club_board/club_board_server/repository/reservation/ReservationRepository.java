package com.club_board.club_board_server.repository.reservation;
import com.club_board.club_board_server.domain.book.Book;
import com.club_board.club_board_server.domain.book.Reservation;
import com.club_board.club_board_server.dto.bookAdmin.response.BookLoan;
import com.club_board.club_board_server.dto.bookAdmin.response.BookReservation;
import com.club_board.club_board_server.dto.bookAdmin.response.BookReturn;
import com.club_board.club_board_server.dto.myPage.book.MyLoanResponse;
import com.club_board.club_board_server.dto.myPage.book.MyReservationResponse;
import com.club_board.club_board_server.dto.myPage.book.MyReturnResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /*
    예약 중or 대출 중 상태인 사람의 수를 구함 --> 대출자+예약자 포함 3명까지만 가능
     */
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.book.id = :bookId AND (r.status='RESERVED' OR r.status='BORROWING')")
    int countActiveReservation(@Param("bookId") Long bookId);

    @Query("SELECT r From Reservation r WHERE r.book.id=:bookId AND r.user.id=:userId")
    Optional<Reservation> findReservationByBookAndUserId(@Param("bookId") Long bookId, @Param("userId") Long userId);

    @Query("select new com.club_board.club_board_server.dto.myPage.book.MyLoanResponse(" +
            "b.id, b.title, r.borrowDate, r.returnDate, " +
            "CAST(function('DATEDIFF', current_date, function('ADDDATE', r.borrowDate, 14)) AS int)) " +
            "from Reservation r join r.book b " +
            "where r.user.id = :userId and (r.status = 'BORROWING' or r.status = 'OVERDUE')")
    List<MyLoanResponse> findUserLoans(@Param("userId") Long userId);

    @Query("select new com.club_board.club_board_server.dto.myPage.book.MyReservationResponse(" +
            "b.id, b.title, r.reservationDate) " +
            "from Reservation r join r.book b " +
            "where r.user.id = :userId and r.status = 'RESERVED'")
    List<MyReservationResponse> findUserReservations(@Param("userId") Long userId);

    @Query("select new com.club_board.club_board_server.dto.myPage.book.MyReturnResponse(" +
            "b.id, b.title, r.borrowDate, r.returnDate) " +
            "from Reservation r join r.book b " +
            "where r.user.id = :userId and (r.status = 'RETURNED' or r.status='OVERDUE_RETURNED')")
    List<MyReturnResponse> findUserReturns(@Param("userId") Long userId);

    @Query("SELECT r FROM Reservation r WHERE r.book.id = :bookId AND r.user.id=:userId AND r.status='RESERVED'")
    Optional<Reservation> findReservedReservationByBookAndUser(@Param("bookId") Long bookId, @Param("userId") Long userId);

    // 마감 기한이 지난 예약 내역을 리스트로 가져오는 JPQL
    @Query("SELECT r FROM Reservation r JOIN FETCH r.user WHERE r.status='BORROWING' AND r.borrowDate < :overdueDate")
    List<Reservation> findAllOverdueReservations(@Param("overdueDate") LocalDateTime overdueDate);

    @Query("SELECT new com.club_board.club_board_server.dto.bookAdmin.response.BookReservation(" +
            "b.id, b.title, r.id, u.id, u.name, r.reservationDate) " +
            "FROM Reservation r JOIN r.book b JOIN r.user u WHERE r.status='RESERVED'")
    Page<BookReservation> findAllReservations(Pageable pageable);

    @Query("SELECT new com.club_board.club_board_server.dto.bookAdmin.response.BookLoan(" +
            "b.id, b.title, r.id, u.id, u.name, r.borrowDate) " +
            "FROM Reservation r JOIN r.book b JOIN r.user u WHERE r.status='BORROWING' OR r.status='OVERDUE'")
    Page<BookLoan> findAllLoans(Pageable pageable);

    @Query("SELECT new com.club_board.club_board_server.dto.bookAdmin.response.BookReturn(" +
            "b.id, b.title, r.id, u.id, u.name, r.borrowDate, r.returnDate) " +
            "FROM Reservation r JOIN r.book b JOIN r.user u WHERE r.status='RETURNED' OR r.status='OVERDUE_RETURNED'")
    Page<BookReturn> findAllReturns(Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.book.id=:bookId AND (r.status='BORROWING' OR r.status='OVERDUE')")
    List<Reservation> findByBookAndBorrowingStatus(Long bookId);

    Optional<Reservation> findByBook(Book savedBook);
}
