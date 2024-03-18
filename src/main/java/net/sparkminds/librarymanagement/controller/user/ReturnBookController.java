package net.sparkminds.librarymanagement.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.request.BorrowBookDto;
import net.sparkminds.librarymanagement.payload.response.BookResponse;
import net.sparkminds.librarymanagement.service.ReturnBookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class ReturnBookController {
    private final Logger logger = LoggerFactory.getLogger(ReturnBookController.class);

    private final ReturnBookService returnBookService;

    @Operation(summary = "Books being borrowed", description = "Books being borrowed", tags = {"Return book functions"})
    @GetMapping("/return-books")
    public ResponseEntity<List<BookResponse>> booksBeingBorrowed() {
        logger.debug("REST request to get books being borrowed");
        return new ResponseEntity<>(returnBookService.booksBeingBorrowed(), HttpStatus.OK);
    }

    @Operation(summary = "Return books", description = "Return books", tags = {"Return book functions"})
    @PostMapping("/return-books")
    public ResponseEntity<Void> returnBooks(@Valid @RequestBody List<BorrowBookDto> borrowBookDto) {
        logger.debug("REST request to return books : {}", borrowBookDto);
        returnBookService.returnBook(borrowBookDto);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
