package com.twohtwo.notebook.repository;

import com.twohtwo.notebook.domain.Note;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Note entity.
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    @Query("select note from Note note where note.user.login = ?#{principal.username}")
    List<Note> findByUserIsCurrentUser();

    default Optional<Note> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Note> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Note> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct note from Note note left join fetch note.user left join fetch note.notebook",
        countQuery = "select count(distinct note) from Note note"
    )
    Page<Note> findAllWithToOneRelationships(Pageable pageable);


    @Query(
        value = "select distinct note from Note note left join fetch note.user left join fetch note.notebook where note.content like %:searchingFor%",
        countQuery = "select count(distinct note) from Note note where note.content like %:searchingFor%"
    )
    Page<Note> searchAllByTextBlob(String searchingFor, Pageable pageable);

    @Query("select distinct note from Note note left join fetch note.user left join fetch note.notebook")
    List<Note> findAllWithToOneRelationships();

    @Query("select note from Note note left join fetch note.user left join fetch note.notebook where note.id =:id")
    Optional<Note> findOneWithToOneRelationships(@Param("id") Long id);
}
