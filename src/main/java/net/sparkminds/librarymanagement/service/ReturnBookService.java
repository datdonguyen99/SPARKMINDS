package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.payload.request.BorrowBookDto;
import net.sparkminds.librarymanagement.payload.response.BookResponse;

import java.util.List;

public interface ReturnBookService {
    /**
     * List of books being borrowed
     *
     * @return list of books being borrowed
     */
    List<BookResponse> booksBeingBorrowed();

    /**
     * Return books
     *
     * @param borrowBookDto
     */
    void returnBook(List<BorrowBookDto> borrowBookDto);
}
