package net.sparkminds.librarymanagement.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.request.BookDto;
import net.sparkminds.librarymanagement.payload.response.BookResponse;
import net.sparkminds.librarymanagement.payload.response.PreviewImageResponse;
import net.sparkminds.librarymanagement.service.BookService;
import net.sparkminds.librarymanagement.service.criteria.BookCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
public class BookController {
    private final Logger logger = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    @Operation(summary = "Search book", description = "Search book by book Criteria", tags = {"Book functions"})
    @GetMapping("/books")
    public ResponseEntity<Page<BookResponse>> searchBooks(BookCriteria criteria, Pageable pageable) {
        logger.debug("REST request to search Book : {}, {}", criteria, pageable);
        return new ResponseEntity<>(bookService.searchBook(criteria, pageable), HttpStatus.OK);
    }

    @Operation(summary = "Add new book", description = "Add new book", tags = {"Book functions"})
    @PostMapping("/books")
    public ResponseEntity<BookResponse> addBook(@Valid @RequestBody BookDto bookDto) {
        logger.debug("REST request to save Book : {}", bookDto);
        return new ResponseEntity<>(bookService.save(bookDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update book", description = "Update book", tags = {"Book functions"})
    @PutMapping("/books/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable(value = "id") final Long id,
                                                   @Valid @RequestBody BookDto bookDto) {
        logger.debug("REST request to update Book : {}, {}", id, bookDto);
        return new ResponseEntity<>(bookService.update(id, bookDto), HttpStatus.OK);
    }

    @Operation(summary = "Delete book", description = "Delete book(Update flag)", tags = {"Book functions"})
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        logger.debug("REST request to delete Book : {}", id);
        bookService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Import book using .csv file", description = "Import book using .csv file", tags = {"Book functions"})
    @PostMapping(value = "/import-books", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importBook(@RequestParam(name = "file")
                                           @Parameter(description = "File to upload", required = true) MultipartFile file) {
        logger.debug("REST request to import Book from .csv : {}", file);
        bookService.importFromCsv(file);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Upload book's image", description = "Upload book's image", tags = {"Book functions"})
    @PostMapping(value = "/books/{bookId}/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadImage(@PathVariable Long bookId,
                                            @RequestParam(name = "image")
                                            @Parameter(description = "File to upload", required = true) MultipartFile imageFile) {
        logger.debug("REST request to upload image : {}, {}", bookId, imageFile);
        bookService.saveImage(bookId, imageFile);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Update book's image", description = "Update book's image", tags = {"Book functions"})
    @PutMapping(value = "/books/{bookId}/updateImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateImage(@PathVariable Long bookId,
                                            @RequestParam(name = "image")
                                            @Parameter(description = "File to upload", required = true) MultipartFile imageFile) {
        logger.debug("REST request to update image : {}, {}", bookId, imageFile);
        bookService.updateImage(bookId, imageFile);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Preview book's image", description = "Preview book's image", tags = {"Book functions"})
    @GetMapping(value = "/books/{bookId}/previewImage")
    public ResponseEntity<PreviewImageResponse> previewImage(@PathVariable Long bookId) {
        logger.debug("REST request to preview image : {}", bookId);
        return new ResponseEntity<>(bookService.previewImage(bookId), HttpStatus.OK);
    }

    @Operation(summary = "Delete book's image", description = "Delete book's image", tags = {"Book functions"})
    @DeleteMapping(value = "/books/{bookId}/deleteImage")
    public ResponseEntity<Void> deleteImage(@PathVariable Long bookId) {
        logger.debug("REST request to delete image : {}", bookId);
        bookService.deleteImage(bookId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
