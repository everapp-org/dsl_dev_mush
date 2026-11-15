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

describe('CostRecord e2e test', () => {
  const costRecordPageUrl = '/cost-record';
  const costRecordPageUrlPattern = new RegExp('/cost-record(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const costRecordSample = {"recordDate":"2026-02-22","category":"ENERGY","description":"gah rightfully","amount":8346.65,"currency":"quixotic recovery drat"};

  let costRecord;
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
      body: {"batchCode":"graffiti","startDate":"2026-02-21","endDate":"2026-02-22","currentPhase":"PRIMORDIA","numberOfBags":8699,"substrateWeightKg":11366.96,"spawnWeightKg":27644.91,"targetYieldKg":17327.79,"actualTotalYieldKg":22607.66,"biologicalEfficiencyPercent":8943.36,"isContaminated":true,"isActive":false,"completionNote":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      batch = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/cost-records+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/cost-records').as('postEntityRequest');
    cy.intercept('DELETE', '/api/cost-records/*').as('deleteEntityRequest');
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
    if (costRecord) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/cost-records/${costRecord.id}`,
      }).then(() => {
        costRecord = undefined;
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

  it('CostRecords menu should load CostRecords page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('cost-record');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CostRecord').should('exist');
    cy.url().should('match', costRecordPageUrlPattern);
  });

  describe('CostRecord page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(costRecordPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CostRecord page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/cost-record/new$'));
        cy.getEntityCreateUpdateHeading('CostRecord');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', costRecordPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/cost-records',
          body: {
            ...costRecordSample,
            batch: batch,
          },
        }).then(({ body }) => {
          costRecord = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/cost-records+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [costRecord],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(costRecordPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(costRecordPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details CostRecord page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('costRecord');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', costRecordPageUrlPattern);
      });

      it('edit button click should load edit CostRecord page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CostRecord');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', costRecordPageUrlPattern);
      });

      it('edit button click should load edit CostRecord page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CostRecord');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', costRecordPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of CostRecord', () => {
        cy.intercept('GET', '/api/cost-records/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('costRecord').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', costRecordPageUrlPattern);

        costRecord = undefined;
      });
    });
  });

  describe('new CostRecord page', () => {
    beforeEach(() => {
      cy.visit(`${costRecordPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CostRecord');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of CostRecord', () => {
      cy.get(`[data-cy="recordDate"]`).type('2026-02-22');
      cy.get(`[data-cy="recordDate"]`).blur();
      cy.get(`[data-cy="recordDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="category"]`).select('PACKAGING');

      cy.get(`[data-cy="description"]`).type('nor');
      cy.get(`[data-cy="description"]`).should('have.value', 'nor');

      cy.get(`[data-cy="amount"]`).type('11369.36');
      cy.get(`[data-cy="amount"]`).should('have.value', '11369.36');

      cy.get(`[data-cy="currency"]`).type('while demobilise');
      cy.get(`[data-cy="currency"]`).should('have.value', 'while demobilise');

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="batch"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        costRecord = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', costRecordPageUrlPattern);
    });
  });
});
