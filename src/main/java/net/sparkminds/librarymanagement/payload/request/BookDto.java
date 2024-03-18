package net.sparkminds.librarymanagement.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sparkminds.librarymanagement.utils.BookCategory;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookDto {
    @NotEmpty(message = "Title field is required")
    @Size(min = 3, max = 254, message = "Title must be at least 3 characters")
    private String title;
// validate
    @NotNull(message = "authorId field is required")
    @Positive(message = "authorId must be a positive Long")
    private Long authorId;

    @NotNull(message = "publisherId field is required")
    @Positive(message = "publisherId must be a positive Long")
    private Long publisherId;

    @NotNull(message = "Published year field is required")
    @Positive(message = "Published year must be a positive integer")
    private int publishedYear;

    @NotNull(message = "Number of pages field is required")
    @Positive(message = "Number of pages must be a positive integer")
    private int numberOfPages;

    @NotEmpty(message = "ISBN field is required")
    @Size(min = 3, message = "isbn must be at least 3 characters")
    private String isbn;

    @NotNull(message = "Quantity field is required")
    @PositiveOrZero(message = "Quantity must be a positive or zero BigDecimal")
    private BigDecimal quantity;

    private String imagePath;

    @NotNull(message = "Category field is required")
    private BookCategory category;
}
