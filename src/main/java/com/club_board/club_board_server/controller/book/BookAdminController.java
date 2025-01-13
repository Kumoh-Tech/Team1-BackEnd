package com.club_board.club_board_server.controller.book;

import com.club_board.club_board_server.dto.bookAdmin.request.RegisterBookRequest;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.book.BookAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/book/admin")
public class BookAdminController {
    private final BookAdminService bookAdminService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseBody<Void>> registerBook(@Valid @RequestBody RegisterBookRequest request) {
        bookAdminService.registerBook(request);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseBody<Void>> updateBook(
            @RequestParam Long bookId,
            @Valid @RequestBody RegisterBookRequest request
    ) {
        bookAdminService.updateBook(bookId, request);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseBody<Void>> deleteBook(@RequestParam Long bookId) {
        bookAdminService.deleteBook(bookId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

}