package net.sparkminds.librarymanagement.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.request.AuthorDto;
import net.sparkminds.librarymanagement.payload.response.AuthorResponse;
import net.sparkminds.librarymanagement.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
public class AuthorController {
    private final Logger logger = LoggerFactory.getLogger(AuthorController.class);

    private final AuthorService authorService;

    @Operation(summary = "Add new author", description = "Add new author", tags = {"Author functions"})
    @PostMapping("/authors")
    public ResponseEntity<AuthorResponse> addAuthor(@Valid @RequestBody AuthorDto authorDto) {
        logger.debug("REST request to add Author : {}", authorDto);
        return new ResponseEntity<>(authorService.save(authorDto), HttpStatus.CREATED);
    }
}
