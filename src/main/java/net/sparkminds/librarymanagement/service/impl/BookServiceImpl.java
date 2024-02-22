package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Author;
import net.sparkminds.librarymanagement.entity.Book;
import net.sparkminds.librarymanagement.entity.Publisher;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.payload.request.BookDto;
import net.sparkminds.librarymanagement.payload.response.BookResponse;
import net.sparkminds.librarymanagement.repository.AuthorRepository;
import net.sparkminds.librarymanagement.repository.BookRepository;
import net.sparkminds.librarymanagement.repository.PublisherRepository;
import net.sparkminds.librarymanagement.service.BookQueryService;
import net.sparkminds.librarymanagement.service.BookService;
import net.sparkminds.librarymanagement.service.criteria.BookCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;

    private final BookQueryService bookQueryService;

    private final AuthorRepository authorRepository;

    private final PublisherRepository publisherRepository;

    @Override
    public Page<BookResponse> searchBook(BookCriteria criteria, Pageable pageable) {
        logger.debug("Request to search Books: {}, {}", criteria, pageable);

        return bookQueryService.findByCriteria(criteria, pageable)
                .map(BookResponse::buildBookResponse);
    }

    @Override
    @Transactional
    public BookResponse save(BookDto bookDto) {
        logger.debug("Request to save Book : {}", bookDto);

        Author author = authorRepository.findById(bookDto.getAuthorId()).orElseThrow(() -> new ResourceNotFoundException("Author id not found", "author.id.id-not-found"));
        Publisher publisher = publisherRepository.findById(bookDto.getPublisherId()).orElseThrow(() -> new ResourceNotFoundException("Publisher id not found", "publisher.id.id-not-found"));

        Book newBook = Book.builder()
                .title(bookDto.getTitle())
                .author(author)
                .publisher(publisher)
                .publishedYear(bookDto.getPublishedYear())
                .numberOfPages(bookDto.getNumberOfPages())
                .isbn(bookDto.getIsbn())
                .quantity(bookDto.getQuantity())
                .imagePath(bookDto.getImagePath())
                .build();

        bookRepository.save(newBook);

        return BookResponse.buildBookResponse(newBook);
    }

    @Override
    @Transactional
    public BookResponse update(Long id, BookDto bookDto) {
        logger.debug("Request to update Book : {}, {}", id, bookDto);

        if (id == null) {
            throw new ResourceInvalidException("Invalid id", "book.id.id-is-invalid");
        }

        Book updatedBook = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Id book not found", "book.id.id-not-found"));
        Author updatedAuthor = authorRepository.findById(bookDto.getAuthorId()).orElseThrow(() -> new ResourceNotFoundException("Id author not found", "author.id.id-not-found"));
        Publisher updatedPublisher = publisherRepository.findById(bookDto.getPublisherId()).orElseThrow(() -> new ResourceNotFoundException("Id publisher not found", "publisher.id.id-not-found"));

        updatedBook.setTitle(bookDto.getTitle());
        updatedBook.setAuthor(updatedAuthor);
        updatedBook.setPublisher(updatedPublisher);
        updatedBook.setPublishedYear(bookDto.getPublishedYear());
        updatedBook.setNumberOfPages(bookDto.getNumberOfPages());
        updatedBook.setIsbn(bookDto.getIsbn());
        updatedBook.setQuantity(bookDto.getQuantity());
        updatedBook.setImagePath(bookDto.getImagePath());
        bookRepository.save(updatedBook);

        return BookResponse.buildBookResponse(updatedBook);
    }

    @Override
    public void delete(Long id) {
        logger.debug("Request to delete Book : {}", id);

        Book deleteBook = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Id book not found", "book.id.id-not-found"));
        deleteBook.setIsAvailable(false);

        bookRepository.save(deleteBook);
    }
}
