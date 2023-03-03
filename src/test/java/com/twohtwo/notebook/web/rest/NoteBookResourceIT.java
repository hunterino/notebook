package com.twohtwo.notebook.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.twohtwo.notebook.IntegrationTest;
import com.twohtwo.notebook.domain.NoteBook;
import com.twohtwo.notebook.repository.NoteBookRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link NoteBookResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class NoteBookResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_HANDLE = "AAAAAAAAAA";
    private static final String UPDATED_HANDLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/note-books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NoteBookRepository noteBookRepository;

    @Mock
    private NoteBookRepository noteBookRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNoteBookMockMvc;

    private NoteBook noteBook;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NoteBook createEntity(EntityManager em) {
        NoteBook noteBook = new NoteBook().name(DEFAULT_NAME).handle(DEFAULT_HANDLE);
        return noteBook;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NoteBook createUpdatedEntity(EntityManager em) {
        NoteBook noteBook = new NoteBook().name(UPDATED_NAME).handle(UPDATED_HANDLE);
        return noteBook;
    }

    @BeforeEach
    public void initTest() {
        noteBook = createEntity(em);
    }

    @Test
    @Transactional
    void createNoteBook() throws Exception {
        int databaseSizeBeforeCreate = noteBookRepository.findAll().size();
        // Create the NoteBook
        restNoteBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteBook)))
            .andExpect(status().isCreated());

        // Validate the NoteBook in the database
        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeCreate + 1);
        NoteBook testNoteBook = noteBookList.get(noteBookList.size() - 1);
        assertThat(testNoteBook.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testNoteBook.getHandle()).isEqualTo(DEFAULT_HANDLE);
    }

    @Test
    @Transactional
    void createNoteBookWithExistingId() throws Exception {
        // Create the NoteBook with an existing ID
        noteBook.setId(1L);

        int databaseSizeBeforeCreate = noteBookRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNoteBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteBook)))
            .andExpect(status().isBadRequest());

        // Validate the NoteBook in the database
        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = noteBookRepository.findAll().size();
        // set the field null
        noteBook.setName(null);

        // Create the NoteBook, which fails.

        restNoteBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteBook)))
            .andExpect(status().isBadRequest());

        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHandleIsRequired() throws Exception {
        int databaseSizeBeforeTest = noteBookRepository.findAll().size();
        // set the field null
        noteBook.setHandle(null);

        // Create the NoteBook, which fails.

        restNoteBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteBook)))
            .andExpect(status().isBadRequest());

        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNoteBooks() throws Exception {
        // Initialize the database
        noteBookRepository.saveAndFlush(noteBook);

        // Get all the noteBookList
        restNoteBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(noteBook.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].handle").value(hasItem(DEFAULT_HANDLE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllNoteBooksWithEagerRelationshipsIsEnabled() throws Exception {
        when(noteBookRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNoteBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(noteBookRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllNoteBooksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(noteBookRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNoteBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(noteBookRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getNoteBook() throws Exception {
        // Initialize the database
        noteBookRepository.saveAndFlush(noteBook);

        // Get the noteBook
        restNoteBookMockMvc
            .perform(get(ENTITY_API_URL_ID, noteBook.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(noteBook.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.handle").value(DEFAULT_HANDLE));
    }

    @Test
    @Transactional
    void getNonExistingNoteBook() throws Exception {
        // Get the noteBook
        restNoteBookMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNoteBook() throws Exception {
        // Initialize the database
        noteBookRepository.saveAndFlush(noteBook);

        int databaseSizeBeforeUpdate = noteBookRepository.findAll().size();

        // Update the noteBook
        NoteBook updatedNoteBook = noteBookRepository.findById(noteBook.getId()).get();
        // Disconnect from session so that the updates on updatedNoteBook are not directly saved in db
        em.detach(updatedNoteBook);
        updatedNoteBook.name(UPDATED_NAME).handle(UPDATED_HANDLE);

        restNoteBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedNoteBook.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedNoteBook))
            )
            .andExpect(status().isOk());

        // Validate the NoteBook in the database
        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeUpdate);
        NoteBook testNoteBook = noteBookList.get(noteBookList.size() - 1);
        assertThat(testNoteBook.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testNoteBook.getHandle()).isEqualTo(UPDATED_HANDLE);
    }

    @Test
    @Transactional
    void putNonExistingNoteBook() throws Exception {
        int databaseSizeBeforeUpdate = noteBookRepository.findAll().size();
        noteBook.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNoteBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, noteBook.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(noteBook))
            )
            .andExpect(status().isBadRequest());

        // Validate the NoteBook in the database
        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNoteBook() throws Exception {
        int databaseSizeBeforeUpdate = noteBookRepository.findAll().size();
        noteBook.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoteBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(noteBook))
            )
            .andExpect(status().isBadRequest());

        // Validate the NoteBook in the database
        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNoteBook() throws Exception {
        int databaseSizeBeforeUpdate = noteBookRepository.findAll().size();
        noteBook.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoteBookMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteBook)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NoteBook in the database
        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNoteBookWithPatch() throws Exception {
        // Initialize the database
        noteBookRepository.saveAndFlush(noteBook);

        int databaseSizeBeforeUpdate = noteBookRepository.findAll().size();

        // Update the noteBook using partial update
        NoteBook partialUpdatedNoteBook = new NoteBook();
        partialUpdatedNoteBook.setId(noteBook.getId());

        partialUpdatedNoteBook.handle(UPDATED_HANDLE);

        restNoteBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNoteBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNoteBook))
            )
            .andExpect(status().isOk());

        // Validate the NoteBook in the database
        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeUpdate);
        NoteBook testNoteBook = noteBookList.get(noteBookList.size() - 1);
        assertThat(testNoteBook.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testNoteBook.getHandle()).isEqualTo(UPDATED_HANDLE);
    }

    @Test
    @Transactional
    void fullUpdateNoteBookWithPatch() throws Exception {
        // Initialize the database
        noteBookRepository.saveAndFlush(noteBook);

        int databaseSizeBeforeUpdate = noteBookRepository.findAll().size();

        // Update the noteBook using partial update
        NoteBook partialUpdatedNoteBook = new NoteBook();
        partialUpdatedNoteBook.setId(noteBook.getId());

        partialUpdatedNoteBook.name(UPDATED_NAME).handle(UPDATED_HANDLE);

        restNoteBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNoteBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNoteBook))
            )
            .andExpect(status().isOk());

        // Validate the NoteBook in the database
        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeUpdate);
        NoteBook testNoteBook = noteBookList.get(noteBookList.size() - 1);
        assertThat(testNoteBook.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testNoteBook.getHandle()).isEqualTo(UPDATED_HANDLE);
    }

    @Test
    @Transactional
    void patchNonExistingNoteBook() throws Exception {
        int databaseSizeBeforeUpdate = noteBookRepository.findAll().size();
        noteBook.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNoteBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, noteBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(noteBook))
            )
            .andExpect(status().isBadRequest());

        // Validate the NoteBook in the database
        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNoteBook() throws Exception {
        int databaseSizeBeforeUpdate = noteBookRepository.findAll().size();
        noteBook.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoteBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(noteBook))
            )
            .andExpect(status().isBadRequest());

        // Validate the NoteBook in the database
        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNoteBook() throws Exception {
        int databaseSizeBeforeUpdate = noteBookRepository.findAll().size();
        noteBook.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoteBookMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(noteBook)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NoteBook in the database
        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNoteBook() throws Exception {
        // Initialize the database
        noteBookRepository.saveAndFlush(noteBook);

        int databaseSizeBeforeDelete = noteBookRepository.findAll().size();

        // Delete the noteBook
        restNoteBookMockMvc
            .perform(delete(ENTITY_API_URL_ID, noteBook.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<NoteBook> noteBookList = noteBookRepository.findAll();
        assertThat(noteBookList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
