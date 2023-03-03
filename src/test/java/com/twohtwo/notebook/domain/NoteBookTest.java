package com.twohtwo.notebook.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.twohtwo.notebook.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NoteBookTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NoteBook.class);
        NoteBook noteBook1 = new NoteBook();
        noteBook1.setId(1L);
        NoteBook noteBook2 = new NoteBook();
        noteBook2.setId(noteBook1.getId());
        assertThat(noteBook1).isEqualTo(noteBook2);
        noteBook2.setId(2L);
        assertThat(noteBook1).isNotEqualTo(noteBook2);
        noteBook1.setId(null);
        assertThat(noteBook1).isNotEqualTo(noteBook2);
    }
}
