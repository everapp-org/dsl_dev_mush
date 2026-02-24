import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

import { getEntity, activateStrain, deactivateStrain } from './strain.reducer';

export const StrainDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const strainEntity = useAppSelector(state => state.strain.entity);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));

  const handleActivate = () => {
    dispatch(activateStrain(id));
  };

  const handleDeactivate = () => {
    dispatch(deactivateStrain(id));
  };
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="strainDetailsHeading">Strain</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{strainEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
            <UncontrolledTooltip target="name">e.g. &#34;Pleurotus ostreatus - Grey Dove&#34;</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.name}</dd>
          <dt>
            <span id="species">Species</span>
            <UncontrolledTooltip target="species">Latin species name</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.species}</dd>
          <dt>
            <span id="variety">Variety</span>
            <UncontrolledTooltip target="variety">Cultivar or variant</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.variety}</dd>
          <dt>
            <span id="optimalTempMinC">Optimal Temp Min C</span>
            <UncontrolledTooltip target="optimalTempMinC">Min fruiting temp °C</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.optimalTempMinC}</dd>
          <dt>
            <span id="optimalTempMaxC">Optimal Temp Max C</span>
            <UncontrolledTooltip target="optimalTempMaxC">Max fruiting temp °C</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.optimalTempMaxC}</dd>
          <dt>
            <span id="optimalHumidityMin">Optimal Humidity Min</span>
            <UncontrolledTooltip target="optimalHumidityMin">Min RH %</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.optimalHumidityMin}</dd>
          <dt>
            <span id="optimalHumidityMax">Optimal Humidity Max</span>
            <UncontrolledTooltip target="optimalHumidityMax">Max RH %</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.optimalHumidityMax}</dd>
          <dt>
            <span id="optimalCO2MaxPpm">Optimal CO 2 Max Ppm</span>
            <UncontrolledTooltip target="optimalCO2MaxPpm">Max CO₂ ppm for fruiting</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.optimalCO2MaxPpm}</dd>
          <dt>
            <span id="colonizationDaysMin">Colonization Days Min</span>
            <UncontrolledTooltip target="colonizationDaysMin">Typical min colonization days</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.colonizationDaysMin}</dd>
          <dt>
            <span id="colonizationDaysMax">Colonization Days Max</span>
            <UncontrolledTooltip target="colonizationDaysMax">Typical max colonization days</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.colonizationDaysMax}</dd>
          <dt>
            <span id="expectedYieldPercent">Expected Yield Percent</span>
            <UncontrolledTooltip target="expectedYieldPercent">Expected biological efficiency %</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.expectedYieldPercent}</dd>
          <dt>
            <span id="shelfLifeDays">Shelf Life Days</span>
            <UncontrolledTooltip target="shelfLifeDays">Post-harvest shelf life in days</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.shelfLifeDays}</dd>
          <dt>
            <span id="note">Note</span>
            <UncontrolledTooltip target="note">Cultivation tips, observations</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.note}</dd>
          <dt>
            <span id="active">Active</span>
            <UncontrolledTooltip target="active">Is this strain currently in use?</UncontrolledTooltip>
          </dt>
          <dd>{strainEntity.active ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/strain" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/strain/${strainEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
        {isAdmin && (
          <>
            &nbsp;
            {strainEntity.active ? (
              <Button onClick={handleDeactivate} color="warning" data-cy="entityDeactivateButton">
                <FontAwesomeIcon icon="ban" /> <span className="d-none d-md-inline">Deactivate</span>
              </Button>
            ) : (
              <Button onClick={handleActivate} color="success" data-cy="entityActivateButton">
                <FontAwesomeIcon icon="check" /> <span className="d-none d-md-inline">Activate</span>
              </Button>
            )}
          </>
        )}
      </Col>
    </Row>
  );
};

export default StrainDetail;
