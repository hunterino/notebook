import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('NoteBook e2e test', () => {
  const noteBookPageUrl = '/note-book';
  const noteBookPageUrlPattern = new RegExp('/note-book(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const noteBookSample = { name: 'Automotive', handle: 'Designer' };

  let noteBook;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/note-books+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/note-books').as('postEntityRequest');
    cy.intercept('DELETE', '/api/note-books/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (noteBook) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/note-books/${noteBook.id}`,
      }).then(() => {
        noteBook = undefined;
      });
    }
  });

  it('NoteBooks menu should load NoteBooks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('note-book');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('NoteBook').should('exist');
    cy.url().should('match', noteBookPageUrlPattern);
  });

  describe('NoteBook page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(noteBookPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create NoteBook page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/note-book/new$'));
        cy.getEntityCreateUpdateHeading('NoteBook');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', noteBookPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/note-books',
          body: noteBookSample,
        }).then(({ body }) => {
          noteBook = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/note-books+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [noteBook],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(noteBookPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details NoteBook page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('noteBook');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', noteBookPageUrlPattern);
      });

      it('edit button click should load edit NoteBook page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('NoteBook');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', noteBookPageUrlPattern);
      });

      it('edit button click should load edit NoteBook page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('NoteBook');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', noteBookPageUrlPattern);
      });

      it('last delete button click should delete instance of NoteBook', () => {
        cy.intercept('GET', '/api/note-books/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('noteBook').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', noteBookPageUrlPattern);

        noteBook = undefined;
      });
    });
  });

  describe('new NoteBook page', () => {
    beforeEach(() => {
      cy.visit(`${noteBookPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('NoteBook');
    });

    it('should create an instance of NoteBook', () => {
      cy.get(`[data-cy="name"]`).type('programming').should('have.value', 'programming');

      cy.get(`[data-cy="handle"]`).type('Helena').should('have.value', 'Helena');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        noteBook = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', noteBookPageUrlPattern);
    });
  });
});
