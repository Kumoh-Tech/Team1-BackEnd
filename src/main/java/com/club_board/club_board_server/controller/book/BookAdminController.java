package com.club_board.club_board_server.controller.book;

import com.club_board.club_board_server.domain.book.ReservationStatus;
import com.club_board.club_board_server.dto.bookAdmin.request.RegisterBookRequest;
import com.club_board.club_board_server.dto.bookAdmin.response.*;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.book.BookAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/reservations")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseBody<BookReservationResponse>> getReservations(Pageable pageable) {
        BookReservationResponse response = bookAdminService.getReservations(pageable);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    @GetMapping("/loans")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseBody<BookLoanResponse>> getLoans(Pageable pageable) {
        BookLoanResponse response = bookAdminService.getLoans(pageable);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    @GetMapping("/returns")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseBody<BookReturnResponse>> getReturns(
            @PageableDefault(size = 20, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        BookReturnResponse response = bookAdminService.getReturns(pageable);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    @PostMapping("/{reservationId}/accept")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseBody<Void>> approveBookLoan(@PathVariable Long reservationId) {
        bookAdminService.approveBookLoan(reservationId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

    @PostMapping("/{reservationId}/return")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseBody<Void>> completeBookReturn(@PathVariable Long reservationId) {
        bookAdminService.completeBookReturn(reservationId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

    @PostMapping("/{reservationId}/return/correct")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseBody<Void>> correctReturnStatus(
            @PathVariable Long reservationId,
            @RequestParam ReservationStatus status
    ) {
        bookAdminService.correctReturnStatus(reservationId, status);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }
}