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

describe('Sensor e2e test', () => {
  const sensorPageUrl = '/sensor';
  const sensorPageUrlPattern = new RegExp('/sensor(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const sensorSample = { sensorCode: 'bitterly abseil sans', sensorType: 'CELSIUS', status: 'ACTIVE' };

  let sensor;
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
        name: 'carefully far bog',
        code: 'why outside',
        roomType: 'WORK_ZONE',
        status: 'INACTIVE',
        capacityBags: 32392,
        currentOccupancy: 22681,
        areaSqM: 4864.82,
        hasHVAC: false,
        hasMisting: false,
        lastDisinfectionDate: '2026-02-22',
        note: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
      },
    }).then(({ body }) => {
      room = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/sensors+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/sensors').as('postEntityRequest');
    cy.intercept('DELETE', '/api/sensors/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/rooms', {
      statusCode: 200,
      body: [room],
    });
  });

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

  it('Sensors menu should load Sensors page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('sensor');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Sensor').should('exist');
    cy.url().should('match', sensorPageUrlPattern);
  });

  describe('Sensor page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(sensorPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Sensor page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/sensor/new$'));
        cy.getEntityCreateUpdateHeading('Sensor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sensorPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/sensors',
          body: {
            ...sensorSample,
            room,
          },
        }).then(({ body }) => {
          sensor = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/sensors+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [sensor],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(sensorPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Sensor page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('sensor');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sensorPageUrlPattern);
      });

      it('edit button click should load edit Sensor page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Sensor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sensorPageUrlPattern);
      });

      it('edit button click should load edit Sensor page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Sensor');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sensorPageUrlPattern);
      });

      it('last delete button click should delete instance of Sensor', () => {
        cy.intercept('GET', '/api/sensors/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('sensor').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sensorPageUrlPattern);

        sensor = undefined;
      });
    });
  });

  describe('new Sensor page', () => {
    beforeEach(() => {
      cy.visit(`${sensorPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Sensor');
    });

    it('should create an instance of Sensor', () => {
      cy.get(`[data-cy="sensorCode"]`).type('so');
      cy.get(`[data-cy="sensorCode"]`).should('have.value', 'so');

      cy.get(`[data-cy="sensorType"]`).select('CELSIUS');

      cy.get(`[data-cy="status"]`).select('CALIBRATING');

      cy.get(`[data-cy="installedDate"]`).type('2026-02-22');
      cy.get(`[data-cy="installedDate"]`).blur();
      cy.get(`[data-cy="installedDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="lastCalibrationDate"]`).type('2026-02-22');
      cy.get(`[data-cy="lastCalibrationDate"]`).blur();
      cy.get(`[data-cy="lastCalibrationDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="manufacturer"]`).type('over idolized');
      cy.get(`[data-cy="manufacturer"]`).should('have.value', 'over idolized');

      cy.get(`[data-cy="model"]`).type('very');
      cy.get(`[data-cy="model"]`).should('have.value', 'very');

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="room"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        sensor = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', sensorPageUrlPattern);
    });
  });
});
