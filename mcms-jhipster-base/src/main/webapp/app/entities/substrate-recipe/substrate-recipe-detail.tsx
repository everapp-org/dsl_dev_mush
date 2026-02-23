import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

import { getEntity } from './substrate-recipe.reducer';

export const SubstrateRecipeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const substrateRecipeEntity = useAppSelector(state => state.substrateRecipe.entity);
  const isAdminOrManager = useAppSelector(state =>
    hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN, AUTHORITIES.MANAGER]),
  );

  return (
    <Row>
      <Col md="8">
        <h2 data-cy="substrateRecipeDetailsHeading">Substrate Recipe</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{substrateRecipeEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
            <UncontrolledTooltip target="name">e.g. &#34;Standard Wheat Straw v3&#34;</UncontrolledTooltip>
          </dt>
          <dd>{substrateRecipeEntity.name}</dd>
          <dt>
            <span id="version">Version</span>
            <UncontrolledTooltip target="version">Recipe version identifier</UncontrolledTooltip>
          </dt>
          <dd>{substrateRecipeEntity.version}</dd>
          <dt>
            <span id="baseType">Base Type</span>
            <UncontrolledTooltip target="baseType">Primary substrate material</UncontrolledTooltip>
          </dt>
          <dd>{substrateRecipeEntity.baseType}</dd>
          <dt>
            <span id="compositionDetail">Composition Detail</span>
            <UncontrolledTooltip target="compositionDetail">Full ingredient list &amp; ratios</UncontrolledTooltip>
          </dt>
          <dd>{substrateRecipeEntity.compositionDetail}</dd>
          <dt>
            <span id="sterilizationMethod">Sterilization Method</span>
            <UncontrolledTooltip target="sterilizationMethod">
              e.g. &#34;Autoclave 121°C/2h&#34;, &#34;Pasteurization 80°C/8h&#34;
            </UncontrolledTooltip>
          </dt>
          <dd>{substrateRecipeEntity.sterilizationMethod}</dd>
          <dt>
            <span id="moistureTargetPercent">Moisture Target Percent</span>
            <UncontrolledTooltip target="moistureTargetPercent">Target moisture content %</UncontrolledTooltip>
          </dt>
          <dd>{substrateRecipeEntity.moistureTargetPercent}</dd>
          <dt>
            <span id="phTarget">Ph Target</span>
            <UncontrolledTooltip target="phTarget">Target pH level</UncontrolledTooltip>
          </dt>
          <dd>{substrateRecipeEntity.phTarget}</dd>
          <dt>
            <span id="supplementNotes">Supplement Notes</span>
            <UncontrolledTooltip target="supplementNotes">Supplements: gypsum, bran, lime, etc.</UncontrolledTooltip>
          </dt>
          <dd>{substrateRecipeEntity.supplementNotes}</dd>
          <dt>
            <span id="active">Active</span>
          </dt>
          <dd>{substrateRecipeEntity.active ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/substrate-recipe" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        {isAdminOrManager && (
          <Button tag={Link} to={`/substrate-recipe/${substrateRecipeEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        )}
      </Col>
    </Row>
  );
};

export default SubstrateRecipeDetail;
