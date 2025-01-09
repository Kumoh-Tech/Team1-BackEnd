package com.club_board.club_board_server.service.book;

import com.club_board.club_board_server.domain.book.BookImage;
import com.club_board.club_board_server.repository.book.BookImageRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookImageService {
    private final BookImageRepository bookImageRepository;

    @Transactional
    public BookImage saveBookImage(String objectName) {
        return bookImageRepository.save(new BookImage(objectName));
    }

    public String getFileName(Long bookImageId) {
        return bookImageRepository.findById(bookImageId)
                .orElseThrow(() -> new BusinessException(ExceptionType.FILE_NOT_FOUND))
                .getUrl();
    }
}
