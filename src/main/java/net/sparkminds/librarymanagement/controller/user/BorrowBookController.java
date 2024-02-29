package net.sparkminds.librarymanagement.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.request.BorrowBookDto;
import net.sparkminds.librarymanagement.payload.response.BookResponse;
import net.sparkminds.librarymanagement.service.BorrowBookService;
import net.sparkminds.librarymanagement.service.criteria.BorrowBookCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class BorrowBookController {
    private final Logger logger = LoggerFactory.getLogger(BorrowBookController.class);

    private final BorrowBookService borrowBookService;

    @Operation(summary = "Search borrow books", description = "Search borrow books by borrow book Criteria", tags = {"Borrow book functions"})
    @GetMapping("/borrow-books")
    public ResponseEntity<List<BookResponse>> searchBorrowBooks(BorrowBookCriteria criteria) {
        logger.debug("REST request to search borrow books : {}", criteria);
        return new ResponseEntity<>(borrowBookService.searchBorrowBook(criteria), HttpStatus.OK);
    }

    @Operation(summary = "Borrow books", description = "Borrow books", tags = {"Borrow book functions"})
    @PostMapping("/borrow-books")
    public ResponseEntity<Void> borrowBooks(@Valid @RequestBody List<BorrowBookDto> borrowBookDto) {
        logger.debug("REST request to borrow books : {}", borrowBookDto);
        borrowBookService.borrowBook(borrowBookDto);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
