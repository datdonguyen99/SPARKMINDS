package net.sparkminds.librarymanagement.service;

import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Book;
import net.sparkminds.librarymanagement.entity.Book_;
import net.sparkminds.librarymanagement.entity.Author_;
import net.sparkminds.librarymanagement.entity.Publisher_;
import net.sparkminds.librarymanagement.entity.Loan_;
import net.sparkminds.librarymanagement.repository.BookRepository;
import net.sparkminds.librarymanagement.service.criteria.BookCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookQueryService extends QueryService<Book> {
    private final Logger logger = LoggerFactory.getLogger(BookQueryService.class);

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public Page<Book> findByCriteria(BookCriteria criteria, Pageable pageable) {
        logger.debug("find by criteria : {}", criteria);
        final Specification<Book> spec = createSpecification(criteria);

        return bookRepository.findAll(spec, pageable);
    }

    protected Specification<Book> createSpecification(BookCriteria criteria) {
        Specification<Book> specification = Specification.where(null);

        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }

            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Book_.id));
            }

            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Book_.title));
            }

            if (criteria.getPublishedYear() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPublishedYear(), Book_.publishedYear));
            }

            if (criteria.getNumberOfPages() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumberOfPages(), Book_.numberOfPages));
            }

            if (criteria.getAuthorName() != null) {
                specification = specification.and(buildSpecification(criteria.getAuthorName(), root -> root.join(Book_.author, JoinType.LEFT).get(Author_.name)));
            }

            if (criteria.getPublisherName() != null) {
                specification = specification.and(buildSpecification(criteria.getPublisherName(), root -> root.join(Book_.publisher, JoinType.LEFT).get(Publisher_.name)));
            }

            if (criteria.getFrom() != null) {
                specification = specification.and(buildSpecification(criteria.getFrom(), bookRoot -> bookRoot.join(Book_.loans, JoinType.LEFT).get(Loan_.borrowDate)));
            }

            if (criteria.getTo() != null) {
                specification = specification.and(buildSpecification(criteria.getTo(), bookRoot -> bookRoot.join(Book_.loans, JoinType.LEFT).get(Loan_.borrowDate)));
            }
        }

        return specification;
    }
}
