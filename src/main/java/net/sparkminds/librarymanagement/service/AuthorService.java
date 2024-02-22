package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.payload.request.AuthorDto;
import net.sparkminds.librarymanagement.payload.response.AuthorResponse;

public interface AuthorService {
    AuthorResponse save(AuthorDto authorDto);
}
