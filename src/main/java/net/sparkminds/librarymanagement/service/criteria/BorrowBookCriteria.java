package net.sparkminds.librarymanagement.service.criteria;

import lombok.NoArgsConstructor;
import net.sparkminds.librarymanagement.utils.BookCategory;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.Filter;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
public class BorrowBookCriteria implements Serializable, Criteria {
    @Serial
    private static final long serialVersionUID = 1L;

    private StringFilter title;

    private CategoryFilter category;

    private Boolean distinct;

    public BorrowBookCriteria(BorrowBookCriteria other) {
        this.title = other.title == null ? null : other.title.copy();
        this.category = other.category == null ? null : other.category.copy();
        this.distinct = other.distinct;
    }

    @Override
    public BorrowBookCriteria copy() {
        return new BorrowBookCriteria(this);
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

    public CategoryFilter getCategory() {
        return category;
    }

    public void setCategory(CategoryFilter category) {
        this.category = category;
    }

    public static class CategoryFilter extends Filter<BookCategory> {
        private static final long serialVersionUID = 1L;

        public CategoryFilter() {
        }

        public CategoryFilter(CategoryFilter filter) {
            super(filter);
        }

        @Override
        public CategoryFilter copy() {
            return new CategoryFilter(this);
        }
    }
}
