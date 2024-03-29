package net.sparkminds.librarymanagement.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Author;
import net.sparkminds.librarymanagement.entity.Book;
import net.sparkminds.librarymanagement.entity.Publisher;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.payload.request.BookDto;
import net.sparkminds.librarymanagement.payload.response.BookResponse;
import net.sparkminds.librarymanagement.payload.response.PreviewImageResponse;
import net.sparkminds.librarymanagement.repository.AuthorRepository;
import net.sparkminds.librarymanagement.repository.BookRepository;
import net.sparkminds.librarymanagement.repository.PublisherRepository;
import net.sparkminds.librarymanagement.service.BookQueryService;
import net.sparkminds.librarymanagement.service.BookService;
import net.sparkminds.librarymanagement.service.criteria.BookCriteria;
import net.sparkminds.librarymanagement.utils.BookCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.sparkminds.librarymanagement.utils.AppConstants.*;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    @Value("${image.upload.directory}")
    private String uploadDirectory;

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
                .category(bookDto.getCategory())
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
        updatedBook.setCategory(bookDto.getCategory());
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

    @Override
    @Transactional
    public void importFromCsv(MultipartFile file) {
        // Validate file extension
        if (!ALLOWED_EXTENSIONS.contains(StringUtils.getFilenameExtension(file.getOriginalFilename()))) {
            throw new ResourceInvalidException("Only CSV files are allowed.", "file.file-extension-invalid-format");
        }

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResourceInvalidException("File size should be less than 5MB.", "file.size-invalid");
        }

        if (!validateHeader(CSV_FILE_PATH_READ + file.getOriginalFilename())) {
            throw new ResourceInvalidException("Invalid header", "file.header.header-invalid");
        }

        // Process CSV file
        try (FileReader fileReader = new FileReader(CSV_FILE_PATH_READ + file.getOriginalFilename());
             CSVReader csvReader = new CSVReader(fileReader)) {
            csvReader.readNext();        // Omit header

            List<String[]> rows = csvReader.readAll();
            List<Book> booksList = new ArrayList<>();

            for (String[] row : rows) {
                Book book = new Book();

                book.setTitle(row[1]);
                Author author = authorRepository.findById(Long.parseLong(row[2])).orElseThrow(() -> new ResourceNotFoundException("author id not found", "author.id.id-not-found"));
                book.setAuthor(author);
                Publisher publisher = publisherRepository.findById(Long.parseLong(row[3])).orElseThrow(() -> new ResourceNotFoundException("publisher id not found", "publisher.id.id-not-found"));
                book.setPublisher(publisher);
                book.setPublishedYear(Integer.parseInt(row[4]));
                book.setNumberOfPages(Integer.parseInt(row[5]));
                book.setIsbn(row[6]);
                book.setQuantity(new BigDecimal(row[7]));
                book.setImagePath(row[8]);
                book.setCategory(BookCategory.valueOf(row[9]));

                booksList.add(book);
            }

            for (Book book : booksList) {
                Optional<Book> tempBook = bookRepository.findByTitle(book.getTitle());
                if (tempBook.isPresent()) {
                    BigDecimal newQuantity = tempBook.get().getQuantity().add(book.getQuantity());
                    tempBook.get().setQuantity(newQuantity);
                    bookRepository.save(tempBook.get());
                } else {
                    bookRepository.save(book);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateHeader(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] header = reader.readNext();
            if (header == null) {
                return false;
            }

            for (String expectedHeader : EXPECTED_HEADERS) {
                boolean found = false;
                for (String h : header) {
                    if (expectedHeader.equals(h.trim())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage(), "file.file-not-found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void saveImage(Long bookId, MultipartFile imageFile) {
        // Validate file extension
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(StringUtils.getFilenameExtension(imageFile.getOriginalFilename()))) {
            throw new ResourceInvalidException("File not allowed.", "file.file-extension-invalid-format");
        }

        // Validate file size
        if (imageFile.getSize() > MAX_IMAGE_FILE_SIZE) {
            throw new ResourceInvalidException("File size should be less than 1MB.", "file.size-invalid");
        }

        try {
            // Save image to folder in server
            String fileName = bookId + "_" + imageFile.getOriginalFilename();
            Path path = Paths.get(uploadDirectory + fileName);
            Files.write(path, imageFile.getBytes());

            // Save image's path to db
            Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book id not found", "book.id.id-not-found"));
            if (book.getImagePath() != null) {
                throw new ResourceInvalidException("Image have already existed", "book.image.image-already-existed");
            }
            book.setImagePath(path.toString());
            bookRepository.save(book);
        } catch (IOException e) {
            throw new ResourceNotFoundException("File not found", "file.file-not-found");
        }
    }

    @Override
    @Transactional
    public void updateImage(Long bookId, MultipartFile imageFile) {
        // Validate file extension
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(StringUtils.getFilenameExtension(imageFile.getOriginalFilename()))) {
            throw new ResourceInvalidException("File not allowed.", "file.file-extension-invalid-format");
        }

        // Validate file size
        if (imageFile.getSize() > MAX_IMAGE_FILE_SIZE) {
            throw new ResourceInvalidException("File size should be less than 1MB.", "file.size-invalid");
        }

        try {
            // Save image to folder in server
            String fileName = bookId + "_" + imageFile.getOriginalFilename();
            Path path = Paths.get(uploadDirectory + fileName);
            Files.write(path, imageFile.getBytes());

            // Save image's path to db
            Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book id not found", "book.id.id-not-found"));
            if (book.getImagePath() == null) {
                throw new ResourceInvalidException("Image have not existed", "book.image.image-not-existed");
            }
            book.setImagePath(path.toString());
            bookRepository.save(book);
        } catch (IOException e) {
            throw new ResourceNotFoundException("File not found", "file.file-not-found");
        }
    }

    @Override
    public PreviewImageResponse previewImage(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book id not found", "book.id.id-not-found"));
        if (book.getImagePath() == null) {
            throw new ResourceNotFoundException("Image path not found", "book.image.image-path-not-found");
        }

        return PreviewImageResponse.builder()
                .imgPath(book.getImagePath())
                .build();
    }

    @Override
    public void deleteImage(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book id not found", "book.id.id-not-found"));
        if (book.getImagePath() == null) {
            throw new ResourceNotFoundException("Image path not found", "book.image.image-path-not-found");
        }
        book.setImagePath(null);
        bookRepository.save(book);
    }
}
