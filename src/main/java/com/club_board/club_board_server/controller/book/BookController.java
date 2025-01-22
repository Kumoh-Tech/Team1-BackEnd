package com.club_board.club_board_server.controller.book;
import com.club_board.club_board_server.domain.user.CustomUserDetails;
import com.club_board.club_board_server.dto.book.BookResponse;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.book.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/books")
@Slf4j
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<List<BookResponse>>> getAllBooks(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId=customUserDetails.getUser().getId();
        List<BookResponse> bookResponses=bookService.getAllBooks(userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(bookResponses));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<BookResponse>> getBookById(@PathVariable("id") Long id,
                                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId=customUserDetails.getUser().getId();
        BookResponse bookResponse= bookService.getBookById(id,userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(bookResponse));
    }

    @PostMapping("/reservation/{bookId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<String>> reservation(@PathVariable("bookId") Long bookId,
                                                            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        log.info("유저 찾기");
        Long userId=customUserDetails.getUser().getId();
        bookService.addReservation(bookId,userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("Reservation success"));
    }

    @DeleteMapping("/reservation/{bookId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<String>> cancelReservation(@PathVariable("bookId") Long bookId,
                                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId=customUserDetails.getUser().getId();
        bookService.cancelReservation(bookId,userId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("Reservation cancelled"));
    }

}
