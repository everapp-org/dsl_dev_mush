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

describe('StockMovement e2e test', () => {
  const stockMovementPageUrl = '/stock-movement';
  const stockMovementPageUrlPattern = new RegExp('/stock-movement(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const stockMovementSample = {"movementDate":"2026-02-21T21:48:14.118Z","movementType":"WASTE","quantity":8980.89,"unit":"GRAM"};

  let stockMovement;
  // let inventoryLot;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/inventory-lots',
      body: {"lotCode":"along husky","receivedDate":"2026-02-22","quantityReceived":30287.01,"quantityOnHand":16067.18,"unit":"PIECE","expiryDate":"2026-02-21","storageLocation":"worriedly inasmuch boohoo","supplierLotNumber":"midst normal hm","isExhausted":false,"note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      inventoryLot = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/stock-movements+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/stock-movements').as('postEntityRequest');
    cy.intercept('DELETE', '/api/stock-movements/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/inventory-lots', {
      statusCode: 200,
      body: [inventoryLot],
    });

    cy.intercept('GET', '/api/batches', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (stockMovement) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/stock-movements/${stockMovement.id}`,
      }).then(() => {
        stockMovement = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
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
   */

  it('StockMovements menu should load StockMovements page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('stock-movement');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StockMovement').should('exist');
    cy.url().should('match', stockMovementPageUrlPattern);
  });

  describe('StockMovement page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(stockMovementPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StockMovement page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/stock-movement/new$'));
        cy.getEntityCreateUpdateHeading('StockMovement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMovementPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/stock-movements',
          body: {
            ...stockMovementSample,
            inventoryLot: inventoryLot,
          },
        }).then(({ body }) => {
          stockMovement = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/stock-movements+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [stockMovement],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(stockMovementPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(stockMovementPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details StockMovement page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('stockMovement');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMovementPageUrlPattern);
      });

      it('edit button click should load edit StockMovement page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StockMovement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMovementPageUrlPattern);
      });

      it('edit button click should load edit StockMovement page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StockMovement');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMovementPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of StockMovement', () => {
        cy.intercept('GET', '/api/stock-movements/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('stockMovement').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMovementPageUrlPattern);

        stockMovement = undefined;
      });
    });
  });

  describe('new StockMovement page', () => {
    beforeEach(() => {
      cy.visit(`${stockMovementPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StockMovement');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of StockMovement', () => {
      cy.get(`[data-cy="movementDate"]`).type('2026-02-21T18:03');
      cy.get(`[data-cy="movementDate"]`).blur();
      cy.get(`[data-cy="movementDate"]`).should('have.value', '2026-02-21T18:03');

      cy.get(`[data-cy="movementType"]`).select('CONSUMPTION');

      cy.get(`[data-cy="quantity"]`).type('20162.8');
      cy.get(`[data-cy="quantity"]`).should('have.value', '20162.8');

      cy.get(`[data-cy="unit"]`).select('BOX');

      cy.get(`[data-cy="reference"]`).type('yearningly drat');
      cy.get(`[data-cy="reference"]`).should('have.value', 'yearningly drat');

      cy.get(`[data-cy="reason"]`).type('fooey');
      cy.get(`[data-cy="reason"]`).should('have.value', 'fooey');

      cy.get(`[data-cy="performedBy"]`).type('per rebound barring');
      cy.get(`[data-cy="performedBy"]`).should('have.value', 'per rebound barring');

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="inventoryLot"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        stockMovement = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', stockMovementPageUrlPattern);
    });
  });
});
