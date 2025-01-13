package com.club_board.club_board_server.service.book;

import com.club_board.club_board_server.domain.book.Book;
import com.club_board.club_board_server.domain.book.BookImage;
import com.club_board.club_board_server.dto.bookAdmin.request.RegisterBookRequest;
import com.club_board.club_board_server.repository.book.BookImageRepository;
import com.club_board.club_board_server.repository.book.BookRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import com.club_board.club_board_server.service.file.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookAdminService {
    private final BookRepository bookRepository;
    private final BookImageRepository bookImageRepository;
    private final S3Service s3Service;

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
}
