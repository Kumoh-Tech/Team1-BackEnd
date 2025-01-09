package com.club_board.club_board_server.service.book;

import com.club_board.club_board_server.domain.book.Book;
import com.club_board.club_board_server.domain.book.BookStatus;
import com.club_board.club_board_server.domain.book.Reservation;
import com.club_board.club_board_server.domain.book.ReservationStatus;
import com.club_board.club_board_server.domain.user.CustomUserDetails;
import com.club_board.club_board_server.domain.user.User;
import com.club_board.club_board_server.dto.book.BookResponse;
import com.club_board.club_board_server.repository.book.BookRepository;
import com.club_board.club_board_server.repository.book.ReservationRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import com.club_board.club_board_server.service.auth.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final ReservationRepository reservationRepository;

    public List<BookResponse> getAllBooks(){
        List<Book> books=bookRepository.findAll();
        log.info("조회 성공");
        return books.stream()
                .map(book->BookResponse.builder()
                        .id(book.getId())
                        .author(book.getAuthor())
                        .title(book.getTitle())
                        .publishYear(book.getPublishYear())
                        .publisher(book.getPublisher())
                        .status(book.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    public BookResponse getBookById(Long id){
        Book book=bookRepository.findById(id)
                .orElseThrow(()->new BusinessException(ExceptionType.BOOK_NOT_FOUND));
        return BookResponse.builder()
                .id(book.getId())
                .author(book.getAuthor())
                .title(book.getTitle())
                .publishYear(book.getPublishYear())
                .publisher(book.getPublisher())
                .status(book.getStatus())
                .build();
    }

    public void addReservation(Long id){

        // 예약하고자 하는 책 찾기
        Book book=bookRepository.findById(id)
                .orElseThrow(()->new BusinessException(ExceptionType.BOOK_NOT_FOUND));
        // 예약 인원 가득찼는지 확인
        if(book.getReservationCount()>=3){
            throw new BusinessException(ExceptionType.BOOK_ALREADY_FULL);
        }
        // 예약하는 사람 정보 가져오기
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User user = (User) customUserDetailsService.loadUserByUsername(username);
        // 예약이 가능할 때
        if(book.getStatus()!= BookStatus.FULLY_RESERVED&&!user.isOverdue()){
            Reservation reservation=new Reservation(user,book);
            reservationRepository.save(reservation);
        }
        //TODO 예약이 3명이 되어있으면 예약 못하게 서버에서 예외처리 + 프론트에 전달해줘야함
        // 예약을 누르려는 사람이 해당 학기에 연체 이력이 있을 경우 예약이 불가
        // 예약시작한 시점부터 2주라는 기간이 지나서 연체가 되면, 어떻게 자동으로 연체됨이라고 하는거지?
    }

    public void removeReservation(Long id){
        Book book=bookRepository.findById(id).orElse(null);
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails=(CustomUserDetails) authentication.getPrincipal();
        reservationRepository.deleteReservation(id,userDetails.getUser().getId())
                .orElseThrow(()->new BusinessException(ExceptionType.RESERVATION_NOT_FOUND));
    }



}
