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

describe('PhaseExecution e2e test', () => {
  const phaseExecutionPageUrl = '/phase-execution';
  const phaseExecutionPageUrlPattern = new RegExp('/phase-execution(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const phaseExecutionSample = {"phase":"FRUITING_BODY_GROWTH","sequenceOrder":17485,"startDate":"2026-02-22"};

  let phaseExecution;
  // let batch;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/batches',
      body: {"batchCode":"indeed","startDate":"2026-02-22","endDate":"2026-02-21","currentPhase":"COMPLETED","numberOfBags":20026,"substrateWeightKg":4763.17,"spawnWeightKg":5974.45,"targetYieldKg":20345.45,"actualTotalYieldKg":22939.04,"biologicalEfficiencyPercent":12029.01,"isContaminated":true,"isActive":false,"completionNote":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      batch = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/phase-executions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/phase-executions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/phase-executions/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/batches', {
      statusCode: 200,
      body: [batch],
    });

    cy.intercept('GET', '/api/rooms', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (phaseExecution) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/phase-executions/${phaseExecution.id}`,
      }).then(() => {
        phaseExecution = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (batch) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/batches/${batch.id}`,
      }).then(() => {
        batch = undefined;
      });
    }
  });
   */

  it('PhaseExecutions menu should load PhaseExecutions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('phase-execution');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PhaseExecution').should('exist');
    cy.url().should('match', phaseExecutionPageUrlPattern);
  });

  describe('PhaseExecution page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(phaseExecutionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PhaseExecution page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/phase-execution/new$'));
        cy.getEntityCreateUpdateHeading('PhaseExecution');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', phaseExecutionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/phase-executions',
          body: {
            ...phaseExecutionSample,
            batch: batch,
          },
        }).then(({ body }) => {
          phaseExecution = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/phase-executions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [phaseExecution],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(phaseExecutionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(phaseExecutionPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details PhaseExecution page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('phaseExecution');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', phaseExecutionPageUrlPattern);
      });

      it('edit button click should load edit PhaseExecution page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PhaseExecution');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', phaseExecutionPageUrlPattern);
      });

      it('edit button click should load edit PhaseExecution page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PhaseExecution');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', phaseExecutionPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of PhaseExecution', () => {
        cy.intercept('GET', '/api/phase-executions/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('phaseExecution').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', phaseExecutionPageUrlPattern);

        phaseExecution = undefined;
      });
    });
  });

  describe('new PhaseExecution page', () => {
    beforeEach(() => {
      cy.visit(`${phaseExecutionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PhaseExecution');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of PhaseExecution', () => {
      cy.get(`[data-cy="phase"]`).select('REHYDRATION_PAUSE');

      cy.get(`[data-cy="sequenceOrder"]`).type('28313');
      cy.get(`[data-cy="sequenceOrder"]`).should('have.value', '28313');

      cy.get(`[data-cy="startDate"]`).type('2026-02-21');
      cy.get(`[data-cy="startDate"]`).blur();
      cy.get(`[data-cy="startDate"]`).should('have.value', '2026-02-21');

      cy.get(`[data-cy="endDate"]`).type('2026-02-21');
      cy.get(`[data-cy="endDate"]`).blur();
      cy.get(`[data-cy="endDate"]`).should('have.value', '2026-02-21');

      cy.get(`[data-cy="plannedDurationDays"]`).type('29452');
      cy.get(`[data-cy="plannedDurationDays"]`).should('have.value', '29452');

      cy.get(`[data-cy="actualDurationDays"]`).type('16681');
      cy.get(`[data-cy="actualDurationDays"]`).should('have.value', '16681');

      cy.get(`[data-cy="responsiblePerson"]`).type('insignificant');
      cy.get(`[data-cy="responsiblePerson"]`).should('have.value', 'insignificant');

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="batch"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        phaseExecution = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', phaseExecutionPageUrlPattern);
    });
  });
});
