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

describe('SupplyOrderLine e2e test', () => {
  const supplyOrderLinePageUrl = '/supply-order-line';
  const supplyOrderLinePageUrlPattern = new RegExp('/supply-order-line(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const supplyOrderLineSample = {"lineNumber":3318,"itemDescription":"scarily","quantityOrdered":11285.4,"unit":"KG","unitPrice":25024.35,"lineTotal":741.09,"isReceived":false};

  let supplyOrderLine;
  // let supplyOrder;
  // let material;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/supply-orders',
      body: {"orderCode":"profuse blah","orderDate":"2026-02-21","expectedDeliveryDate":"2026-02-22","actualDeliveryDate":"2026-02-22","status":"DELIVERED","totalAmount":19854.58,"currency":"place","shippingAddress":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      supplyOrder = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/materials',
      body: {"code":"achieve","name":"phooey astride wide","category":"PACKAGING","defaultUnit":"PALLET","minimumStockLevel":14546.06,"description":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","active":false,"note":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ="},
    }).then(({ body }) => {
      material = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/supply-order-lines+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/supply-order-lines').as('postEntityRequest');
    cy.intercept('DELETE', '/api/supply-order-lines/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/supply-orders', {
      statusCode: 200,
      body: [supplyOrder],
    });

    cy.intercept('GET', '/api/materials', {
      statusCode: 200,
      body: [material],
    });

    cy.intercept('GET', '/api/batches', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (supplyOrderLine) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/supply-order-lines/${supplyOrderLine.id}`,
      }).then(() => {
        supplyOrderLine = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (supplyOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/supply-orders/${supplyOrder.id}`,
      }).then(() => {
        supplyOrder = undefined;
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

  it('SupplyOrderLines menu should load SupplyOrderLines page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('supply-order-line');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SupplyOrderLine').should('exist');
    cy.url().should('match', supplyOrderLinePageUrlPattern);
  });

  describe('SupplyOrderLine page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(supplyOrderLinePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SupplyOrderLine page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/supply-order-line/new$'));
        cy.getEntityCreateUpdateHeading('SupplyOrderLine');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyOrderLinePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/supply-order-lines',
          body: {
            ...supplyOrderLineSample,
            supplyOrder: supplyOrder,
            material: material,
          },
        }).then(({ body }) => {
          supplyOrderLine = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/supply-order-lines+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [supplyOrderLine],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(supplyOrderLinePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(supplyOrderLinePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details SupplyOrderLine page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('supplyOrderLine');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyOrderLinePageUrlPattern);
      });

      it('edit button click should load edit SupplyOrderLine page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SupplyOrderLine');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyOrderLinePageUrlPattern);
      });

      it('edit button click should load edit SupplyOrderLine page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SupplyOrderLine');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyOrderLinePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of SupplyOrderLine', () => {
        cy.intercept('GET', '/api/supply-order-lines/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('supplyOrderLine').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyOrderLinePageUrlPattern);

        supplyOrderLine = undefined;
      });
    });
  });

  describe('new SupplyOrderLine page', () => {
    beforeEach(() => {
      cy.visit(`${supplyOrderLinePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SupplyOrderLine');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of SupplyOrderLine', () => {
      cy.get(`[data-cy="lineNumber"]`).type('31377');
      cy.get(`[data-cy="lineNumber"]`).should('have.value', '31377');

      cy.get(`[data-cy="itemDescription"]`).type('restfully');
      cy.get(`[data-cy="itemDescription"]`).should('have.value', 'restfully');

      cy.get(`[data-cy="quantityOrdered"]`).type('379.76');
      cy.get(`[data-cy="quantityOrdered"]`).should('have.value', '379.76');

      cy.get(`[data-cy="quantityReceived"]`).type('17861.69');
      cy.get(`[data-cy="quantityReceived"]`).should('have.value', '17861.69');

      cy.get(`[data-cy="unit"]`).select('LITER');

      cy.get(`[data-cy="unitPrice"]`).type('5867.29');
      cy.get(`[data-cy="unitPrice"]`).should('have.value', '5867.29');

      cy.get(`[data-cy="lineTotal"]`).type('30991.68');
      cy.get(`[data-cy="lineTotal"]`).should('have.value', '30991.68');

      cy.get(`[data-cy="lotNumber"]`).type('lest');
      cy.get(`[data-cy="lotNumber"]`).should('have.value', 'lest');

      cy.get(`[data-cy="expiryDate"]`).type('2026-02-21');
      cy.get(`[data-cy="expiryDate"]`).blur();
      cy.get(`[data-cy="expiryDate"]`).should('have.value', '2026-02-21');

      cy.get(`[data-cy="qualityOnReceipt"]`).type('spherical numb hundred');
      cy.get(`[data-cy="qualityOnReceipt"]`).should('have.value', 'spherical numb hundred');

      cy.get(`[data-cy="isReceived"]`).should('not.be.checked');
      cy.get(`[data-cy="isReceived"]`).click();
      cy.get(`[data-cy="isReceived"]`).should('be.checked');

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="supplyOrder"]`).select(1);
      cy.get(`[data-cy="material"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        supplyOrderLine = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', supplyOrderLinePageUrlPattern);
    });
  });
});
