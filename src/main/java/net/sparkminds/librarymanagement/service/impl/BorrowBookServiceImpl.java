package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Book;
import net.sparkminds.librarymanagement.entity.Loan;
import net.sparkminds.librarymanagement.entity.CustomAccount;
import net.sparkminds.librarymanagement.entity.User;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.payload.request.BorrowBookDto;
import net.sparkminds.librarymanagement.payload.response.BookResponse;
import net.sparkminds.librarymanagement.repository.BookRepository;
import net.sparkminds.librarymanagement.repository.LoanRepository;
import net.sparkminds.librarymanagement.repository.UserRepository;
import net.sparkminds.librarymanagement.service.BookQueryService;
import net.sparkminds.librarymanagement.service.BorrowBookService;
import net.sparkminds.librarymanagement.service.MailSenderService;
import net.sparkminds.librarymanagement.service.criteria.BorrowBookCriteria;
import net.sparkminds.librarymanagement.utils.BookCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowBookServiceImpl implements BorrowBookService {
    private final Logger logger = LoggerFactory.getLogger(BorrowBookServiceImpl.class);

    private final BookRepository bookRepository;

    private final BookQueryService bookQueryService;

    private final UserRepository userRepository;

    private final LoanRepository loanRepository;

    private final MailSenderService mailSenderService;

    @Override
    public List<BookResponse> searchBorrowBook(BorrowBookCriteria criteria) {
        logger.debug("Request to search borrow Books: {}", criteria);

        return bookQueryService.findByBorrowBook(criteria).stream()
                .map(BookResponse::buildBookResponse)
                .toList();
    }

    @Override
    @Transactional
    public void borrowBook(List<BorrowBookDto> borrowBookDto) {
        logger.debug("Request to borrow Books: {}", borrowBookDto);
        CustomAccount customAccount = (CustomAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = customAccount.getAccount().getEmail();

        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new ResourceNotFoundException("user id not found", "user.id.id-not-found"));
        List<Loan> loans = loanRepository.findAllByUser_Id(user.getId());

        List<Long> borrowBookIds = loans.stream()
                .filter(loan -> !loan.isReturned())
                .map(loan -> loan.getBook().getId())
                .toList();

        boolean isMatchBookId;
        if (borrowBookIds.isEmpty()) {
            isMatchBookId = false;
        } else {
            isMatchBookId = borrowBookDto.stream()
                    .map(BorrowBookDto::getBookId)
                    .anyMatch(borrowBookIds::contains);
        }

        if (isMatchBookId) {
            throw new ResourceInvalidException("book_id is already loan", "loan.book_id.book_id-loan");
        }

        List<Book> borrowedBooks = bookRepository.findAllById(borrowBookDto.stream().map(BorrowBookDto::getBookId).toList());
        Set<BookCategory> bookCategories = new HashSet<>();
        for (Book book : borrowedBooks) {
            bookCategories.add(book.getCategory());
        }
        if (bookCategories.size() > 1) {
            throw new ResourceInvalidException("Just only one book category allow", "book.category.category-invalid");
        }

        List<Book> bookList = borrowedBooks.stream()
                .map(book -> {
                    book.setQuantity(book.getQuantity().subtract(BigDecimal.ONE));
                    if (book.getQuantity().compareTo(BigDecimal.ONE) < 0) {
                        book.setIsAvailable(false);
                    }
                    return book;
                })
                .toList();
        bookRepository.saveAll(bookList);

        List<Loan> listLoan = new ArrayList<>();
        for (Book book : bookList) {
            Loan loan = Loan.builder()
                    .book(book)
                    .user(user)
                    .borrowDate(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                    .returnDate(LocalDateTime.now().plusDays(3).toInstant(ZoneOffset.UTC))
                    .build();
            listLoan.add(loan);
        }
        loanRepository.saveAll(listLoan);

        String imgPaths = bookList.stream()
                .map(Book::getImagePath)
                .collect(Collectors.joining(", "));
        mailSenderService.sendEmailToUserBorrowedBook(email, imgPaths);
    }
}
