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

describe('EnvironmentalAlert e2e test', () => {
  const environmentalAlertPageUrl = '/environmental-alert';
  const environmentalAlertPageUrlPattern = new RegExp('/environmental-alert(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const environmentalAlertSample = {
    alertTime: '2026-02-22T05:10:51.246Z',
    severity: 'INFO',
    parameter: 'AIRFLOW_MS',
    actualValue: 22949.22,
    thresholdValue: 30762.42,
    message: 'gown',
    acknowledged: false,
  };

  let environmentalAlert;
  let room;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/rooms',
      body: {
        name: 'phooey',
        code: 'ah perfection',
        roomType: 'WORK_ZONE',
        status: 'INACTIVE',
        capacityBags: 19721,
        currentOccupancy: 40,
        areaSqM: 20053.81,
        hasHVAC: true,
        hasMisting: false,
        lastDisinfectionDate: '2026-02-22',
        note: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
      },
    }).then(({ body }) => {
      room = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/environmental-alerts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/environmental-alerts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/environmental-alerts/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/rooms', {
      statusCode: 200,
      body: [room],
    });

    cy.intercept('GET', '/api/sensors', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/batches', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (environmentalAlert) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/environmental-alerts/${environmentalAlert.id}`,
      }).then(() => {
        environmentalAlert = undefined;
      });
    }
  });

  afterEach(() => {
    if (room) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/rooms/${room.id}`,
      }).then(() => {
        room = undefined;
      });
    }
  });

  it('EnvironmentalAlerts menu should load EnvironmentalAlerts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('environmental-alert');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('EnvironmentalAlert').should('exist');
    cy.url().should('match', environmentalAlertPageUrlPattern);
  });

  describe('EnvironmentalAlert page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(environmentalAlertPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create EnvironmentalAlert page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/environmental-alert/new$'));
        cy.getEntityCreateUpdateHeading('EnvironmentalAlert');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentalAlertPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/environmental-alerts',
          body: {
            ...environmentalAlertSample,
            room,
          },
        }).then(({ body }) => {
          environmentalAlert = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/environmental-alerts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [environmentalAlert],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(environmentalAlertPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details EnvironmentalAlert page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('environmentalAlert');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentalAlertPageUrlPattern);
      });

      it('edit button click should load edit EnvironmentalAlert page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EnvironmentalAlert');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentalAlertPageUrlPattern);
      });

      it('edit button click should load edit EnvironmentalAlert page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EnvironmentalAlert');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentalAlertPageUrlPattern);
      });

      it('last delete button click should delete instance of EnvironmentalAlert', () => {
        cy.intercept('GET', '/api/environmental-alerts/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('environmentalAlert').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentalAlertPageUrlPattern);

        environmentalAlert = undefined;
      });
    });
  });

  describe('new EnvironmentalAlert page', () => {
    beforeEach(() => {
      cy.visit(`${environmentalAlertPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('EnvironmentalAlert');
    });

    it('should create an instance of EnvironmentalAlert', () => {
      cy.get(`[data-cy="alertTime"]`).type('2026-02-22T02:46');
      cy.get(`[data-cy="alertTime"]`).blur();
      cy.get(`[data-cy="alertTime"]`).should('have.value', '2026-02-22T02:46');

      cy.get(`[data-cy="severity"]`).select('INFO');

      cy.get(`[data-cy="parameter"]`).select('AIRFLOW_MS');

      cy.get(`[data-cy="actualValue"]`).type('10232.97');
      cy.get(`[data-cy="actualValue"]`).should('have.value', '10232.97');

      cy.get(`[data-cy="thresholdValue"]`).type('8434.13');
      cy.get(`[data-cy="thresholdValue"]`).should('have.value', '8434.13');

      cy.get(`[data-cy="message"]`).type('what violent ouch');
      cy.get(`[data-cy="message"]`).should('have.value', 'what violent ouch');

      cy.get(`[data-cy="acknowledged"]`).should('not.be.checked');
      cy.get(`[data-cy="acknowledged"]`).click();
      cy.get(`[data-cy="acknowledged"]`).should('be.checked');

      cy.get(`[data-cy="acknowledgedBy"]`).type('golden boohoo who');
      cy.get(`[data-cy="acknowledgedBy"]`).should('have.value', 'golden boohoo who');

      cy.get(`[data-cy="acknowledgedAt"]`).type('2026-02-21T20:32');
      cy.get(`[data-cy="acknowledgedAt"]`).blur();
      cy.get(`[data-cy="acknowledgedAt"]`).should('have.value', '2026-02-21T20:32');

      cy.get(`[data-cy="resolutionNote"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="resolutionNote"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="room"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        environmentalAlert = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', environmentalAlertPageUrlPattern);
    });
  });
});
