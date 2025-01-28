package com.club_board.club_board_server.dto.bookAdmin.response;

import com.club_board.club_board_server.dto.pageable.PageInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BookReservationResponse {
    private List<BookReservation> reservations;
    private PageInfo paging;
}
