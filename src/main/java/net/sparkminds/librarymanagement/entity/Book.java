package net.sparkminds.librarymanagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "books")
public class Book extends Auditable implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", length = 50)
    @Size(min = 1, max = 50)
    private String title;

    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false, name = "author_id")
    private Author author;

    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false, name = "publisher_id")
    private Publisher publisher;

    @NotNull
    @Positive
    @Column(name = "published_year")
    private int publishedYear;

    @NotNull
    @Positive
    @Column(name = "number_of_pages")
    private int numberOfPages;

    @NotNull
    @Column(name = "isbn", length = 50)
    @Size(min = 1, max = 50)
    private String isbn;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();

    @NotNull
    @Column(name = "quantity")
    private BigDecimal quantity;

    @Lob
    @Column(name = "image_path")
    private String imagePath;

    @NotNull
    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;
}
