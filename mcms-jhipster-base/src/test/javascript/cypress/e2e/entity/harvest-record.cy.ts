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

describe('HarvestRecord e2e test', () => {
  const harvestRecordPageUrl = '/harvest-record';
  const harvestRecordPageUrlPattern = new RegExp('/harvest-record(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const harvestRecordSample = {"harvestDate":"2026-02-21","weightKg":19674.04,"grade":"A_PREMIUM"};

  let harvestRecord;
  // let flushCycle;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/flush-cycles',
      body: {"flushNumber":13608,"harvestStartDate":"2026-02-21","harvestEndDate":"2026-02-21","yieldKg":194.29,"yieldBagsHarvested":29312,"avgFruitBodyWeightG":6245.1,"rehydrationDone":false,"rehydrationDurationHours":6891,"note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      flushCycle = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/harvest-records+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/harvest-records').as('postEntityRequest');
    cy.intercept('DELETE', '/api/harvest-records/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/flush-cycles', {
      statusCode: 200,
      body: [flushCycle],
    });

  });
   */

  afterEach(() => {
    if (harvestRecord) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/harvest-records/${harvestRecord.id}`,
      }).then(() => {
        harvestRecord = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
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
   */

  it('HarvestRecords menu should load HarvestRecords page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('harvest-record');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('HarvestRecord').should('exist');
    cy.url().should('match', harvestRecordPageUrlPattern);
  });

  describe('HarvestRecord page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(harvestRecordPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create HarvestRecord page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/harvest-record/new$'));
        cy.getEntityCreateUpdateHeading('HarvestRecord');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', harvestRecordPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/harvest-records',
          body: {
            ...harvestRecordSample,
            flushCycle: flushCycle,
          },
        }).then(({ body }) => {
          harvestRecord = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/harvest-records+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [harvestRecord],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(harvestRecordPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(harvestRecordPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details HarvestRecord page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('harvestRecord');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', harvestRecordPageUrlPattern);
      });

      it('edit button click should load edit HarvestRecord page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('HarvestRecord');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', harvestRecordPageUrlPattern);
      });

      it('edit button click should load edit HarvestRecord page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('HarvestRecord');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', harvestRecordPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of HarvestRecord', () => {
        cy.intercept('GET', '/api/harvest-records/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('harvestRecord').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', harvestRecordPageUrlPattern);

        harvestRecord = undefined;
      });
    });
  });

  describe('new HarvestRecord page', () => {
    beforeEach(() => {
      cy.visit(`${harvestRecordPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('HarvestRecord');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of HarvestRecord', () => {
      cy.get(`[data-cy="harvestDate"]`).type('2026-02-22');
      cy.get(`[data-cy="harvestDate"]`).blur();
      cy.get(`[data-cy="harvestDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="weightKg"]`).type('12120.44');
      cy.get(`[data-cy="weightKg"]`).should('have.value', '12120.44');

      cy.get(`[data-cy="grade"]`).select('C_INDUSTRIAL');

      cy.get(`[data-cy="pickerName"]`).type('thunderbolt');
      cy.get(`[data-cy="pickerName"]`).should('have.value', 'thunderbolt');

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="flushCycle"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        harvestRecord = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', harvestRecordPageUrlPattern);
    });
  });
});
