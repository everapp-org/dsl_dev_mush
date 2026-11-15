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

describe('EnvironmentalTarget e2e test', () => {
  const environmentalTargetPageUrl = '/environmental-target';
  const environmentalTargetPageUrlPattern = new RegExp('/environmental-target(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const environmentalTargetSample = {
    phase: 'CONSOLIDATION',
    tempMinC: 31158.87,
    tempMaxC: 5078.1,
    humidityMinPercent: 182.63,
    humidityMaxPercent: 18359.54,
  };

  let environmentalTarget;
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
        name: 'readies',
        code: 'sheathe zowie gah',
        roomType: 'COLD_STORAGE',
        status: 'DISINFECTION',
        capacityBags: 15154,
        currentOccupancy: 13229,
        areaSqM: 16531.05,
        hasHVAC: true,
        hasMisting: true,
        lastDisinfectionDate: '2026-02-21',
        note: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
      },
    }).then(({ body }) => {
      room = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/environmental-targets+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/environmental-targets').as('postEntityRequest');
    cy.intercept('DELETE', '/api/environmental-targets/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/rooms', {
      statusCode: 200,
      body: [room],
    });
  });

  afterEach(() => {
    if (environmentalTarget) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/environmental-targets/${environmentalTarget.id}`,
      }).then(() => {
        environmentalTarget = undefined;
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

  it('EnvironmentalTargets menu should load EnvironmentalTargets page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('environmental-target');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('EnvironmentalTarget').should('exist');
    cy.url().should('match', environmentalTargetPageUrlPattern);
  });

  describe('EnvironmentalTarget page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(environmentalTargetPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create EnvironmentalTarget page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/environmental-target/new$'));
        cy.getEntityCreateUpdateHeading('EnvironmentalTarget');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentalTargetPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/environmental-targets',
          body: {
            ...environmentalTargetSample,
            room,
          },
        }).then(({ body }) => {
          environmentalTarget = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/environmental-targets+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [environmentalTarget],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(environmentalTargetPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details EnvironmentalTarget page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('environmentalTarget');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentalTargetPageUrlPattern);
      });

      it('edit button click should load edit EnvironmentalTarget page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EnvironmentalTarget');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentalTargetPageUrlPattern);
      });

      it('edit button click should load edit EnvironmentalTarget page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EnvironmentalTarget');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentalTargetPageUrlPattern);
      });

      it('last delete button click should delete instance of EnvironmentalTarget', () => {
        cy.intercept('GET', '/api/environmental-targets/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('environmentalTarget').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', environmentalTargetPageUrlPattern);

        environmentalTarget = undefined;
      });
    });
  });

  describe('new EnvironmentalTarget page', () => {
    beforeEach(() => {
      cy.visit(`${environmentalTargetPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('EnvironmentalTarget');
    });

    it('should create an instance of EnvironmentalTarget', () => {
      cy.get(`[data-cy="phase"]`).select('FRUITING_BODY_GROWTH');

      cy.get(`[data-cy="tempMinC"]`).type('9510.77');
      cy.get(`[data-cy="tempMinC"]`).should('have.value', '9510.77');

      cy.get(`[data-cy="tempMaxC"]`).type('31419.59');
      cy.get(`[data-cy="tempMaxC"]`).should('have.value', '31419.59');

      cy.get(`[data-cy="humidityMinPercent"]`).type('12383.2');
      cy.get(`[data-cy="humidityMinPercent"]`).should('have.value', '12383.2');

      cy.get(`[data-cy="humidityMaxPercent"]`).type('1266.48');
      cy.get(`[data-cy="humidityMaxPercent"]`).should('have.value', '1266.48');

      cy.get(`[data-cy="co2MaxPpm"]`).type('268');
      cy.get(`[data-cy="co2MaxPpm"]`).should('have.value', '268');

      cy.get(`[data-cy="lightLux"]`).type('1895');
      cy.get(`[data-cy="lightLux"]`).should('have.value', '1895');

      cy.get(`[data-cy="freshAirExchangesPerHour"]`).type('15454');
      cy.get(`[data-cy="freshAirExchangesPerHour"]`).should('have.value', '15454');

      cy.get(`[data-cy="room"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        environmentalTarget = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', environmentalTargetPageUrlPattern);
    });
  });
});
