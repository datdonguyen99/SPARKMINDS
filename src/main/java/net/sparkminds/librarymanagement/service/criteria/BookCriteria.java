package net.sparkminds.librarymanagement.service.criteria;

import lombok.NoArgsConstructor;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.IntegerFilter;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
public class BookCriteria implements Serializable, Criteria {
    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private IntegerFilter publishedYear;

    private IntegerFilter numberOfPages;

    private StringFilter authorName;

    private StringFilter publisherName;

    private InstantFilterV2 from;

    private InstantFilterV2 to;

    private Boolean distinct;

    public BookCriteria(BookCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.publishedYear = other.publishedYear == null ? null : other.publishedYear.copy();
        this.numberOfPages = other.numberOfPages == null ? null : other.numberOfPages.copy();
        this.authorName = other.authorName == null ? null : other.authorName.copy();
        this.publisherName = other.publisherName == null ? null : other.publisherName.copy();
        this.from = other.from == null ? null : other.from.copy();
        this.to = other.to == null ? null : other.to.copy();
        this.distinct = other.distinct;
    }

    @Override
    public BookCriteria copy() {
        return new BookCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public IntegerFilter publishedYear() {
        if (publishedYear == null) {
            publishedYear = new IntegerFilter();
        }
        return publishedYear;
    }

    public IntegerFilter getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(IntegerFilter publishedYear) {
        this.publishedYear = publishedYear;
    }

    public IntegerFilter numberOfPages() {
        if (numberOfPages == null) {
            numberOfPages = new IntegerFilter();
        }
        return numberOfPages;
    }

    public IntegerFilter getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(IntegerFilter numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public StringFilter authorName() {
        if (authorName == null) {
            authorName = new StringFilter();
        }
        return authorName;
    }

    public StringFilter getAuthorName() {
        return authorName;
    }

    public void setAuthorName(StringFilter authorName) {
        this.authorName = authorName;
    }

    public StringFilter publisherName() {
        if (publisherName == null) {
            publisherName = new StringFilter();
        }
        return publisherName;
    }

    public StringFilter getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(StringFilter publisherName) {
        this.publisherName = publisherName;
    }

    public InstantFilterV2 from() {
        if (from == null) {
            from = new InstantFilterV2();
        }
        return from;
    }

    public InstantFilterV2 getFrom() {
        return from;
    }

    public void setFrom(InstantFilterV2 from) {
        this.from = from;
    }

    public InstantFilterV2 to() {
        if (to == null) {
            to = new InstantFilterV2();
        }
        return to;
    }

    public InstantFilterV2 getTo() {
        return to;
    }

    public void setTo(InstantFilterV2 to) {
        this.to = to;
    }
}
