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

describe('Note e2e test', () => {
  const notePageUrl = '/note';
  const notePageUrlPattern = new RegExp('/note(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const noteSample = { title: 'Shoes', content: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=', date: '2023-03-01T20:37:48.016Z' };

  let note;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/notes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/notes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/notes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (note) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/notes/${note.id}`,
      }).then(() => {
        note = undefined;
      });
    }
  });

  it('Notes menu should load Notes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('note');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Note').should('exist');
    cy.url().should('match', notePageUrlPattern);
  });

  describe('Note page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(notePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Note page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/note/new$'));
        cy.getEntityCreateUpdateHeading('Note');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/notes',
          body: noteSample,
        }).then(({ body }) => {
          note = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/notes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/notes?page=0&size=20>; rel="last",<http://localhost/api/notes?page=0&size=20>; rel="first"',
              },
              body: [note],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(notePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Note page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('note');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notePageUrlPattern);
      });

      it('edit button click should load edit Note page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Note');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notePageUrlPattern);
      });

      it('edit button click should load edit Note page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Note');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notePageUrlPattern);
      });

      it('last delete button click should delete instance of Note', () => {
        cy.intercept('GET', '/api/notes/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('note').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', notePageUrlPattern);

        note = undefined;
      });
    });
  });

  describe('new Note page', () => {
    beforeEach(() => {
      cy.visit(`${notePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Note');
    });

    it('should create an instance of Note', () => {
      cy.get(`[data-cy="title"]`).type('front-end Tuna').should('have.value', 'front-end Tuna');

      cy.get(`[data-cy="content"]`)
        .type('../fake-data/blob/hipster.txt')
        .invoke('val')
        .should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="date"]`).type('2023-03-01T21:23').blur().should('have.value', '2023-03-01T21:23');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        note = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', notePageUrlPattern);
    });
  });
});
