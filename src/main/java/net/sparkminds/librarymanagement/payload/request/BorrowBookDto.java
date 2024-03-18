package net.sparkminds.librarymanagement.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BorrowBookDto {
    @NotNull(message = "BookId field is required")
    @Positive(message = "Book id must be a positive Long")
    private Long bookId;
}
