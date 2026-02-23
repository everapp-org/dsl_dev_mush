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

describe('FlushCycle e2e test', () => {
  const flushCyclePageUrl = '/flush-cycle';
  const flushCyclePageUrlPattern = new RegExp('/flush-cycle(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const flushCycleSample = {"flushNumber":16515,"harvestStartDate":"2026-02-22","yieldKg":12349.82};

  let flushCycle;
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
      body: {"batchCode":"heroine","startDate":"2026-02-21","endDate":"2026-02-22","currentPhase":"INOCULATION","numberOfBags":25871,"substrateWeightKg":29921.04,"spawnWeightKg":51.48,"targetYieldKg":848.55,"actualTotalYieldKg":14376.69,"biologicalEfficiencyPercent":32038.34,"isContaminated":true,"isActive":false,"completionNote":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      batch = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/flush-cycles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/flush-cycles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/flush-cycles/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/batches', {
      statusCode: 200,
      body: [batch],
    });

  });
   */

  afterEach(() => {
    if (flushCycle) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/flush-cycles/${flushCycle.id}`,
      }).then(() => {
        flushCycle = undefined;
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

  it('FlushCycles menu should load FlushCycles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('flush-cycle');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('FlushCycle').should('exist');
    cy.url().should('match', flushCyclePageUrlPattern);
  });

  describe('FlushCycle page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(flushCyclePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create FlushCycle page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/flush-cycle/new$'));
        cy.getEntityCreateUpdateHeading('FlushCycle');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', flushCyclePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/flush-cycles',
          body: {
            ...flushCycleSample,
            batch: batch,
          },
        }).then(({ body }) => {
          flushCycle = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/flush-cycles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [flushCycle],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(flushCyclePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(flushCyclePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details FlushCycle page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('flushCycle');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', flushCyclePageUrlPattern);
      });

      it('edit button click should load edit FlushCycle page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FlushCycle');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', flushCyclePageUrlPattern);
      });

      it('edit button click should load edit FlushCycle page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FlushCycle');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', flushCyclePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of FlushCycle', () => {
        cy.intercept('GET', '/api/flush-cycles/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('flushCycle').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', flushCyclePageUrlPattern);

        flushCycle = undefined;
      });
    });
  });

  describe('new FlushCycle page', () => {
    beforeEach(() => {
      cy.visit(`${flushCyclePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('FlushCycle');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of FlushCycle', () => {
      cy.get(`[data-cy="flushNumber"]`).type('21361');
      cy.get(`[data-cy="flushNumber"]`).should('have.value', '21361');

      cy.get(`[data-cy="harvestStartDate"]`).type('2026-02-22');
      cy.get(`[data-cy="harvestStartDate"]`).blur();
      cy.get(`[data-cy="harvestStartDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="harvestEndDate"]`).type('2026-02-22');
      cy.get(`[data-cy="harvestEndDate"]`).blur();
      cy.get(`[data-cy="harvestEndDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="yieldKg"]`).type('20665.4');
      cy.get(`[data-cy="yieldKg"]`).should('have.value', '20665.4');

      cy.get(`[data-cy="yieldBagsHarvested"]`).type('12897');
      cy.get(`[data-cy="yieldBagsHarvested"]`).should('have.value', '12897');

      cy.get(`[data-cy="avgFruitBodyWeightG"]`).type('21840.63');
      cy.get(`[data-cy="avgFruitBodyWeightG"]`).should('have.value', '21840.63');

      cy.get(`[data-cy="rehydrationDone"]`).should('not.be.checked');
      cy.get(`[data-cy="rehydrationDone"]`).click();
      cy.get(`[data-cy="rehydrationDone"]`).should('be.checked');

      cy.get(`[data-cy="rehydrationDurationHours"]`).type('17971');
      cy.get(`[data-cy="rehydrationDurationHours"]`).should('have.value', '17971');

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="batch"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        flushCycle = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', flushCyclePageUrlPattern);
    });
  });
});
