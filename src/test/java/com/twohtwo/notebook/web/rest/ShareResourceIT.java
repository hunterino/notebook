package com.twohtwo.notebook.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.twohtwo.notebook.IntegrationTest;
import com.twohtwo.notebook.domain.Share;
import com.twohtwo.notebook.repository.ShareRepository;
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
 * Integration tests for the {@link ShareResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ShareResourceIT {

    private static final String DEFAULT_INVITE = "AAAAAAAAAA";
    private static final String UPDATED_INVITE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/shares";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ShareRepository shareRepository;

    @Mock
    private ShareRepository shareRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShareMockMvc;

    private Share share;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Share createEntity(EntityManager em) {
        Share share = new Share().invite(DEFAULT_INVITE);
        return share;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Share createUpdatedEntity(EntityManager em) {
        Share share = new Share().invite(UPDATED_INVITE);
        return share;
    }

    @BeforeEach
    public void initTest() {
        share = createEntity(em);
    }

    @Test
    @Transactional
    void createShare() throws Exception {
        int databaseSizeBeforeCreate = shareRepository.findAll().size();
        // Create the Share
        restShareMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(share)))
            .andExpect(status().isCreated());

        // Validate the Share in the database
        List<Share> shareList = shareRepository.findAll();
        assertThat(shareList).hasSize(databaseSizeBeforeCreate + 1);
        Share testShare = shareList.get(shareList.size() - 1);
        assertThat(testShare.getInvite()).isEqualTo(DEFAULT_INVITE);
    }

    @Test
    @Transactional
    void createShareWithExistingId() throws Exception {
        // Create the Share with an existing ID
        share.setId(1L);

        int databaseSizeBeforeCreate = shareRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShareMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(share)))
            .andExpect(status().isBadRequest());

        // Validate the Share in the database
        List<Share> shareList = shareRepository.findAll();
        assertThat(shareList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkInviteIsRequired() throws Exception {
        int databaseSizeBeforeTest = shareRepository.findAll().size();
        // set the field null
        share.setInvite(null);

        // Create the Share, which fails.

        restShareMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(share)))
            .andExpect(status().isBadRequest());

        List<Share> shareList = shareRepository.findAll();
        assertThat(shareList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllShares() throws Exception {
        // Initialize the database
        shareRepository.saveAndFlush(share);

        // Get all the shareList
        restShareMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(share.getId().intValue())))
            .andExpect(jsonPath("$.[*].invite").value(hasItem(DEFAULT_INVITE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSharesWithEagerRelationshipsIsEnabled() throws Exception {
        when(shareRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restShareMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(shareRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSharesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(shareRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restShareMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(shareRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getShare() throws Exception {
        // Initialize the database
        shareRepository.saveAndFlush(share);

        // Get the share
        restShareMockMvc
            .perform(get(ENTITY_API_URL_ID, share.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(share.getId().intValue()))
            .andExpect(jsonPath("$.invite").value(DEFAULT_INVITE));
    }

    @Test
    @Transactional
    void getNonExistingShare() throws Exception {
        // Get the share
        restShareMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShare() throws Exception {
        // Initialize the database
        shareRepository.saveAndFlush(share);

        int databaseSizeBeforeUpdate = shareRepository.findAll().size();

        // Update the share
        Share updatedShare = shareRepository.findById(share.getId()).get();
        // Disconnect from session so that the updates on updatedShare are not directly saved in db
        em.detach(updatedShare);
        updatedShare.invite(UPDATED_INVITE);

        restShareMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedShare.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedShare))
            )
            .andExpect(status().isOk());

        // Validate the Share in the database
        List<Share> shareList = shareRepository.findAll();
        assertThat(shareList).hasSize(databaseSizeBeforeUpdate);
        Share testShare = shareList.get(shareList.size() - 1);
        assertThat(testShare.getInvite()).isEqualTo(UPDATED_INVITE);
    }

    @Test
    @Transactional
    void putNonExistingShare() throws Exception {
        int databaseSizeBeforeUpdate = shareRepository.findAll().size();
        share.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShareMockMvc
            .perform(
                put(ENTITY_API_URL_ID, share.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(share))
            )
            .andExpect(status().isBadRequest());

        // Validate the Share in the database
        List<Share> shareList = shareRepository.findAll();
        assertThat(shareList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShare() throws Exception {
        int databaseSizeBeforeUpdate = shareRepository.findAll().size();
        share.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShareMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(share))
            )
            .andExpect(status().isBadRequest());

        // Validate the Share in the database
        List<Share> shareList = shareRepository.findAll();
        assertThat(shareList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShare() throws Exception {
        int databaseSizeBeforeUpdate = shareRepository.findAll().size();
        share.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShareMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(share)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Share in the database
        List<Share> shareList = shareRepository.findAll();
        assertThat(shareList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShareWithPatch() throws Exception {
        // Initialize the database
        shareRepository.saveAndFlush(share);

        int databaseSizeBeforeUpdate = shareRepository.findAll().size();

        // Update the share using partial update
        Share partialUpdatedShare = new Share();
        partialUpdatedShare.setId(share.getId());

        restShareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShare.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShare))
            )
            .andExpect(status().isOk());

        // Validate the Share in the database
        List<Share> shareList = shareRepository.findAll();
        assertThat(shareList).hasSize(databaseSizeBeforeUpdate);
        Share testShare = shareList.get(shareList.size() - 1);
        assertThat(testShare.getInvite()).isEqualTo(DEFAULT_INVITE);
    }

    @Test
    @Transactional
    void fullUpdateShareWithPatch() throws Exception {
        // Initialize the database
        shareRepository.saveAndFlush(share);

        int databaseSizeBeforeUpdate = shareRepository.findAll().size();

        // Update the share using partial update
        Share partialUpdatedShare = new Share();
        partialUpdatedShare.setId(share.getId());

        partialUpdatedShare.invite(UPDATED_INVITE);

        restShareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShare.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShare))
            )
            .andExpect(status().isOk());

        // Validate the Share in the database
        List<Share> shareList = shareRepository.findAll();
        assertThat(shareList).hasSize(databaseSizeBeforeUpdate);
        Share testShare = shareList.get(shareList.size() - 1);
        assertThat(testShare.getInvite()).isEqualTo(UPDATED_INVITE);
    }

    @Test
    @Transactional
    void patchNonExistingShare() throws Exception {
        int databaseSizeBeforeUpdate = shareRepository.findAll().size();
        share.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, share.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(share))
            )
            .andExpect(status().isBadRequest());

        // Validate the Share in the database
        List<Share> shareList = shareRepository.findAll();
        assertThat(shareList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShare() throws Exception {
        int databaseSizeBeforeUpdate = shareRepository.findAll().size();
        share.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(share))
            )
            .andExpect(status().isBadRequest());

        // Validate the Share in the database
        List<Share> shareList = shareRepository.findAll();
        assertThat(shareList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShare() throws Exception {
        int databaseSizeBeforeUpdate = shareRepository.findAll().size();
        share.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShareMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(share)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Share in the database
        List<Share> shareList = shareRepository.findAll();
        assertThat(shareList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShare() throws Exception {
        // Initialize the database
        shareRepository.saveAndFlush(share);

        int databaseSizeBeforeDelete = shareRepository.findAll().size();

        // Delete the share
        restShareMockMvc
            .perform(delete(ENTITY_API_URL_ID, share.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Share> shareList = shareRepository.findAll();
        assertThat(shareList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
