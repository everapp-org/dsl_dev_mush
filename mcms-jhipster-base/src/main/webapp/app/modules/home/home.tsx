import './home.scss';

import React from 'react';
import { Link } from 'react-router-dom';

import { Alert, Col, Row } from 'reactstrap';

import { useAppSelector } from 'app/config/store';
import Dashboard from 'app/modules/dashboard/dashboard';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);

  // If user is authenticated, show dashboard
  if (isAuthenticated && account?.login) {
    return <Dashboard />;
  }

  // Otherwise, show login page
  return (
    <Row>
      <Col md="3" className="pad">
        <span className="hipster rounded" />
      </Col>
      <Col md="9">
        <h1 className="display-4">Mushroom Cultivation Management System</h1>
        <p className="lead">Commercial Mushroom Farming Management</p>
        <div>
          <Alert color="warning">
            Please
            <span>&nbsp;</span>
            <Link to="/login" className="alert-link">
              sign in
            </Link>
            <span>&nbsp;</span>
            to access the system.
          </Alert>
        </div>
      </Col>
    </Row>
  );
};

export default Home;
