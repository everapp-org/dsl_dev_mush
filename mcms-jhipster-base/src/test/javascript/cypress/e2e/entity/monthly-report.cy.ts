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

describe('MonthlyReport e2e test', () => {
  const monthlyReportPageUrl = '/monthly-report';
  const monthlyReportPageUrlPattern = new RegExp('/monthly-report(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const monthlyReportSample = { year: 32579, month: 7, generatedAt: '2026-02-21T20:31:07.913Z' };

  let monthlyReport;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/monthly-reports+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/monthly-reports').as('postEntityRequest');
    cy.intercept('DELETE', '/api/monthly-reports/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (monthlyReport) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/monthly-reports/${monthlyReport.id}`,
      }).then(() => {
        monthlyReport = undefined;
      });
    }
  });

  it('MonthlyReports menu should load MonthlyReports page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('monthly-report');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MonthlyReport').should('exist');
    cy.url().should('match', monthlyReportPageUrlPattern);
  });

  describe('MonthlyReport page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(monthlyReportPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MonthlyReport page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/monthly-report/new$'));
        cy.getEntityCreateUpdateHeading('MonthlyReport');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', monthlyReportPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/monthly-reports',
          body: monthlyReportSample,
        }).then(({ body }) => {
          monthlyReport = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/monthly-reports+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [monthlyReport],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(monthlyReportPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MonthlyReport page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('monthlyReport');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', monthlyReportPageUrlPattern);
      });

      it('edit button click should load edit MonthlyReport page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MonthlyReport');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', monthlyReportPageUrlPattern);
      });

      it('edit button click should load edit MonthlyReport page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MonthlyReport');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', monthlyReportPageUrlPattern);
      });

      it('last delete button click should delete instance of MonthlyReport', () => {
        cy.intercept('GET', '/api/monthly-reports/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('monthlyReport').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', monthlyReportPageUrlPattern);

        monthlyReport = undefined;
      });
    });
  });

  describe('new MonthlyReport page', () => {
    beforeEach(() => {
      cy.visit(`${monthlyReportPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MonthlyReport');
    });

    it('should create an instance of MonthlyReport', () => {
      cy.get(`[data-cy="year"]`).type('8049');
      cy.get(`[data-cy="year"]`).should('have.value', '8049');

      cy.get(`[data-cy="month"]`).type('2');
      cy.get(`[data-cy="month"]`).should('have.value', '2');

      cy.get(`[data-cy="generatedAt"]`).type('2026-02-22T05:58');
      cy.get(`[data-cy="generatedAt"]`).blur();
      cy.get(`[data-cy="generatedAt"]`).should('have.value', '2026-02-22T05:58');

      cy.get(`[data-cy="totalYieldKg"]`).type('1170.18');
      cy.get(`[data-cy="totalYieldKg"]`).should('have.value', '1170.18');

      cy.get(`[data-cy="totalCost"]`).type('24541.96');
      cy.get(`[data-cy="totalCost"]`).should('have.value', '24541.96');

      cy.get(`[data-cy="totalRevenue"]`).type('25555.69');
      cy.get(`[data-cy="totalRevenue"]`).should('have.value', '25555.69');

      cy.get(`[data-cy="profitMarginPercent"]`).type('23751.52');
      cy.get(`[data-cy="profitMarginPercent"]`).should('have.value', '23751.52');

      cy.get(`[data-cy="totalContaminationEvents"]`).type('24603');
      cy.get(`[data-cy="totalContaminationEvents"]`).should('have.value', '24603');

      cy.get(`[data-cy="totalMissingFields"]`).type('29498');
      cy.get(`[data-cy="totalMissingFields"]`).should('have.value', '29498');

      cy.get(`[data-cy="summary"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="summary"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        monthlyReport = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', monthlyReportPageUrlPattern);
    });
  });
});
