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

describe('ContaminationEvent e2e test', () => {
  const contaminationEventPageUrl = '/contamination-event';
  const contaminationEventPageUrlPattern = new RegExp('/contamination-event(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const contaminationEventSample = {"detectedDate":"2026-02-22","type":"COBWEB","severity":"LOW","actionTaken":"INCREASED_VENTILATION"};

  let contaminationEvent;
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
      body: {"batchCode":"quickly darn cafe","startDate":"2026-02-22","endDate":"2026-02-21","currentPhase":"CONSOLIDATION","numberOfBags":9467,"substrateWeightKg":22826.1,"spawnWeightKg":26865.82,"targetYieldKg":28014.79,"actualTotalYieldKg":32508.38,"biologicalEfficiencyPercent":13854.04,"isContaminated":false,"isActive":false,"completionNote":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      batch = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/contamination-events+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/contamination-events').as('postEntityRequest');
    cy.intercept('DELETE', '/api/contamination-events/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/batches', {
      statusCode: 200,
      body: [batch],
    });

    cy.intercept('GET', '/api/phase-executions', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/rooms', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (contaminationEvent) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/contamination-events/${contaminationEvent.id}`,
      }).then(() => {
        contaminationEvent = undefined;
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

  it('ContaminationEvents menu should load ContaminationEvents page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('contamination-event');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ContaminationEvent').should('exist');
    cy.url().should('match', contaminationEventPageUrlPattern);
  });

  describe('ContaminationEvent page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(contaminationEventPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ContaminationEvent page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/contamination-event/new$'));
        cy.getEntityCreateUpdateHeading('ContaminationEvent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contaminationEventPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/contamination-events',
          body: {
            ...contaminationEventSample,
            batch: batch,
          },
        }).then(({ body }) => {
          contaminationEvent = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/contamination-events+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [contaminationEvent],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(contaminationEventPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(contaminationEventPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ContaminationEvent page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('contaminationEvent');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contaminationEventPageUrlPattern);
      });

      it('edit button click should load edit ContaminationEvent page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ContaminationEvent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contaminationEventPageUrlPattern);
      });

      it('edit button click should load edit ContaminationEvent page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ContaminationEvent');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contaminationEventPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of ContaminationEvent', () => {
        cy.intercept('GET', '/api/contamination-events/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('contaminationEvent').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', contaminationEventPageUrlPattern);

        contaminationEvent = undefined;
      });
    });
  });

  describe('new ContaminationEvent page', () => {
    beforeEach(() => {
      cy.visit(`${contaminationEventPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ContaminationEvent');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of ContaminationEvent', () => {
      cy.get(`[data-cy="detectedDate"]`).type('2026-02-21');
      cy.get(`[data-cy="detectedDate"]`).blur();
      cy.get(`[data-cy="detectedDate"]`).should('have.value', '2026-02-21');

      cy.get(`[data-cy="type"]`).select('OTHER');

      cy.get(`[data-cy="severity"]`).select('HIGH');

      cy.get(`[data-cy="affectedBags"]`).type('28226');
      cy.get(`[data-cy="affectedBags"]`).should('have.value', '28226');

      cy.get(`[data-cy="affectedPercentage"]`).type('21737.21');
      cy.get(`[data-cy="affectedPercentage"]`).should('have.value', '21737.21');

      cy.get(`[data-cy="actionTaken"]`).select('PEROXIDE_TREATMENT');

      cy.get(`[data-cy="resolvedDate"]`).type('2026-02-21');
      cy.get(`[data-cy="resolvedDate"]`).blur();
      cy.get(`[data-cy="resolvedDate"]`).should('have.value', '2026-02-21');

      cy.get(`[data-cy="lossKg"]`).type('20071.32');
      cy.get(`[data-cy="lossKg"]`).should('have.value', '20071.32');

      cy.get(`[data-cy="rootCauseAnalysis"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="rootCauseAnalysis"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="preventiveMeasures"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="preventiveMeasures"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="detectedBy"]`).type('juggernaut');
      cy.get(`[data-cy="detectedBy"]`).should('have.value', 'juggernaut');

      cy.get(`[data-cy="photosReference"]`).type('frightfully');
      cy.get(`[data-cy="photosReference"]`).should('have.value', 'frightfully');

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="batch"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        contaminationEvent = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', contaminationEventPageUrlPattern);
    });
  });
});
