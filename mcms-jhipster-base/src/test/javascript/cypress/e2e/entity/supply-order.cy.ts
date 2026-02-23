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

describe('SupplyOrder e2e test', () => {
  const supplyOrderPageUrl = '/supply-order';
  const supplyOrderPageUrlPattern = new RegExp('/supply-order(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const supplyOrderSample = { orderCode: 'knitting', orderDate: '2026-02-22', status: 'ORDERED', currency: 'mouser' };

  let supplyOrder;
  let supplier;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/suppliers',
      body: {
        name: 'right culminate',
        type: 'SUBSTRATE',
        contactPerson: 'bah inculcate consequently',
        email: 'Bailee66@hotmail.com',
        phone: '1-696-285-0809 x21136',
        address: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        rating: 2,
        active: true,
        note: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
      },
    }).then(({ body }) => {
      supplier = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/supply-orders+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/supply-orders').as('postEntityRequest');
    cy.intercept('DELETE', '/api/supply-orders/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/suppliers', {
      statusCode: 200,
      body: [supplier],
    });
  });

  afterEach(() => {
    if (supplyOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/supply-orders/${supplyOrder.id}`,
      }).then(() => {
        supplyOrder = undefined;
      });
    }
  });

  afterEach(() => {
    if (supplier) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/suppliers/${supplier.id}`,
      }).then(() => {
        supplier = undefined;
      });
    }
  });

  it('SupplyOrders menu should load SupplyOrders page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('supply-order');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SupplyOrder').should('exist');
    cy.url().should('match', supplyOrderPageUrlPattern);
  });

  describe('SupplyOrder page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(supplyOrderPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SupplyOrder page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/supply-order/new$'));
        cy.getEntityCreateUpdateHeading('SupplyOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyOrderPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/supply-orders',
          body: {
            ...supplyOrderSample,
            supplier,
          },
        }).then(({ body }) => {
          supplyOrder = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/supply-orders+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [supplyOrder],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(supplyOrderPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details SupplyOrder page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('supplyOrder');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyOrderPageUrlPattern);
      });

      it('edit button click should load edit SupplyOrder page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SupplyOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyOrderPageUrlPattern);
      });

      it('edit button click should load edit SupplyOrder page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SupplyOrder');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyOrderPageUrlPattern);
      });

      it('last delete button click should delete instance of SupplyOrder', () => {
        cy.intercept('GET', '/api/supply-orders/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('supplyOrder').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyOrderPageUrlPattern);

        supplyOrder = undefined;
      });
    });
  });

  describe('new SupplyOrder page', () => {
    beforeEach(() => {
      cy.visit(`${supplyOrderPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SupplyOrder');
    });

    it('should create an instance of SupplyOrder', () => {
      cy.get(`[data-cy="orderCode"]`).type('orientate aftermath father');
      cy.get(`[data-cy="orderCode"]`).should('have.value', 'orientate aftermath father');

      cy.get(`[data-cy="orderDate"]`).type('2026-02-22');
      cy.get(`[data-cy="orderDate"]`).blur();
      cy.get(`[data-cy="orderDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="expectedDeliveryDate"]`).type('2026-02-21');
      cy.get(`[data-cy="expectedDeliveryDate"]`).blur();
      cy.get(`[data-cy="expectedDeliveryDate"]`).should('have.value', '2026-02-21');

      cy.get(`[data-cy="actualDeliveryDate"]`).type('2026-02-22');
      cy.get(`[data-cy="actualDeliveryDate"]`).blur();
      cy.get(`[data-cy="actualDeliveryDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="status"]`).select('DELIVERED');

      cy.get(`[data-cy="totalAmount"]`).type('14131.2');
      cy.get(`[data-cy="totalAmount"]`).should('have.value', '14131.2');

      cy.get(`[data-cy="currency"]`).type('during every');
      cy.get(`[data-cy="currency"]`).should('have.value', 'during every');

      cy.get(`[data-cy="shippingAddress"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="shippingAddress"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="supplier"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        supplyOrder = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', supplyOrderPageUrlPattern);
    });
  });
});
