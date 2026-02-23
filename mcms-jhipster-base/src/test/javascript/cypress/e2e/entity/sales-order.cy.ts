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

describe('SalesOrder e2e test', () => {
  const salesOrderPageUrl = '/sales-order';
  const salesOrderPageUrlPattern = new RegExp('/sales-order(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const salesOrderSample = {
    orderCode: 'scent tusk',
    orderDate: '2026-02-21',
    status: 'RETURNED',
    currency: 'dissemble throughout unit',
    paymentStatus: 'PARTIALLY_PAID',
  };

  let salesOrder;
  let customer;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/customers',
      body: {
        name: 'likewise help evenly',
        contactPerson: 'vice',
        email: 'Steve_Corkery29@yahoo.com',
        phone: '(818) 436-8265 x7595',
        address: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        deliveryPreference: 'ha fatally',
        paymentTerms: 'polished release',
        active: false,
        note: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
      },
    }).then(({ body }) => {
      customer = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/sales-orders+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/sales-orders').as('postEntityRequest');
    cy.intercept('DELETE', '/api/sales-orders/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/customers', {
      statusCode: 200,
      body: [customer],
    });
  });

  afterEach(() => {
    if (salesOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/sales-orders/${salesOrder.id}`,
      }).then(() => {
        salesOrder = undefined;
      });
    }
  });

  afterEach(() => {
    if (customer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/customers/${customer.id}`,
      }).then(() => {
        customer = undefined;
      });
    }
  });

  it('SalesOrders menu should load SalesOrders page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('sales-order');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SalesOrder').should('exist');
    cy.url().should('match', salesOrderPageUrlPattern);
  });

  describe('SalesOrder page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(salesOrderPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SalesOrder page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/sales-order/new$'));
        cy.getEntityCreateUpdateHeading('SalesOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salesOrderPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/sales-orders',
          body: {
            ...salesOrderSample,
            customer,
          },
        }).then(({ body }) => {
          salesOrder = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/sales-orders+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [salesOrder],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(salesOrderPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details SalesOrder page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('salesOrder');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salesOrderPageUrlPattern);
      });

      it('edit button click should load edit SalesOrder page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SalesOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salesOrderPageUrlPattern);
      });

      it('edit button click should load edit SalesOrder page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SalesOrder');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salesOrderPageUrlPattern);
      });

      it('last delete button click should delete instance of SalesOrder', () => {
        cy.intercept('GET', '/api/sales-orders/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('salesOrder').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', salesOrderPageUrlPattern);

        salesOrder = undefined;
      });
    });
  });

  describe('new SalesOrder page', () => {
    beforeEach(() => {
      cy.visit(`${salesOrderPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SalesOrder');
    });

    it('should create an instance of SalesOrder', () => {
      cy.get(`[data-cy="orderCode"]`).type('although');
      cy.get(`[data-cy="orderCode"]`).should('have.value', 'although');

      cy.get(`[data-cy="orderDate"]`).type('2026-02-21');
      cy.get(`[data-cy="orderDate"]`).blur();
      cy.get(`[data-cy="orderDate"]`).should('have.value', '2026-02-21');

      cy.get(`[data-cy="requestedDeliveryDate"]`).type('2026-02-22');
      cy.get(`[data-cy="requestedDeliveryDate"]`).blur();
      cy.get(`[data-cy="requestedDeliveryDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="actualDeliveryDate"]`).type('2026-02-22');
      cy.get(`[data-cy="actualDeliveryDate"]`).blur();
      cy.get(`[data-cy="actualDeliveryDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="status"]`).select('DELIVERED');

      cy.get(`[data-cy="totalWeight"]`).type('12579.05');
      cy.get(`[data-cy="totalWeight"]`).should('have.value', '12579.05');

      cy.get(`[data-cy="totalRevenue"]`).type('22945.26');
      cy.get(`[data-cy="totalRevenue"]`).should('have.value', '22945.26');

      cy.get(`[data-cy="currency"]`).type('gee');
      cy.get(`[data-cy="currency"]`).should('have.value', 'gee');

      cy.get(`[data-cy="invoiceNumber"]`).type('pity');
      cy.get(`[data-cy="invoiceNumber"]`).should('have.value', 'pity');

      cy.get(`[data-cy="paymentStatus"]`).select('UNPAID');

      cy.get(`[data-cy="paymentDueDate"]`).type('2026-02-22');
      cy.get(`[data-cy="paymentDueDate"]`).blur();
      cy.get(`[data-cy="paymentDueDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="paymentReceivedDate"]`).type('2026-02-22');
      cy.get(`[data-cy="paymentReceivedDate"]`).blur();
      cy.get(`[data-cy="paymentReceivedDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="shippingAddress"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="shippingAddress"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="customer"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        salesOrder = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', salesOrderPageUrlPattern);
    });
  });
});
