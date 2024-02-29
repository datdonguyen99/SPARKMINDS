package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Book;
import net.sparkminds.librarymanagement.entity.CustomAccount;
import net.sparkminds.librarymanagement.entity.User;
import net.sparkminds.librarymanagement.entity.Loan;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.payload.request.BorrowBookDto;
import net.sparkminds.librarymanagement.payload.response.BookResponse;
import net.sparkminds.librarymanagement.repository.BookRepository;
import net.sparkminds.librarymanagement.repository.LoanRepository;
import net.sparkminds.librarymanagement.repository.UserRepository;
import net.sparkminds.librarymanagement.service.ReturnBookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnBookServiceImpl implements ReturnBookService {
    private final Logger logger = LoggerFactory.getLogger(ReturnBookServiceImpl.class);

    private final UserRepository userRepository;

    private final LoanRepository loanRepository;

    private final BookRepository bookRepository;

    @Override
    public List<BookResponse> booksBeingBorrowed() {
        logger.debug("Request to get books being borrowed");
        CustomAccount customAccount = (CustomAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = customAccount.getAccount().getEmail();

        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new ResourceNotFoundException("user id not found", "user.id.id-not-found"));

        List<Loan> loans = loanRepository.findAllByUser_Id(user.getId());

        return loans.stream()
                .filter(loan -> !loan.isReturned())
                .map(loan -> BookResponse.buildBookResponse(loan.getBook()))
                .toList();
    }

    @Override
    @Transactional
    public void returnBook(List<BorrowBookDto> borrowBookDto) {
        logger.debug("Request to return books");
        CustomAccount customAccount = (CustomAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = customAccount.getAccount().getEmail();

        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new ResourceNotFoundException("user id not found", "user.id.id-not-found"));
        List<Loan> loans = loanRepository.findAllByUser_Id(user.getId());
        List<Loan> loansNotReturn = loans.stream()
                .filter(loan -> !loan.isReturned())
                .toList();

        boolean loanExists = borrowBookDto.stream()
                .map(BorrowBookDto::getBookId)
                .allMatch(bookId -> loansNotReturn.stream()
                        .anyMatch(loan -> loan.getBook().getId().equals(bookId))
                );

        if (!loanExists) {
            throw new ResourceNotFoundException("Book id not found", "Book.id.id-not-found");
        }

        List<Long> matchingBookIds = borrowBookDto.stream()
                .map(BorrowBookDto::getBookId)
                .toList();

        List<Loan> loansNotReturnWithMatchingBooks = loansNotReturn.stream()
                .filter(loan -> matchingBookIds.contains(loan.getBook().getId()))
                .toList();

        loansNotReturnWithMatchingBooks.forEach(loan -> {
            Book book = loan.getBook();
            book.setQuantity(book.getQuantity().add(BigDecimal.ONE));

            if (!book.getIsAvailable()) {
                book.setIsAvailable(true);
            }

            loan.setReturned(true);
            loan.setReturnDate(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        });

        bookRepository.saveAll(loansNotReturnWithMatchingBooks.stream().map(Loan::getBook).toList());
        loanRepository.saveAll(loansNotReturnWithMatchingBooks);
    }
}
