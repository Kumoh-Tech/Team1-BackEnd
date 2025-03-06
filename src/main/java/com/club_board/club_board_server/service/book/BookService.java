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
import com.club_board.club_board_server.service.file.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;


    // 모든 책을 조회, 데이터가 많아질 시 추후 페이징 처리 필요해보임
    public List<BookResponse> getAllBooks(Long userId){
        List<Book> books=bookRepository.findAll();
        return books.stream()
                .map(book->{
                            String bookUrl=null;
                            if(book.getBookImage()!=null){
                                bookUrl=s3Service.generateBookImageDownloadUrl(book.getBookImage().getId()).getUrl();
                            }
                            int borrowCount=reservationRepository.countActiveReservation(book.getId());
                            ReservationStatus userReservationStatus=checkUserBookStatus(book.getId(),userId);
                return   BookResponse.builder()
                        .id(book.getId())
                        .author(book.getAuthor())
                        .title(book.getTitle())
                        .publishYear(book.getPublishYear())
                        .publisher(book.getPublisher())
                        .status(book.getStatus())
                        .bookImageId(book.getBookImage().getId())
                        .bookUrl(bookUrl)
                        .borrowCount(borrowCount)
                        .reservationStatus(userReservationStatus)
                        .build();
                })
                .collect(Collectors.toList());
    }

    // 책 상세내역 조회
    public BookResponse getBookById(Long bookId,Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(ExceptionType.BOOK_NOT_FOUND));
        String bookUrl = null;
        if (book.getBookImage() != null) {
            bookUrl = s3Service.generateBookImageDownloadUrl(book.getBookImage().getId()).getUrl();
        }
        int borrowCount = reservationRepository.countActiveReservation(book.getId());
        ReservationStatus userReservationStatus=checkUserBookStatus(bookId,userId);
            return BookResponse.builder()
                    .id(book.getId())
                    .author(book.getAuthor())
                    .title(book.getTitle())
                    .publishYear(book.getPublishYear())
                    .publisher(book.getPublisher())
                    .status(book.getStatus())
                    .bookImageId(book.getBookImage().getId())
                    .bookUrl(bookUrl)
                    .borrowCount(borrowCount)
                    .reservationStatus(userReservationStatus)
                    .build();
    }

    @Transactional
    public void addReservation(Long bookId,Long userId){
        // 예약하고자 하는 책 찾기
        Book book=bookRepository.findBookWithPessimisticLock(bookId)
                .orElseThrow(()->new BusinessException(ExceptionType.BOOK_NOT_FOUND));

        // 예약 수를 동적으로 계산하여 체크
        int currentReservationCount = reservationRepository.countActiveReservation(book.getId());
        if (currentReservationCount >= 3 || book.getStatus()==BookStatus.FULLY_RESERVED) {
            throw new BusinessException(ExceptionType.BOOK_ALREADY_FULL);
        }

        User user=userRepository.findById(userId)
                .orElseThrow(()->new BusinessException(ExceptionType.USER_NOT_FOUND));

        // 연체자인지 확인 (연체자는 예약 불가 정책 예시)
        if (user.isOverdue()) {
            throw new BusinessException(ExceptionType.USER_OVERDUE);
        }
        // 유저가 이미 책을 대여중인지 확인
        if(reservationRepository.findReservationByBookAndUserId(bookId, userId).isPresent()){
            throw new BusinessException(ExceptionType.YOU_ALREADY_RESERVATION);
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
    @Transactional
    public void cancelReservation(Long bookId,Long userId){
        Book book=bookRepository.findById(bookId)
                .orElseThrow(()->new BusinessException(ExceptionType.BOOK_NOT_FOUND));
        Reservation reservation=reservationRepository.findReservedReservationByBookAndUser(bookId,userId)
                .orElseThrow(()->new BusinessException(ExceptionType.RESERVATION_NOT_FOUND));
        reservationRepository.delete(reservation);

        if (book.getStatus() == BookStatus.FULLY_RESERVED && reservationRepository.countActiveReservation(book.getId())-1<3) {
            book.setStatus(BookStatus.AVAILABLE);
        }
    }

    // 자정이 될 때마다 스케줄러를 통해 주기적으로 메서드 실행
    //TODO
    // Batch Update 적용 고려
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkUserIsOverdue(){
        LocalDateTime today=LocalDateTime.now();
        LocalDateTime overdueDate=today.minusDays(14);
        List<Reservation> overdueReservation=reservationRepository.findAllOverdueReservations(overdueDate);
        if (overdueReservation.isEmpty()) {
            return;
        }
        for(Reservation reservation:overdueReservation){
            reservation.setStatus(ReservationStatus.OVERDUE);
            reservation.getUser().setOverdue(true);
            reservationRepository.save(reservation);
        }
    }
    public ReservationStatus checkUserBookStatus(Long id,Long userId){
        boolean isReserved = reservationRepository.findReservedReservationByBookAndUser(id, userId).isPresent();
        boolean isBorrowed = reservationRepository.findReservedReservationByBookAndUser(id, userId).isPresent();
        ReservationStatus userReservationStatus=null;
        if(isReserved)
            userReservationStatus=ReservationStatus.RESERVED;
        if(isBorrowed)
            userReservationStatus=ReservationStatus.BORROWING;
        return userReservationStatus;
    }
}
