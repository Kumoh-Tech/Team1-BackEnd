package com.club_board.club_board_server.service.myPage;
import com.club_board.club_board_server.dto.myPage.book.MyLoanResponse;
import com.club_board.club_board_server.dto.myPage.book.MyReservationResponse;
import com.club_board.club_board_server.dto.myPage.book.MyReturnResponse;
import com.club_board.club_board_server.repository.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyLoanService {

    private final ReservationRepository reservationRepository;
    public List<MyLoanResponse> getMyLoan(Long userId){
        return reservationRepository.findUserLoans(userId);
    }

    public List<MyReturnResponse> getMyReturn(Long userId){
        return reservationRepository.findUserReturns(userId);
    }

    public List<MyReservationResponse> getMyReservations(Long userId){
        return reservationRepository.findUserReservations(userId);
    }
}
