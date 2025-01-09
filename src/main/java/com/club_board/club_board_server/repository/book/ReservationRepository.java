package com.club_board.club_board_server.repository.book;

import com.club_board.club_board_server.domain.book.Reservation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Reservation r WHERE r.book.id=:bookId And r.user.id=:userId")
    Optional<Reservation> deleteReservation(@Param("bookId") Long bookId, @Param("userId") Long userId);

    @Query("SELECT r FROM Reservation  r WHERE r.reservationDate<:deadLine")
    Optional<Reservation> findAllByReservationDateBefore(@Param("deadLine") LocalDate deadLine);
}
