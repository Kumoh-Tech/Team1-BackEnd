package com.club_board.club_board_server.service.book;

import com.club_board.club_board_server.domain.book.Book;
import com.club_board.club_board_server.domain.book.BookImage;
import com.club_board.club_board_server.domain.book.Reservation;
import com.club_board.club_board_server.domain.book.ReservationStatus;
import com.club_board.club_board_server.dto.bookAdmin.request.RegisterBookRequest;
import com.club_board.club_board_server.dto.bookAdmin.response.BookLoan;
import com.club_board.club_board_server.dto.bookAdmin.response.BookReservation;
import com.club_board.club_board_server.dto.bookAdmin.response.BookReturn;
import com.club_board.club_board_server.repository.book.BookImageRepository;
import com.club_board.club_board_server.repository.book.BookRepository;
import com.club_board.club_board_server.repository.book.ReservationRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import com.club_board.club_board_server.service.file.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookAdminService {
    private static final int MAX_LOAN_PERIOD_DAYS = 14;

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

        s3Service.deleteBookImage(savedBookImage);

        bookRepository.delete(savedBook);
    }

    @Transactional(readOnly = true)
    public List<BookReservation> getReservations() {
        return reservationRepository.findAllByStatusOrderByReservationDateAsc()
                .stream()
                .map(reservation -> BookReservation.builder()
                        .bookId(reservation.getBook().getId())
                        .bookTitle(reservation.getBook().getTitle())
                        .reservationId(reservation.getId())
                        .userId(reservation.getUser().getId())
                        .userName(reservation.getUser().getName())
                        .reservationDate(reservation.getReservationDate())
                        .build()
                ).toList();
    }

    @Transactional(readOnly = true)
    public List<BookLoan> getLoans() {
        return reservationRepository.findAllByBorrowingStatusOrderByBorrowDateAsc()
                .stream()
                .map(reservation -> BookLoan.builder()
                        .bookId(reservation.getBook().getId())
                        .bookTitle(reservation.getBook().getTitle())
                        .reservationId(reservation.getId())
                        .userId(reservation.getUser().getId())
                        .userName(reservation.getUser().getName())
                        .borrowDate(reservation.getBorrowDate())
                        .returnDueDate(reservation.getBorrowDate().plusDays(MAX_LOAN_PERIOD_DAYS))
                        .build()
                ).toList();
    }

    @Transactional(readOnly = true)
    public List<BookReturn> getReturns() {
        return reservationRepository.findAllByReturnedStatusOrderByReturnDateAsc()
                .stream()
                .map(reservation -> BookReturn.builder()
                        .bookId(reservation.getBook().getId())
                        .bookTitle(reservation.getBook().getTitle())
                        .reservationId(reservation.getId())
                        .userId(reservation.getUser().getId())
                        .userName(reservation.getUser().getName())
                        .borrowDate(reservation.getBorrowDate())
                        .returnDate(reservation.getReturnDate())
                        .build()
                ).toList();
    }
}
