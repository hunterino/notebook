package com.twohtwo.notebook.web.rest;

import com.twohtwo.notebook.domain.NoteBook;
import com.twohtwo.notebook.repository.NoteBookRepository;
import com.twohtwo.notebook.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.twohtwo.notebook.domain.NoteBook}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class NoteBookResource {

    private final Logger log = LoggerFactory.getLogger(NoteBookResource.class);

    private static final String ENTITY_NAME = "noteBook";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NoteBookRepository noteBookRepository;

    public NoteBookResource(NoteBookRepository noteBookRepository) {
        this.noteBookRepository = noteBookRepository;
    }

    /**
     * {@code POST  /note-books} : Create a new noteBook.
     *
     * @param noteBook the noteBook to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new noteBook, or with status {@code 400 (Bad Request)} if the noteBook has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/note-books")
    public ResponseEntity<NoteBook> createNoteBook(@Valid @RequestBody NoteBook noteBook) throws URISyntaxException {
        log.debug("REST request to save NoteBook : {}", noteBook);
        if (noteBook.getId() != null) {
            throw new BadRequestAlertException("A new noteBook cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NoteBook result = noteBookRepository.save(noteBook);
        return ResponseEntity
            .created(new URI("/api/note-books/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /note-books/:id} : Updates an existing noteBook.
     *
     * @param id the id of the noteBook to save.
     * @param noteBook the noteBook to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated noteBook,
     * or with status {@code 400 (Bad Request)} if the noteBook is not valid,
     * or with status {@code 500 (Internal Server Error)} if the noteBook couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/note-books/{id}")
    public ResponseEntity<NoteBook> updateNoteBook(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NoteBook noteBook
    ) throws URISyntaxException {
        log.debug("REST request to update NoteBook : {}, {}", id, noteBook);
        if (noteBook.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, noteBook.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!noteBookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        NoteBook result = noteBookRepository.save(noteBook);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, noteBook.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /note-books/:id} : Partial updates given fields of an existing noteBook, field will ignore if it is null
     *
     * @param id the id of the noteBook to save.
     * @param noteBook the noteBook to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated noteBook,
     * or with status {@code 400 (Bad Request)} if the noteBook is not valid,
     * or with status {@code 404 (Not Found)} if the noteBook is not found,
     * or with status {@code 500 (Internal Server Error)} if the noteBook couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/note-books/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NoteBook> partialUpdateNoteBook(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NoteBook noteBook
    ) throws URISyntaxException {
        log.debug("REST request to partial update NoteBook partially : {}, {}", id, noteBook);
        if (noteBook.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, noteBook.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!noteBookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NoteBook> result = noteBookRepository
            .findById(noteBook.getId())
            .map(existingNoteBook -> {
                if (noteBook.getName() != null) {
                    existingNoteBook.setName(noteBook.getName());
                }
                if (noteBook.getHandle() != null) {
                    existingNoteBook.setHandle(noteBook.getHandle());
                }

                return existingNoteBook;
            })
            .map(noteBookRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, noteBook.getId().toString())
        );
    }

    /**
     * {@code GET  /note-books} : get all the noteBooks.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of noteBooks in body.
     */
    @GetMapping("/note-books")
    public List<NoteBook> getAllNoteBooks(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all NoteBooks");
        if (eagerload) {
            return noteBookRepository.findAllWithEagerRelationships();
        } else {
            return noteBookRepository.findAll();
        }
    }

    /**
     * {@code GET  /note-books/:id} : get the "id" noteBook.
     *
     * @param id the id of the noteBook to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the noteBook, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/note-books/{id}")
    public ResponseEntity<NoteBook> getNoteBook(@PathVariable Long id) {
        log.debug("REST request to get NoteBook : {}", id);
        Optional<NoteBook> noteBook = noteBookRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(noteBook);
    }

    /**
     * {@code DELETE  /note-books/:id} : delete the "id" noteBook.
     *
     * @param id the id of the noteBook to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/note-books/{id}")
    public ResponseEntity<Void> deleteNoteBook(@PathVariable Long id) {
        log.debug("REST request to delete NoteBook : {}", id);
        noteBookRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
