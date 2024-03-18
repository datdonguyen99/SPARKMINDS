package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.payload.request.BorrowBookDto;
import net.sparkminds.librarymanagement.payload.response.BookResponse;
import net.sparkminds.librarymanagement.service.criteria.BorrowBookCriteria;

import java.util.List;

public interface BorrowBookService {
    /**
     * Search book can borrow
     *
     * @param criteria
     * @return list of books can borrow
     */
    List<BookResponse> searchBorrowBook(BorrowBookCriteria criteria);

    /**
     * Borrow books
     *
     * @param borrowBookDto
     */
    void borrowBook(List<BorrowBookDto> borrowBookDto);
}
