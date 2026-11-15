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

describe('SensorReading e2e test', () => {
  const sensorReadingPageUrl = '/sensor-reading';
  const sensorReadingPageUrlPattern = new RegExp('/sensor-reading(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const sensorReadingSample = {"timestamp":"2026-02-22T02:20:59.275Z","value":782.56,"unit":"PPM_CO2"};

  let sensorReading;
  // let sensor;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/sensors',
      body: {"sensorCode":"outnumber following versus","sensorType":"AIRFLOW_MS","status":"CALIBRATING","installedDate":"2026-02-22","lastCalibrationDate":"2026-02-21","manufacturer":"unearth innocently","model":"for","note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      sensor = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/sensor-readings+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/sensor-readings').as('postEntityRequest');
    cy.intercept('DELETE', '/api/sensor-readings/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/sensors', {
      statusCode: 200,
      body: [sensor],
    });

  });
   */

  afterEach(() => {
    if (sensorReading) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/sensor-readings/${sensorReading.id}`,
      }).then(() => {
        sensorReading = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (sensor) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/sensors/${sensor.id}`,
      }).then(() => {
        sensor = undefined;
      });
    }
  });
   */

  it('SensorReadings menu should load SensorReadings page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('sensor-reading');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SensorReading').should('exist');
    cy.url().should('match', sensorReadingPageUrlPattern);
  });

  describe('SensorReading page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(sensorReadingPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SensorReading page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/sensor-reading/new$'));
        cy.getEntityCreateUpdateHeading('SensorReading');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sensorReadingPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/sensor-readings',
          body: {
            ...sensorReadingSample,
            sensor: sensor,
          },
        }).then(({ body }) => {
          sensorReading = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/sensor-readings+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [sensorReading],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(sensorReadingPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(sensorReadingPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details SensorReading page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('sensorReading');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sensorReadingPageUrlPattern);
      });

      it('edit button click should load edit SensorReading page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SensorReading');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sensorReadingPageUrlPattern);
      });

      it('edit button click should load edit SensorReading page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SensorReading');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sensorReadingPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of SensorReading', () => {
        cy.intercept('GET', '/api/sensor-readings/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('sensorReading').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sensorReadingPageUrlPattern);

        sensorReading = undefined;
      });
    });
  });

  describe('new SensorReading page', () => {
    beforeEach(() => {
      cy.visit(`${sensorReadingPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SensorReading');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of SensorReading', () => {
      cy.get(`[data-cy="timestamp"]`).type('2026-02-22T12:10');
      cy.get(`[data-cy="timestamp"]`).blur();
      cy.get(`[data-cy="timestamp"]`).should('have.value', '2026-02-22T12:10');

      cy.get(`[data-cy="value"]`).type('27985.06');
      cy.get(`[data-cy="value"]`).should('have.value', '27985.06');

      cy.get(`[data-cy="unit"]`).select('PPM_CO2');

      cy.get(`[data-cy="sensor"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        sensorReading = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', sensorReadingPageUrlPattern);
    });
  });
});
