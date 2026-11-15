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

describe('Batch e2e test', () => {
  const batchPageUrl = '/batch';
  const batchPageUrlPattern = new RegExp('/batch(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const batchSample = { batchCode: 'qua blah', startDate: '2026-02-21', currentPhase: 'FRUITING_TRIGGER', isActive: false };

  let batch;
  let strain;
  let substrateRecipe;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/strains',
      body: {
        name: 'tail zowie sauerkraut',
        species: 'unless consequently',
        variety: 'rightfully once',
        optimalTempMinC: 15172.87,
        optimalTempMaxC: 1997.05,
        optimalHumidityMin: 7345.35,
        optimalHumidityMax: 3692.01,
        optimalCO2MaxPpm: 350,
        colonizationDaysMin: 7904,
        colonizationDaysMax: 4762,
        expectedYieldPercent: 15757.15,
        shelfLifeDays: 17629,
        note: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        active: false,
      },
    }).then(({ body }) => {
      strain = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/substrate-recipes',
      body: {
        name: 'whereas',
        version: 'whether kowtow hm',
        baseType: 'BARLEY_STRAW',
        compositionDetail: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        sterilizationMethod: 'cripple where',
        moistureTargetPercent: 18364.83,
        phTarget: 24828.43,
        supplementNotes: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        active: false,
      },
    }).then(({ body }) => {
      substrateRecipe = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/batches+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/batches').as('postEntityRequest');
    cy.intercept('DELETE', '/api/batches/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/strains', {
      statusCode: 200,
      body: [strain],
    });

    cy.intercept('GET', '/api/substrate-recipes', {
      statusCode: 200,
      body: [substrateRecipe],
    });
  });

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

  afterEach(() => {
    if (strain) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/strains/${strain.id}`,
      }).then(() => {
        strain = undefined;
      });
    }
    if (substrateRecipe) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/substrate-recipes/${substrateRecipe.id}`,
      }).then(() => {
        substrateRecipe = undefined;
      });
    }
  });

  it('Batches menu should load Batches page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('batch');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Batch').should('exist');
    cy.url().should('match', batchPageUrlPattern);
  });

  describe('Batch page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(batchPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Batch page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/batch/new$'));
        cy.getEntityCreateUpdateHeading('Batch');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', batchPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/batches',
          body: {
            ...batchSample,
            strain,
            recipe: substrateRecipe,
          },
        }).then(({ body }) => {
          batch = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/batches+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [batch],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(batchPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Batch page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('batch');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', batchPageUrlPattern);
      });

      it('edit button click should load edit Batch page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Batch');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', batchPageUrlPattern);
      });

      it('edit button click should load edit Batch page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Batch');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', batchPageUrlPattern);
      });

      it('last delete button click should delete instance of Batch', () => {
        cy.intercept('GET', '/api/batches/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('batch').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', batchPageUrlPattern);

        batch = undefined;
      });
    });
  });

  describe('new Batch page', () => {
    beforeEach(() => {
      cy.visit(`${batchPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Batch');
    });

    it('should create an instance of Batch', () => {
      cy.get(`[data-cy="batchCode"]`).type('rejigger bah waterlogged');
      cy.get(`[data-cy="batchCode"]`).should('have.value', 'rejigger bah waterlogged');

      cy.get(`[data-cy="startDate"]`).type('2026-02-21');
      cy.get(`[data-cy="startDate"]`).blur();
      cy.get(`[data-cy="startDate"]`).should('have.value', '2026-02-21');

      cy.get(`[data-cy="endDate"]`).type('2026-02-22');
      cy.get(`[data-cy="endDate"]`).blur();
      cy.get(`[data-cy="endDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="currentPhase"]`).select('HARVEST');

      cy.get(`[data-cy="numberOfBags"]`).type('11898');
      cy.get(`[data-cy="numberOfBags"]`).should('have.value', '11898');

      cy.get(`[data-cy="substrateWeightKg"]`).type('20513.61');
      cy.get(`[data-cy="substrateWeightKg"]`).should('have.value', '20513.61');

      cy.get(`[data-cy="spawnWeightKg"]`).type('854.91');
      cy.get(`[data-cy="spawnWeightKg"]`).should('have.value', '854.91');

      cy.get(`[data-cy="targetYieldKg"]`).type('6149.87');
      cy.get(`[data-cy="targetYieldKg"]`).should('have.value', '6149.87');

      cy.get(`[data-cy="actualTotalYieldKg"]`).type('10979.46');
      cy.get(`[data-cy="actualTotalYieldKg"]`).should('have.value', '10979.46');

      cy.get(`[data-cy="biologicalEfficiencyPercent"]`).type('1907.14');
      cy.get(`[data-cy="biologicalEfficiencyPercent"]`).should('have.value', '1907.14');

      cy.get(`[data-cy="isContaminated"]`).should('not.be.checked');
      cy.get(`[data-cy="isContaminated"]`).click();
      cy.get(`[data-cy="isContaminated"]`).should('be.checked');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="completionNote"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="completionNote"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="strain"]`).select(1);
      cy.get(`[data-cy="recipe"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        batch = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', batchPageUrlPattern);
    });
  });
});
