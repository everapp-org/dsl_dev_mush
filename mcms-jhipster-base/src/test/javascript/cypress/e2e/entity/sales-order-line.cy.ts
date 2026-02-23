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

describe('SalesOrderLine e2e test', () => {
  const salesOrderLinePageUrl = '/sales-order-line';
  const salesOrderLinePageUrlPattern = new RegExp('/sales-order-line(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const salesOrderLineSample = {"lineNumber":8341,"weightKg":10383.66,"grade":"A_PREMIUM","pricePerKg":19605.96,"lineTotal":32505.53};

  let salesOrderLine;
  // let salesOrder;
  // let product;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/sales-orders',
      body: {"orderCode":"coarse","orderDate":"2026-02-22","requestedDeliveryDate":"2026-02-22","actualDeliveryDate":"2026-02-22","status":"ORDERED","totalWeight":26545.34,"totalRevenue":26492.72,"currency":"upbeat","invoiceNumber":"clueless since than","paymentStatus":"UNPAID","paymentDueDate":"2026-02-21","paymentReceivedDate":"2026-02-22","shippingAddress":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      salesOrder = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/products',
      body: {"code":"above bourgeoisie row","name":"wearily","description":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","defaultGrade":"A_PREMIUM","defaultWeightKg":17918.74,"defaultPricePerKg":4181.67,"shelfLifeDays":29031,"active":false,"note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      product = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/sales-order-lines+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/sales-order-lines').as('postEntityRequest');
    cy.intercept('DELETE', '/api/sales-order-lines/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/sales-orders', {
      statusCode: 200,
      body: [salesOrder],
    });

    cy.intercept('GET', '/api/products', {
      statusCode: 200,
      body: [product],
    });

    cy.intercept('GET', '/api/batches', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (salesOrderLine) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/sales-order-lines/${salesOrderLine.id}`,
      }).then(() => {
        salesOrderLine = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (salesOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/sales-orders/${salesOrder.id}`,
      }).then(() => {
        salesOrder = undefined;
      });
    }
    if (product) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/products/${product.id}`,
      }).then(() => {
        product = undefined;
      });
    }
  });
   */

  it('SalesOrderLines menu should load SalesOrderLines page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('sales-order-line');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SalesOrderLine').should('exist');
    cy.url().should('match', salesOrderLinePageUrlPattern);
  });

  describe('SalesOrderLine page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(salesOrderLinePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SalesOrderLine page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/sales-order-line/new$'));
        cy.getEntityCreateUpdateHeading('SalesOrderLine');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salesOrderLinePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/sales-order-lines',
          body: {
            ...salesOrderLineSample,
            salesOrder: salesOrder,
            product: product,
          },
        }).then(({ body }) => {
          salesOrderLine = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/sales-order-lines+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [salesOrderLine],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(salesOrderLinePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(salesOrderLinePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details SalesOrderLine page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('salesOrderLine');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salesOrderLinePageUrlPattern);
      });

      it('edit button click should load edit SalesOrderLine page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SalesOrderLine');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salesOrderLinePageUrlPattern);
      });

      it('edit button click should load edit SalesOrderLine page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SalesOrderLine');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salesOrderLinePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of SalesOrderLine', () => {
        cy.intercept('GET', '/api/sales-order-lines/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('salesOrderLine').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salesOrderLinePageUrlPattern);

        salesOrderLine = undefined;
      });
    });
  });

  describe('new SalesOrderLine page', () => {
    beforeEach(() => {
      cy.visit(`${salesOrderLinePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SalesOrderLine');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of SalesOrderLine', () => {
      cy.get(`[data-cy="lineNumber"]`).type('13010');
      cy.get(`[data-cy="lineNumber"]`).should('have.value', '13010');

      cy.get(`[data-cy="weightKg"]`).type('2018.46');
      cy.get(`[data-cy="weightKg"]`).should('have.value', '2018.46');

      cy.get(`[data-cy="grade"]`).select('WASTE');

      cy.get(`[data-cy="quantityUnits"]`).type('24960');
      cy.get(`[data-cy="quantityUnits"]`).should('have.value', '24960');

      cy.get(`[data-cy="unit"]`).select('LITER');

      cy.get(`[data-cy="pricePerKg"]`).type('20202.59');
      cy.get(`[data-cy="pricePerKg"]`).should('have.value', '20202.59');

      cy.get(`[data-cy="lineTotal"]`).type('29323.63');
      cy.get(`[data-cy="lineTotal"]`).should('have.value', '29323.63');

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="salesOrder"]`).select(1);
      cy.get(`[data-cy="product"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        salesOrderLine = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', salesOrderLinePageUrlPattern);
    });
  });
});
