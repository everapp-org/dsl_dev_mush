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

describe('Room e2e test', () => {
  const roomPageUrl = '/room';
  const roomPageUrlPattern = new RegExp('/room(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const roomSample = { name: 'along like sailor', code: 'hunt', roomType: 'INCUBATION', status: 'DISINFECTION' };

  let room;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/rooms+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/rooms').as('postEntityRequest');
    cy.intercept('DELETE', '/api/rooms/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (room) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/rooms/${room.id}`,
      }).then(() => {
        room = undefined;
      });
    }
  });

  it('Rooms menu should load Rooms page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('room');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Room').should('exist');
    cy.url().should('match', roomPageUrlPattern);
  });

  describe('Room page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(roomPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Room page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/room/new$'));
        cy.getEntityCreateUpdateHeading('Room');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', roomPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/rooms',
          body: roomSample,
        }).then(({ body }) => {
          room = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/rooms+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [room],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(roomPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Room page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('room');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', roomPageUrlPattern);
      });

      it('edit button click should load edit Room page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Room');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', roomPageUrlPattern);
      });

      it('edit button click should load edit Room page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Room');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', roomPageUrlPattern);
      });

      it('last delete button click should delete instance of Room', () => {
        cy.intercept('GET', '/api/rooms/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('room').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', roomPageUrlPattern);

        room = undefined;
      });
    });
  });

  describe('new Room page', () => {
    beforeEach(() => {
      cy.visit(`${roomPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Room');
    });

    it('should create an instance of Room', () => {
      cy.get(`[data-cy="name"]`).type('um contravene');
      cy.get(`[data-cy="name"]`).should('have.value', 'um contravene');

      cy.get(`[data-cy="code"]`).type('mindless');
      cy.get(`[data-cy="code"]`).should('have.value', 'mindless');

      cy.get(`[data-cy="roomType"]`).select('STERILE_ZONE');

      cy.get(`[data-cy="status"]`).select('ACTIVE');

      cy.get(`[data-cy="capacityBags"]`).type('21417');
      cy.get(`[data-cy="capacityBags"]`).should('have.value', '21417');

      cy.get(`[data-cy="currentOccupancy"]`).type('8388');
      cy.get(`[data-cy="currentOccupancy"]`).should('have.value', '8388');

      cy.get(`[data-cy="areaSqM"]`).type('20435.34');
      cy.get(`[data-cy="areaSqM"]`).should('have.value', '20435.34');

      cy.get(`[data-cy="hasHVAC"]`).should('not.be.checked');
      cy.get(`[data-cy="hasHVAC"]`).click();
      cy.get(`[data-cy="hasHVAC"]`).should('be.checked');

      cy.get(`[data-cy="hasMisting"]`).should('not.be.checked');
      cy.get(`[data-cy="hasMisting"]`).click();
      cy.get(`[data-cy="hasMisting"]`).should('be.checked');

      cy.get(`[data-cy="lastDisinfectionDate"]`).type('2026-02-22');
      cy.get(`[data-cy="lastDisinfectionDate"]`).blur();
      cy.get(`[data-cy="lastDisinfectionDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="note"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="note"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        room = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', roomPageUrlPattern);
    });
  });
});
