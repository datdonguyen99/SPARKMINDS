package net.sparkminds.librarymanagement.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sparkminds.librarymanagement.entity.Book;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class BookResponse {
    private Long id;

    private String title;

    private AuthorResponse author;

    private PublisherResponse publisher;

    private int publishedYear;

    private int numberOfPages;

    private String isbn;

    private BigDecimal quantity;

    private String imagePath;

    public static BookResponse buildBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(AuthorResponse.buildAuthorResponse(book.getAuthor()))
                .publisher(PublisherResponse.buildPublisherResponse(book.getPublisher()))
                .publishedYear(book.getPublishedYear())
                .numberOfPages(book.getNumberOfPages())
                .isbn(book.getIsbn())
                .quantity(book.getQuantity())
                .imagePath(book.getImagePath())
                .build();
    }
}
