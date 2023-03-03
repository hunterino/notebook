package com.twohtwo.notebook.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Share.
 */
@Entity
@Table(name = "share")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Share implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "invite", nullable = false)
    private String invite;

    @ManyToOne
    private User author;

    @ManyToOne
    private User withUser;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "notebook" }, allowSetters = true)
    private Note sharing;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Share id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvite() {
        return this.invite;
    }

    public Share invite(String invite) {
        this.setInvite(invite);
        return this;
    }

    public void setInvite(String invite) {
        this.invite = invite;
    }

    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    public Share author(User user) {
        this.setAuthor(user);
        return this;
    }

    public User getWithUser() {
        return this.withUser;
    }

    public void setWithUser(User user) {
        this.withUser = user;
    }

    public Share withUser(User user) {
        this.setWithUser(user);
        return this;
    }

    public Note getSharing() {
        return this.sharing;
    }

    public void setSharing(Note note) {
        this.sharing = note;
    }

    public Share sharing(Note note) {
        this.setSharing(note);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Share)) {
            return false;
        }
        return id != null && id.equals(((Share) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Share{" +
            "id=" + getId() +
            ", invite='" + getInvite() + "'" +
            "}";
    }
}
