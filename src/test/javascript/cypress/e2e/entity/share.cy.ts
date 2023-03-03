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

describe('Share e2e test', () => {
  const sharePageUrl = '/share';
  const sharePageUrlPattern = new RegExp('/share(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const shareSample = { invite: 'array installation Inverse' };

  let share;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/shares+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/shares').as('postEntityRequest');
    cy.intercept('DELETE', '/api/shares/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (share) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/shares/${share.id}`,
      }).then(() => {
        share = undefined;
      });
    }
  });

  it('Shares menu should load Shares page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('share');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Share').should('exist');
    cy.url().should('match', sharePageUrlPattern);
  });

  describe('Share page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(sharePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Share page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/share/new$'));
        cy.getEntityCreateUpdateHeading('Share');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', sharePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/shares',
          body: shareSample,
        }).then(({ body }) => {
          share = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/shares+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [share],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(sharePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Share page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('share');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', sharePageUrlPattern);
      });

      it('edit button click should load edit Share page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Share');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', sharePageUrlPattern);
      });

      it('edit button click should load edit Share page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Share');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', sharePageUrlPattern);
      });

      it('last delete button click should delete instance of Share', () => {
        cy.intercept('GET', '/api/shares/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('share').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', sharePageUrlPattern);

        share = undefined;
      });
    });
  });

  describe('new Share page', () => {
    beforeEach(() => {
      cy.visit(`${sharePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Share');
    });

    it('should create an instance of Share', () => {
      cy.get(`[data-cy="invite"]`).type('Response').should('have.value', 'Response');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        share = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', sharePageUrlPattern);
    });
  });
});
