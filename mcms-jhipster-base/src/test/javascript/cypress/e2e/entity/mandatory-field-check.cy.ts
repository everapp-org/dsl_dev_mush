import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('MandatoryFieldCheck e2e test', () => {
  const mandatoryFieldCheckPageUrl = '/mandatory-field-check';
  const mandatoryFieldCheckPageUrlPattern = new RegExp('/mandatory-field-check(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mandatoryFieldCheckSample = { fieldName: 'gullible tribe', isFilled: true, checkDate: '2026-02-21' };

  let mandatoryFieldCheck;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mandatory-field-checks+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mandatory-field-checks').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mandatory-field-checks/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mandatoryFieldCheck) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mandatory-field-checks/${mandatoryFieldCheck.id}`,
      }).then(() => {
        mandatoryFieldCheck = undefined;
      });
    }
  });

  it('MandatoryFieldChecks menu should load MandatoryFieldChecks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mandatory-field-check');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MandatoryFieldCheck').should('exist');
    cy.url().should('match', mandatoryFieldCheckPageUrlPattern);
  });

  describe('MandatoryFieldCheck page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mandatoryFieldCheckPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MandatoryFieldCheck page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mandatory-field-check/new$'));
        cy.getEntityCreateUpdateHeading('MandatoryFieldCheck');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mandatoryFieldCheckPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mandatory-field-checks',
          body: mandatoryFieldCheckSample,
        }).then(({ body }) => {
          mandatoryFieldCheck = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mandatory-field-checks+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [mandatoryFieldCheck],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mandatoryFieldCheckPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MandatoryFieldCheck page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mandatoryFieldCheck');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mandatoryFieldCheckPageUrlPattern);
      });

      it('edit button click should load edit MandatoryFieldCheck page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MandatoryFieldCheck');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mandatoryFieldCheckPageUrlPattern);
      });

      it('edit button click should load edit MandatoryFieldCheck page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MandatoryFieldCheck');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mandatoryFieldCheckPageUrlPattern);
      });

      it('last delete button click should delete instance of MandatoryFieldCheck', () => {
        cy.intercept('GET', '/api/mandatory-field-checks/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('mandatoryFieldCheck').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mandatoryFieldCheckPageUrlPattern);

        mandatoryFieldCheck = undefined;
      });
    });
  });

  describe('new MandatoryFieldCheck page', () => {
    beforeEach(() => {
      cy.visit(`${mandatoryFieldCheckPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MandatoryFieldCheck');
    });

    it('should create an instance of MandatoryFieldCheck', () => {
      cy.get(`[data-cy="fieldName"]`).type('factorize innocently');
      cy.get(`[data-cy="fieldName"]`).should('have.value', 'factorize innocently');

      cy.get(`[data-cy="isFilled"]`).should('not.be.checked');
      cy.get(`[data-cy="isFilled"]`).click();
      cy.get(`[data-cy="isFilled"]`).should('be.checked');

      cy.get(`[data-cy="checkDate"]`).type('2026-02-22');
      cy.get(`[data-cy="checkDate"]`).blur();
      cy.get(`[data-cy="checkDate"]`).should('have.value', '2026-02-22');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mandatoryFieldCheck = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mandatoryFieldCheckPageUrlPattern);
    });
  });
});
