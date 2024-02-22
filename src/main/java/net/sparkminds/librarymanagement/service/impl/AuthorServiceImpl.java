package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Author;
import net.sparkminds.librarymanagement.payload.request.AuthorDto;
import net.sparkminds.librarymanagement.payload.response.AuthorResponse;
import net.sparkminds.librarymanagement.repository.AuthorRepository;
import net.sparkminds.librarymanagement.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final Logger logger = LoggerFactory.getLogger(AuthorServiceImpl.class);

    private final AuthorRepository authorRepository;

    @Override
    public AuthorResponse save(AuthorDto authorDto) {
        logger.debug("Request to save Author : {}", authorDto);

        Author newAuthor = Author.builder()
                .name(authorDto.getName())
                .address(authorDto.getAddress())
                .build();
        authorRepository.save(newAuthor);

        return AuthorResponse.buildAuthorResponse(newAuthor);
    }
}
