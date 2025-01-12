package com.club_board.club_board_server.domain.book;

import com.club_board.club_board_server.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "reservation_date", nullable = false)
    @CreatedDate
    private LocalDateTime reservationDate;

    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    public Reservation(User user, Book book) {
        this.user = user;
        this.book = book;
        this.status = ReservationStatus.RESERVED;
    }
}
