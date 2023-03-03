package com.twohtwo.notebook.repository;

import com.twohtwo.notebook.domain.NoteBook;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the NoteBook entity.
 */
@Repository
public interface NoteBookRepository extends JpaRepository<NoteBook, Long> {
    @Query("select noteBook from NoteBook noteBook where noteBook.user.login = ?#{principal.username}")
    List<NoteBook> findByUserIsCurrentUser();

    default Optional<NoteBook> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<NoteBook> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<NoteBook> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct noteBook from NoteBook noteBook left join fetch noteBook.user",
        countQuery = "select count(distinct noteBook) from NoteBook noteBook"
    )
    Page<NoteBook> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct noteBook from NoteBook noteBook left join fetch noteBook.user")
    List<NoteBook> findAllWithToOneRelationships();

    @Query("select noteBook from NoteBook noteBook left join fetch noteBook.user where noteBook.id =:id")
    Optional<NoteBook> findOneWithToOneRelationships(@Param("id") Long id);
}
