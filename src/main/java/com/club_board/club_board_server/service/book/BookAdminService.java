package com.club_board.club_board_server.service.book;

import com.club_board.club_board_server.domain.book.Book;
import com.club_board.club_board_server.domain.book.BookImage;
import com.club_board.club_board_server.domain.book.Reservation;
import com.club_board.club_board_server.domain.book.ReservationStatus;
import com.club_board.club_board_server.dto.bookAdmin.request.RegisterBookRequest;
import com.club_board.club_board_server.dto.bookAdmin.response.*;
import com.club_board.club_board_server.dto.pageable.PageInfo;
import com.club_board.club_board_server.repository.book.BookImageRepository;
import com.club_board.club_board_server.repository.book.BookRepository;
import com.club_board.club_board_server.repository.book.ReservationRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import com.club_board.club_board_server.service.file.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookAdminService {
    public static final int MAX_LOAN_PERIOD_DAYS = 14;

    private final BookRepository bookRepository;
    private final BookImageRepository bookImageRepository;
    private final S3Service s3Service;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void registerBook(RegisterBookRequest request) {
        Book newBook = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .publishYear(request.getPublishYear())
                .build();

        bookRepository.save(newBook);

        BookImage savedImage = bookImageRepository.findById(request.getFileId())
                .orElseThrow(() -> new BusinessException(ExceptionType.FILE_NOT_FOUND));

        savedImage.setBook(newBook);
    }


    @Transactional
    public void updateBook(Long bookId, RegisterBookRequest request) {
        Book savedBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(ExceptionType.BOOK_NOT_FOUND));

        BookImage savedBookImage = bookImageRepository.findById(request.getFileId())
                .orElseThrow(() -> new BusinessException(ExceptionType.FILE_NOT_FOUND));

        BeanUtils.copyProperties(request, savedBook, "fileId");

        if (savedBookImage.getBook() != savedBook) {
            bookImageRepository.findByBook(savedBook)
                    .ifPresent(s3Service::deleteBookImage);

            savedBookImage.setBook(savedBook);
        }
    }

    @Transactional
    public void deleteBook(Long bookId) {
        Book savedBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(ExceptionType.BOOK_NOT_FOUND));

        BookImage savedBookImage = bookImageRepository.findByBook(savedBook)
                .orElseThrow(() -> new BusinessException(ExceptionType.FILE_NOT_FOUND));

        reservationRepository.findByBook(savedBook).ifPresent(reservation -> {
            throw new BusinessException(ExceptionType.DELETE_RESTRICTED_BY_RESERVATIONS);
        });

        s3Service.deleteBookImage(savedBookImage);

        bookRepository.delete(savedBook);
    }

    @Transactional(readOnly = true)
    public BookReservationResponse getReservations(Pageable pageable) {
        Page<BookReservation> reservationPage = reservationRepository.findAllReservations(pageable);

        return BookReservationResponse.builder()
                .reservations(reservationPage.toList())
                .paging(PageInfo.from(pageable, reservationPage))
                .build();
    }

    @Transactional(readOnly = true)
    public BookLoanResponse getLoans(Pageable pageable) {
        Page<BookLoan> reservationPage = reservationRepository.findAllLoans(pageable);

        return BookLoanResponse.builder()
                .loans(reservationPage.toList())
                .paging(PageInfo.from(pageable, reservationPage))
                .build();
    }

    @Transactional(readOnly = true)
    public BookReturnResponse getReturns(Pageable pageable) {
        Page<BookReturn> reservationPage = reservationRepository.findAllReturns(pageable);

        return BookReturnResponse.builder()
                .returns(reservationPage.toList())
                .paging(PageInfo.from(pageable, reservationPage))
                .build();
    }

    @Transactional
    public void approveBookLoan(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ExceptionType.RESERVATION_NOT_FOUND));

        if (isReturned(reservation.getStatus())) {
            throw new BusinessException(ExceptionType.UNSUPPORTED_STATUS_CHANGE);
        }

        if (!reservationRepository.findByBookAndBorrowingStatus(reservation.getBook().getId()).isEmpty()) {
            throw new BusinessException(ExceptionType.BOOK_ALREADY_LOAN);
        }

        reservation.setStatus(ReservationStatus.BORROWING);
        reservation.setBorrowDate(LocalDateTime.now());
    }

    @Transactional
    public void completeBookReturn(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ExceptionType.RESERVATION_NOT_FOUND));

        reservation.setStatus(this.switchBorrowingToReturn(reservation.getStatus()));
        reservation.setReturnDate(LocalDateTime.now());
    }

    private ReservationStatus switchBorrowingToReturn(ReservationStatus status) {
        if (status == ReservationStatus.BORROWING) {
            return ReservationStatus.RETURNED;
        }

        if (status == ReservationStatus.OVERDUE) {
            return ReservationStatus.OVERDUE_RETURNED;
        }

        throw new BusinessException(ExceptionType.UNSUPPORTED_STATUS_CHANGE);
    }

    @Transactional
    public void correctReturnStatus(Long reservationId, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ExceptionType.RESERVATION_NOT_FOUND));
        if (!isReturned(status)) {
            throw new BusinessException(ExceptionType.UNSUPPORTED_STATUS_CHANGE);
        }
        reservation.setStatus(status);
    }

    private boolean isReturned(ReservationStatus status) {
        return status == ReservationStatus.RETURNED || status == ReservationStatus.OVERDUE_RETURNED;
    }
}
