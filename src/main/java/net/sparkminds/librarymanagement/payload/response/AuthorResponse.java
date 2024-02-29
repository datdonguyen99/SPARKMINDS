package net.sparkminds.librarymanagement.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sparkminds.librarymanagement.entity.Author;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuthorResponse {
    private Long id;

    private String name;

    private String address;

    public static AuthorResponse buildAuthorResponse(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .address(author.getAddress())
                .build();
    }
}
