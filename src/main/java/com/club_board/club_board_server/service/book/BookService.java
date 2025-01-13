package com.club_board.club_board_server.service.book;
import com.club_board.club_board_server.domain.book.Book;
import com.club_board.club_board_server.domain.book.BookStatus;
import com.club_board.club_board_server.domain.book.Reservation;
import com.club_board.club_board_server.domain.book.ReservationStatus;
import com.club_board.club_board_server.domain.user.User;
import com.club_board.club_board_server.dto.book.BookResponse;
import com.club_board.club_board_server.repository.book.BookRepository;
import com.club_board.club_board_server.repository.book.ReservationRepository;
import com.club_board.club_board_server.repository.user.UserRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;


    // 모든 책을 조회, 데이터가 많아질 시 추후 페이징 처리 필요해보임
    public List<BookResponse> getAllBooks(){
        List<Book> books=bookRepository.findAll();
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

    // 책 상세내역 조회
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


    public void addReservation(Long bookId,Long userId){

        // 예약하고자 하는 책 찾기
        Book book=bookRepository.findById(bookId)
                .orElseThrow(()->new BusinessException(ExceptionType.BOOK_NOT_FOUND));

        // 예약 수를 동적으로 계산하여 체크
        int currentReservationCount = reservationRepository.countActiveReservation(book.getId());
        if (currentReservationCount >= 3 || book.getStatus()==BookStatus.FULLY_RESERVED) {
            throw new BusinessException(ExceptionType.BOOK_ALREADY_FULL);
        }

        User user=userRepository.findById(userId);

        // 연체자인지 확인 (연체자는 예약 불가 정책 예시)
        if (user.isOverdue()) {
            throw new BusinessException(ExceptionType.USER_OVERDUE);
        }

        Reservation reservation=new Reservation(user,book);
        reservationRepository.save(reservation);

        // 예약 수가 3명이 되면 책 상태를 FULLY_RESERVED로 변경
        if (currentReservationCount + 1 >= 3) {
            book.setStatus(BookStatus.FULLY_RESERVED);
            bookRepository.save(book);
        }
    }

    // 예약 취소
    public void cancelReservation(Long bookId,Long userId){
        Book book=bookRepository.findById(bookId)
                .orElseThrow(()->new BusinessException(ExceptionType.BOOK_NOT_FOUND));

        Reservation reservation=reservationRepository.findByBookIdAndUserId(bookId,userId)
                .orElseThrow(()->new BusinessException(ExceptionType.RESERVATION_NOT_FOUND));
        reservationRepository.delete(reservation);

        if (book.getStatus() == BookStatus.FULLY_RESERVED) {
            book.setStatus(BookStatus.AVAILABLE);
            bookRepository.save(book);
        }
    }

    // 자정이 될 때마다 스케줄러를 통해 주기적으로 메서드 실행
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkUserIsOverdue(){
        LocalDate today=LocalDate.now();
        LocalDate overdueDate=today.minusDays(14);
        List<Reservation> overdueReservation=reservationRepository.findAllOverdueReservations(overdueDate);
        if (overdueReservation.isEmpty()) {
            return;
        }
        for(Reservation reservation:overdueReservation){
            reservation.setStatus(ReservationStatus.OVERDUE);
            reservation.getUser().setOverdue(true);
        }
    }
}
