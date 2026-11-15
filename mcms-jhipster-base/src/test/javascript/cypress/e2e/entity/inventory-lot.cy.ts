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

describe('InventoryLot e2e test', () => {
  const inventoryLotPageUrl = '/inventory-lot';
  const inventoryLotPageUrlPattern = new RegExp('/inventory-lot(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const inventoryLotSample = {"lotCode":"calmly humble","receivedDate":"2026-02-22","quantityReceived":13702.26,"quantityOnHand":8548.52,"unit":"GRAM","isExhausted":true};

  let inventoryLot;
  // let material;
  // let supplyOrderLine;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/materials',
      body: {"code":"colossal scratchy physically","name":"sore","category":"EQUIPMENT_SUPPLY","defaultUnit":"PIECE","minimumStockLevel":26145.38,"description":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","active":false,"note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      material = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/supply-order-lines',
      body: {"lineNumber":6945,"itemDescription":"bah","quantityOrdered":26906.46,"quantityReceived":22995.72,"unit":"PIECE","unitPrice":21528.75,"lineTotal":4235.38,"lotNumber":"reasonable","expiryDate":"2026-02-21","qualityOnReceipt":"minor willfully","isReceived":true,"note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      supplyOrderLine = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/inventory-lots+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/inventory-lots').as('postEntityRequest');
    cy.intercept('DELETE', '/api/inventory-lots/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/materials', {
      statusCode: 200,
      body: [material],
    });

    cy.intercept('GET', '/api/supply-order-lines', {
      statusCode: 200,
      body: [supplyOrderLine],
    });

  });
   */

  afterEach(() => {
    if (inventoryLot) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/inventory-lots/${inventoryLot.id}`,
      }).then(() => {
        inventoryLot = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (material) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/materials/${material.id}`,
      }).then(() => {
        material = undefined;
      });
    }
    if (supplyOrderLine) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/supply-order-lines/${supplyOrderLine.id}`,
      }).then(() => {
        supplyOrderLine = undefined;
      });
    }
  });
   */

  it('InventoryLots menu should load InventoryLots page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('inventory-lot');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('InventoryLot').should('exist');
    cy.url().should('match', inventoryLotPageUrlPattern);
  });

  describe('InventoryLot page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(inventoryLotPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create InventoryLot page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/inventory-lot/new$'));
        cy.getEntityCreateUpdateHeading('InventoryLot');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryLotPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/inventory-lots',
          body: {
            ...inventoryLotSample,
            material: material,
            supplyOrderLine: supplyOrderLine,
          },
        }).then(({ body }) => {
          inventoryLot = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/inventory-lots+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [inventoryLot],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(inventoryLotPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(inventoryLotPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details InventoryLot page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('inventoryLot');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryLotPageUrlPattern);
      });

      it('edit button click should load edit InventoryLot page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InventoryLot');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryLotPageUrlPattern);
      });

      it('edit button click should load edit InventoryLot page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InventoryLot');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryLotPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of InventoryLot', () => {
        cy.intercept('GET', '/api/inventory-lots/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('inventoryLot').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryLotPageUrlPattern);

        inventoryLot = undefined;
      });
    });
  });

  describe('new InventoryLot page', () => {
    beforeEach(() => {
      cy.visit(`${inventoryLotPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('InventoryLot');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of InventoryLot', () => {
      cy.get(`[data-cy="lotCode"]`).type('where');
      cy.get(`[data-cy="lotCode"]`).should('have.value', 'where');

      cy.get(`[data-cy="receivedDate"]`).type('2026-02-21');
      cy.get(`[data-cy="receivedDate"]`).blur();
      cy.get(`[data-cy="receivedDate"]`).should('have.value', '2026-02-21');

      cy.get(`[data-cy="quantityReceived"]`).type('473.15');
      cy.get(`[data-cy="quantityReceived"]`).should('have.value', '473.15');

      cy.get(`[data-cy="quantityOnHand"]`).type('26911.25');
      cy.get(`[data-cy="quantityOnHand"]`).should('have.value', '26911.25');

      cy.get(`[data-cy="unit"]`).select('KG');

      cy.get(`[data-cy="expiryDate"]`).type('2026-02-22');
      cy.get(`[data-cy="expiryDate"]`).blur();
      cy.get(`[data-cy="expiryDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="storageLocation"]`).type('wherever esteemed');
      cy.get(`[data-cy="storageLocation"]`).should('have.value', 'wherever esteemed');

      cy.get(`[data-cy="supplierLotNumber"]`).type('excepting');
      cy.get(`[data-cy="supplierLotNumber"]`).should('have.value', 'excepting');

      cy.get(`[data-cy="isExhausted"]`).should('not.be.checked');
      cy.get(`[data-cy="isExhausted"]`).click();
      cy.get(`[data-cy="isExhausted"]`).should('be.checked');

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="material"]`).select(1);
      cy.get(`[data-cy="supplyOrderLine"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        inventoryLot = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', inventoryLotPageUrlPattern);
    });
  });
});
