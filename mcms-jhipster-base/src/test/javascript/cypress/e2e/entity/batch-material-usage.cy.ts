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

describe('BatchMaterialUsage e2e test', () => {
  const batchMaterialUsagePageUrl = '/batch-material-usage';
  const batchMaterialUsagePageUrlPattern = new RegExp('/batch-material-usage(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const batchMaterialUsageSample = {"usageDate":"2026-02-21","quantityUsed":11939.54,"unit":"PALLET"};

  let batchMaterialUsage;
  // let batch;
  // let inventoryLot;
  // let material;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/batches',
      body: {"batchCode":"biodegrade shovel hungrily","startDate":"2026-02-22","endDate":"2026-02-21","currentPhase":"INOCULATION","numberOfBags":1334,"substrateWeightKg":7347.75,"spawnWeightKg":7868.63,"targetYieldKg":22374.98,"actualTotalYieldKg":4251.37,"biologicalEfficiencyPercent":21594.96,"isContaminated":true,"isActive":false,"completionNote":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      batch = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/inventory-lots',
      body: {"lotCode":"crossly critical moment","receivedDate":"2026-02-22","quantityReceived":12362.27,"quantityOnHand":7068.64,"unit":"PALLET","expiryDate":"2026-02-22","storageLocation":"omelet fervently anti","supplierLotNumber":"stiffen clamour","isExhausted":false,"note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      inventoryLot = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/materials',
      body: {"code":"nor quizzically as","name":"because","category":"SPAWN","defaultUnit":"BAG","minimumStockLevel":32526.12,"description":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","active":false,"note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      material = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/batch-material-usages+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/batch-material-usages').as('postEntityRequest');
    cy.intercept('DELETE', '/api/batch-material-usages/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/batches', {
      statusCode: 200,
      body: [batch],
    });

    cy.intercept('GET', '/api/inventory-lots', {
      statusCode: 200,
      body: [inventoryLot],
    });

    cy.intercept('GET', '/api/materials', {
      statusCode: 200,
      body: [material],
    });

  });
   */

  afterEach(() => {
    if (batchMaterialUsage) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/batch-material-usages/${batchMaterialUsage.id}`,
      }).then(() => {
        batchMaterialUsage = undefined;
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
    if (inventoryLot) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/inventory-lots/${inventoryLot.id}`,
      }).then(() => {
        inventoryLot = undefined;
      });
    }
    if (material) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/materials/${material.id}`,
      }).then(() => {
        material = undefined;
      });
    }
  });
   */

  it('BatchMaterialUsages menu should load BatchMaterialUsages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('batch-material-usage');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('BatchMaterialUsage').should('exist');
    cy.url().should('match', batchMaterialUsagePageUrlPattern);
  });

  describe('BatchMaterialUsage page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(batchMaterialUsagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create BatchMaterialUsage page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/batch-material-usage/new$'));
        cy.getEntityCreateUpdateHeading('BatchMaterialUsage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', batchMaterialUsagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/batch-material-usages',
          body: {
            ...batchMaterialUsageSample,
            batch: batch,
            inventoryLot: inventoryLot,
            material: material,
          },
        }).then(({ body }) => {
          batchMaterialUsage = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/batch-material-usages+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [batchMaterialUsage],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(batchMaterialUsagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(batchMaterialUsagePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details BatchMaterialUsage page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('batchMaterialUsage');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', batchMaterialUsagePageUrlPattern);
      });

      it('edit button click should load edit BatchMaterialUsage page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BatchMaterialUsage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', batchMaterialUsagePageUrlPattern);
      });

      it('edit button click should load edit BatchMaterialUsage page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BatchMaterialUsage');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', batchMaterialUsagePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of BatchMaterialUsage', () => {
        cy.intercept('GET', '/api/batch-material-usages/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('batchMaterialUsage').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', batchMaterialUsagePageUrlPattern);

        batchMaterialUsage = undefined;
      });
    });
  });

  describe('new BatchMaterialUsage page', () => {
    beforeEach(() => {
      cy.visit(`${batchMaterialUsagePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('BatchMaterialUsage');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of BatchMaterialUsage', () => {
      cy.get(`[data-cy="usageDate"]`).type('2026-02-22');
      cy.get(`[data-cy="usageDate"]`).blur();
      cy.get(`[data-cy="usageDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="quantityUsed"]`).type('16015.42');
      cy.get(`[data-cy="quantityUsed"]`).should('have.value', '16015.42');

      cy.get(`[data-cy="unit"]`).select('GRAM');

      cy.get(`[data-cy="purpose"]`).type('tremendously');
      cy.get(`[data-cy="purpose"]`).should('have.value', 'tremendously');

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="batch"]`).select(1);
      cy.get(`[data-cy="inventoryLot"]`).select(1);
      cy.get(`[data-cy="material"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        batchMaterialUsage = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', batchMaterialUsagePageUrlPattern);
    });
  });
});
