package com.twohtwo.notebook.repository;

import com.twohtwo.notebook.domain.Share;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Share entity.
 */
@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {
    @Query("select share from Share share where share.author.login = ?#{principal.username}")
    List<Share> findByAuthorIsCurrentUser();

    @Query("select share from Share share where share.withUser.login = ?#{principal.username}")
    List<Share> findByWithUserIsCurrentUser();

    default Optional<Share> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Share> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Share> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct share from Share share left join fetch share.author left join fetch share.withUser left join fetch share.sharing",
        countQuery = "select count(distinct share) from Share share"
    )
    Page<Share> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct share from Share share left join fetch share.author left join fetch share.withUser left join fetch share.sharing"
    )
    List<Share> findAllWithToOneRelationships();

    @Query(
        "select share from Share share left join fetch share.author left join fetch share.withUser left join fetch share.sharing where share.id =:id"
    )
    Optional<Share> findOneWithToOneRelationships(@Param("id") Long id);
}
