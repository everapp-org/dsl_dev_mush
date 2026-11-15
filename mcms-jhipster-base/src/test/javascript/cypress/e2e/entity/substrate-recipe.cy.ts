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

describe('SubstrateRecipe e2e test', () => {
  const substrateRecipePageUrl = '/substrate-recipe';
  const substrateRecipePageUrlPattern = new RegExp('/substrate-recipe(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const substrateRecipeSample = { name: 'huzzah', version: 'buzzing for baritone', baseType: 'SPENT_COFFEE_GROUNDS', active: true };

  let substrateRecipe;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/substrate-recipes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/substrate-recipes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/substrate-recipes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (substrateRecipe) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/substrate-recipes/${substrateRecipe.id}`,
      }).then(() => {
        substrateRecipe = undefined;
      });
    }
  });

  it('SubstrateRecipes menu should load SubstrateRecipes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('substrate-recipe');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SubstrateRecipe').should('exist');
    cy.url().should('match', substrateRecipePageUrlPattern);
  });

  describe('SubstrateRecipe page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(substrateRecipePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SubstrateRecipe page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/substrate-recipe/new$'));
        cy.getEntityCreateUpdateHeading('SubstrateRecipe');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', substrateRecipePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/substrate-recipes',
          body: substrateRecipeSample,
        }).then(({ body }) => {
          substrateRecipe = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/substrate-recipes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [substrateRecipe],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(substrateRecipePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details SubstrateRecipe page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('substrateRecipe');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', substrateRecipePageUrlPattern);
      });

      it('edit button click should load edit SubstrateRecipe page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SubstrateRecipe');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', substrateRecipePageUrlPattern);
      });

      it('edit button click should load edit SubstrateRecipe page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SubstrateRecipe');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', substrateRecipePageUrlPattern);
      });

      it('last delete button click should delete instance of SubstrateRecipe', () => {
        cy.intercept('GET', '/api/substrate-recipes/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('substrateRecipe').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', substrateRecipePageUrlPattern);

        substrateRecipe = undefined;
      });
    });
  });

  describe('new SubstrateRecipe page', () => {
    beforeEach(() => {
      cy.visit(`${substrateRecipePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SubstrateRecipe');
    });

    it('should create an instance of SubstrateRecipe', () => {
      cy.get(`[data-cy="name"]`).type('following clone');
      cy.get(`[data-cy="name"]`).should('have.value', 'following clone');

      cy.get(`[data-cy="version"]`).type('miserably metabolite within');
      cy.get(`[data-cy="version"]`).should('have.value', 'miserably metabolite within');

      cy.get(`[data-cy="baseType"]`).select('SPENT_COFFEE_GROUNDS');

      cy.get(`[data-cy="compositionDetail"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="compositionDetail"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="sterilizationMethod"]`).type('since provider');
      cy.get(`[data-cy="sterilizationMethod"]`).should('have.value', 'since provider');

      cy.get(`[data-cy="moistureTargetPercent"]`).type('1670.98');
      cy.get(`[data-cy="moistureTargetPercent"]`).should('have.value', '1670.98');

      cy.get(`[data-cy="phTarget"]`).type('7364.77');
      cy.get(`[data-cy="phTarget"]`).should('have.value', '7364.77');

      cy.get(`[data-cy="supplementNotes"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="supplementNotes"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="active"]`).should('not.be.checked');
      cy.get(`[data-cy="active"]`).click();
      cy.get(`[data-cy="active"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        substrateRecipe = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', substrateRecipePageUrlPattern);
    });
  });
});
