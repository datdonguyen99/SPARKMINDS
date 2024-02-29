package net.sparkminds.librarymanagement.repository;

import net.sparkminds.librarymanagement.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("unused")
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findAllByUser_Id(Long userId);
}
