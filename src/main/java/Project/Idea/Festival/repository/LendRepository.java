package Project.Idea.Festival.repository;

import Project.Idea.Festival.domain.Book;
import Project.Idea.Festival.domain.Lend;
import Project.Idea.Festival.domain.LendStatus;
import java.util.Optional;

public interface LendRepository extends JpaRepository<Lend, Long>{
    Optional<Lend> findByBookAndStatus(Book book, LendStatus status);
}
