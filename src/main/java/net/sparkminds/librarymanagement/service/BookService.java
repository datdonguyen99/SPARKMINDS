package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.payload.request.BookDto;
import net.sparkminds.librarymanagement.payload.response.BookResponse;
import net.sparkminds.librarymanagement.service.criteria.BookCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    /**
     * Search book by bookCriteria
     *
     * @param bookCriteria
     * @param pageable
     * @return list of books
     */
    Page<BookResponse> searchBook(BookCriteria bookCriteria, Pageable pageable);

    /**
     * Save a book
     *
     * @param bookDto the entity to save
     * @return
     */
    BookResponse save(BookDto bookDto);

    /**
     * Update a book by {id}
     *
     * @param id      id of book
     * @param bookDto
     * @return
     */
    BookResponse update(Long id, BookDto bookDto);

    /**
     * Delete a book by {id}
     *
     * @param id id of book
     */
    void delete(Long id);
}
