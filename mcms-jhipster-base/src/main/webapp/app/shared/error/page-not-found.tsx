import React from 'react';
import { Link } from 'react-router-dom';
import { Button, Alert } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

const PageNotFound = () => {
  return (
    <div>
      <Alert color="danger">
        <h4>Page Not Found</h4>
        <p>The page you are looking for does not exist.</p>
      </Alert>
      <div className="mt-3">
        <Button tag={Link} to="/" color="primary">
          <FontAwesomeIcon icon="home" /> Return to Dashboard
        </Button>
      </div>
    </div>
  );
};

export default PageNotFound;
