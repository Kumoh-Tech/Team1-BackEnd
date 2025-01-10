package com.club_board.club_board_server.controller.book;
import com.club_board.club_board_server.dto.book.BookResponse;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;


    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<List<BookResponse>>> getAllBooks(){
        List<BookResponse> bookResponses=bookService.getAllBooks();
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(bookResponses));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<BookResponse>> getBookById(@PathVariable Long id){
        BookResponse bookResponse= bookService.getBookById(id);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(bookResponse));
    }

    @PostMapping("/reservation/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<String>> reservation(@PathVariable Long id){
        bookService.addReservation(id);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("Reservation success"));
    }

    @DeleteMapping("/reservation/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<ResponseBody<String>> cancelReservation(@PathVariable Long id){
        bookService.cancelReservation(id);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse("Reservation cancelled"));
    }

}
