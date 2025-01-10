package com.club_board.club_board_server.repository.book;
import com.club_board.club_board_server.domain.book.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /*
    예약 중or 대출 중 상태인 사람의 수를 구함 --> 대출자+예약자 포함 3명까지만 가능
     */
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.book.id = :bookId AND (r.status='RESERVED' OR r.status='BORROWING')")
    int countActiveReservation(@Param("bookId") Long bookId);


    Optional<Reservation> findByBookIdAndUserId(Long bookId, Long userId);

    // 마감 기한이 지난 예약 내역을 리스트로 가져오는 JPQL
    @Query("SELECT r FROM Reservation r JOIN FETCH r.user WHERE r.status='BORROWING' AND r.borrowDate+14 < :today")
    List<Reservation> findAllOverdueReservations(@Param("deadLine") LocalDate today);

}

