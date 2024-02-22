package net.sparkminds.librarymanagement.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthorDto {
    @NotEmpty(message = "Author name field is required")
    @Size(min = 3, message = "Author name must be at least 3 characters")
    private String name;

    @NotEmpty(message = "Author address field is required")
    @Size(min = 3, message = "Author address must be at least 3 characters")
    private String address;
}
