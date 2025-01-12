package com.club_board.club_board_server.repository.book;

import com.club_board.club_board_server.domain.book.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
