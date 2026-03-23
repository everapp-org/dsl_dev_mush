import './dashboard.scss';

import React, { useEffect, useState } from 'react';
import { Row, Col, Card, CardBody, CardTitle } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useAppSelector } from 'app/config/store';
import axios from 'axios';

export const Dashboard = () => {
  const account = useAppSelector(state => state.authentication.account);
  const [stats, setStats] = useState({
    activeBatches: 0,
    totalStrains: 0,
    totalRooms: 0,
    totalTasks: 0,
  });
  const [productionData, setProductionData] = useState<{
    activeBatchCount: number;
    batchesByPhase: Record<string, number>;
  } | null>(null);
  const [loading, setLoading] = useState(true);
  const [chartLoading, setChartLoading] = useState(true);

  useEffect(() => {
    const fetchAllData = async () => {
      try {
        setLoading(true);
        setChartLoading(true);

        // Batch API requests using Promise.all for parallel execution
        const [overviewResponse, productionResponse] = await Promise.all([
          axios.get('/api/dashboard/overview'),
          axios.get('/api/dashboard/production'),
        ]);

        // Update stats from overview endpoint
        const data = overviewResponse.data;
        setStats({
          activeBatches: data.activeBatches || 0,
          totalStrains: data.totalStrains || 0,
          totalRooms: data.totalRooms || 0,
          totalTasks: data.totalTasks || 0,
        });

        // Update production data
        setProductionData(productionResponse.data);
      } catch (error) {
        console.error('Error fetching dashboard data:', error);
      } finally {
        setLoading(false);
        setChartLoading(false);
      }
    };

    if (account?.login) {
      fetchAllData();
    }
  }, [account]);

  return (
    <div className="dashboard-container">
      <h1>
        <FontAwesomeIcon icon="dashboard" /> Dashboard
      </h1>
      <p className="lead">Welcome, {account?.firstName || account?.login}!</p>

      <Row className="mt-4">
        <Col md="3" sm="6" className="mb-4">
          <Card className="kpi-card kpi-primary">
            <CardBody>
              <CardTitle tag="h2">Active Batches</CardTitle>
              {loading ? (
                <div className="spinner-border spinner-border-sm" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
              ) : (
                <div className="kpi-value">{stats.activeBatches}</div>
              )}
              <p className="kpi-description">Currently in production</p>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" sm="6" className="mb-4">
          <Card className="kpi-card kpi-success">
            <CardBody>
              <CardTitle tag="h2">Strains</CardTitle>
              {loading ? (
                <div className="spinner-border spinner-border-sm" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
              ) : (
                <div className="kpi-value">{stats.totalStrains}</div>
              )}
              <p className="kpi-description">Available strains</p>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" sm="6" className="mb-4">
          <Card className="kpi-card kpi-info">
            <CardBody>
              <CardTitle tag="h2">Rooms</CardTitle>
              {loading ? (
                <div className="spinner-border spinner-border-sm" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
              ) : (
                <div className="kpi-value">{stats.totalRooms}</div>
              )}
              <p className="kpi-description">Growing rooms</p>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" sm="6" className="mb-4">
          <Card className="kpi-card kpi-warning">
            <CardBody>
              <CardTitle tag="h2">Pending Tasks</CardTitle>
              {loading ? (
                <div className="spinner-border spinner-border-sm" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
              ) : (
                <div className="kpi-value">{stats.totalTasks}</div>
              )}
              <p className="kpi-description">Tasks to complete</p>
            </CardBody>
          </Card>
        </Col>
      </Row>

      <Row className="mt-4">
        <Col md="6">
          <Card>
            <CardBody>
              <CardTitle tag="h2">
                <FontAwesomeIcon icon="chart-bar" /> Production Pipeline
              </CardTitle>
              {chartLoading ? (
                <div className="text-center">
                  <div className="spinner-border" role="status">
                    <span className="visually-hidden">Loading chart...</span>
                  </div>
                </div>
              ) : productionData ? (
                <div>
                  <p className="text-muted">Batches by Phase ({productionData.activeBatchCount} active batches)</p>
                  <div className="chart-container">
                    {Object.entries(productionData.batchesByPhase).map(([phase, count]) => (
                      <div key={phase} className="mb-2">
                        <div className="d-flex justify-content-between align-items-center">
                          <span className="phase-label">{phase.replace(/_/g, ' ')}</span>
                          <span className="badge bg-primary">{count}</span>
                        </div>
                        <div className="progress mt-1" style={{ height: '20px' }}>
                          <div
                            className="progress-bar"
                            role="progressbar"
                            style={{ width: `${(count / productionData.activeBatchCount) * 100}%` }}
                            aria-valuenow={count}
                            aria-valuemin={0}
                            aria-valuemax={productionData.activeBatchCount}
                          >
                            {count > 0 ? count : ''}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                  {Object.keys(productionData.batchesByPhase).length === 0 && <p className="text-muted">No active batches to display</p>}
                </div>
              ) : (
                <p className="text-muted">Unable to load production data</p>
              )}
            </CardBody>
          </Card>
        </Col>

        <Col md="6">
          <Card>
            <CardBody>
              <CardTitle tag="h2">
                <FontAwesomeIcon icon="chart-line" /> Quick Access
              </CardTitle>
              <p>
                This is your main dashboard. Use the sidebar navigation to access different sections of the Mushroom Cultivation Management
                System.
              </p>
              <ul>
                <li>
                  <strong>Production:</strong> Manage batches, strains, and substrate recipes
                </li>
                <li>
                  <strong>Facility:</strong> Monitor rooms, sensors, and environmental conditions
                </li>
                <li>
                  <strong>Supply Chain:</strong> Handle suppliers, customers, and orders
                </li>
                <li>
                  <strong>Quality:</strong> Track contamination events and compliance
                </li>
                <li>
                  <strong>Finance:</strong> View cost records and monthly reports
                </li>
              </ul>
            </CardBody>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;
