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

describe('Strain e2e test', () => {
  const strainPageUrl = '/strain';
  const strainPageUrlPattern = new RegExp('/strain(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const strainSample = { name: 'dreamily sympathetically since', species: 'design whup descriptive', active: false };

  let strain;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/strains+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/strains').as('postEntityRequest');
    cy.intercept('DELETE', '/api/strains/*').as('deleteEntityRequest');
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
  });

  it('Strains menu should load Strains page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('strain');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Strain').should('exist');
    cy.url().should('match', strainPageUrlPattern);
  });

  describe('Strain page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(strainPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Strain page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/strain/new$'));
        cy.getEntityCreateUpdateHeading('Strain');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', strainPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/strains',
          body: strainSample,
        }).then(({ body }) => {
          strain = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/strains+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [strain],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(strainPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Strain page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('strain');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', strainPageUrlPattern);
      });

      it('edit button click should load edit Strain page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Strain');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', strainPageUrlPattern);
      });

      it('edit button click should load edit Strain page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Strain');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', strainPageUrlPattern);
      });

      it('last delete button click should delete instance of Strain', () => {
        cy.intercept('GET', '/api/strains/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('strain').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', strainPageUrlPattern);

        strain = undefined;
      });
    });
  });

  describe('new Strain page', () => {
    beforeEach(() => {
      cy.visit(`${strainPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Strain');
    });

    it('should create an instance of Strain', () => {
      cy.get(`[data-cy="name"]`).type('super pine coordination');
      cy.get(`[data-cy="name"]`).should('have.value', 'super pine coordination');

      cy.get(`[data-cy="species"]`).type('shallow pish cricket');
      cy.get(`[data-cy="species"]`).should('have.value', 'shallow pish cricket');

      cy.get(`[data-cy="variety"]`).type('upside-down executor circa');
      cy.get(`[data-cy="variety"]`).should('have.value', 'upside-down executor circa');

      cy.get(`[data-cy="optimalTempMinC"]`).type('19167.99');
      cy.get(`[data-cy="optimalTempMinC"]`).should('have.value', '19167.99');

      cy.get(`[data-cy="optimalTempMaxC"]`).type('25270.13');
      cy.get(`[data-cy="optimalTempMaxC"]`).should('have.value', '25270.13');

      cy.get(`[data-cy="optimalHumidityMin"]`).type('2783.25');
      cy.get(`[data-cy="optimalHumidityMin"]`).should('have.value', '2783.25');

      cy.get(`[data-cy="optimalHumidityMax"]`).type('13134.14');
      cy.get(`[data-cy="optimalHumidityMax"]`).should('have.value', '13134.14');

      cy.get(`[data-cy="optimalCO2MaxPpm"]`).type('30743');
      cy.get(`[data-cy="optimalCO2MaxPpm"]`).should('have.value', '30743');

      cy.get(`[data-cy="colonizationDaysMin"]`).type('21633');
      cy.get(`[data-cy="colonizationDaysMin"]`).should('have.value', '21633');

      cy.get(`[data-cy="colonizationDaysMax"]`).type('28683');
      cy.get(`[data-cy="colonizationDaysMax"]`).should('have.value', '28683');

      cy.get(`[data-cy="expectedYieldPercent"]`).type('24168.53');
      cy.get(`[data-cy="expectedYieldPercent"]`).should('have.value', '24168.53');

      cy.get(`[data-cy="shelfLifeDays"]`).type('6177');
      cy.get(`[data-cy="shelfLifeDays"]`).should('have.value', '6177');

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="active"]`).should('not.be.checked');
      cy.get(`[data-cy="active"]`).click();
      cy.get(`[data-cy="active"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        strain = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', strainPageUrlPattern);
    });
  });
});
