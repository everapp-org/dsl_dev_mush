import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip, Table } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { TextFormat } from 'react-jhipster';
import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';
import { IBatch } from 'app/shared/model/batch.model';
import axios from 'axios';

import { getEntity } from './substrate-recipe.reducer';

export const SubstrateRecipeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();
  const [relatedBatches, setRelatedBatches] = useState<IBatch[]>([]);
  const [loadingBatches, setLoadingBatches] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));

    // Fetch related batches
    if (id) {
      setLoadingBatches(true);
      axios
        .get<IBatch[]>(`api/substrate-recipes/${id}/batches`)
        .then(response => {
          setRelatedBatches(response.data);
          setLoadingBatches(false);
        })
        .catch(() => {
          setLoadingBatches(false);
        });
    }
  }, [id]);

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
        {/* Related Batches Section */}
        <h3 className="mt-4">Related Batches</h3>
        {loadingBatches ? (
          <div className="text-center">
            <div className="spinner-border" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        ) : relatedBatches.length === 0 ? (
          <div className="alert alert-warning">No batches have been created using this recipe yet.</div>
        ) : (
          <Table responsive>
            <thead>
              <tr>
                <th>Batch Code</th>
                <th>Start Date</th>
                <th>Current Phase</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {relatedBatches.map(batch => (
                <tr key={batch.id}>
                  <td>
                    <Link to={`/batch/${batch.id}`}>{batch.batchCode}</Link>
                  </td>
                  <td>
                    {batch.startDate ? (
                      <TextFormat type="date" value={batch.startDate as unknown as string} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{batch.currentPhase}</td>
                  <td>
                    <span className={`badge ${batch.isActive ? 'bg-success' : 'bg-secondary'}`}>
                      {batch.isActive ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td>
                    <Button tag={Link} to={`/batch/${batch.id}`} color="info" size="sm">
                      <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        )}
      </Col>
    </Row>
  );
};

export default SubstrateRecipeDetail;
