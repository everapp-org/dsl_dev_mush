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
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardStats = async () => {
      try {
        setLoading(true);
        // Fetch basic statistics from existing endpoints
        const [batchesRes, strainsRes, roomsRes, tasksRes] = await Promise.all([
          axios.get('/api/batches?size=1000'),
          axios.get('/api/strains?size=1000'),
          axios.get('/api/rooms?size=1000'),
          axios.get('/api/tasks?size=1000'),
        ]);

        const activeBatches = batchesRes.data?.filter(b => b.isActive)?.length || 0;
        const totalStrains = strainsRes.data?.length || 0;
        const totalRooms = roomsRes.data?.length || 0;
        const totalTasks = tasksRes.data?.filter(t => !t.completed)?.length || 0;

        setStats({
          activeBatches,
          totalStrains,
          totalRooms,
          totalTasks,
        });
      } catch (error) {
        console.error('Error fetching dashboard stats:', error);
      } finally {
        setLoading(false);
      }
    };

    if (account?.login) {
      fetchDashboardStats();
    }
  }, [account]);

  return (
    <div className="dashboard-container">
      <h2>
        <FontAwesomeIcon icon="dashboard" /> Dashboard
      </h2>
      <p className="lead">Welcome, {account?.firstName || account?.login}!</p>

      <Row className="mt-4">
        <Col md="3" sm="6" className="mb-4">
          <Card className="kpi-card kpi-primary">
            <CardBody>
              <CardTitle tag="h6">Active Batches</CardTitle>
              {loading ? (
                <div className="spinner-border spinner-border-sm" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
              ) : (
                <h2 className="kpi-value">{stats.activeBatches}</h2>
              )}
              <p className="kpi-description">Currently in production</p>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" sm="6" className="mb-4">
          <Card className="kpi-card kpi-success">
            <CardBody>
              <CardTitle tag="h6">Strains</CardTitle>
              {loading ? (
                <div className="spinner-border spinner-border-sm" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
              ) : (
                <h2 className="kpi-value">{stats.totalStrains}</h2>
              )}
              <p className="kpi-description">Available strains</p>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" sm="6" className="mb-4">
          <Card className="kpi-card kpi-info">
            <CardBody>
              <CardTitle tag="h6">Rooms</CardTitle>
              {loading ? (
                <div className="spinner-border spinner-border-sm" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
              ) : (
                <h2 className="kpi-value">{stats.totalRooms}</h2>
              )}
              <p className="kpi-description">Growing rooms</p>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" sm="6" className="mb-4">
          <Card className="kpi-card kpi-warning">
            <CardBody>
              <CardTitle tag="h6">Pending Tasks</CardTitle>
              {loading ? (
                <div className="spinner-border spinner-border-sm" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
              ) : (
                <h2 className="kpi-value">{stats.totalTasks}</h2>
              )}
              <p className="kpi-description">Tasks to complete</p>
            </CardBody>
          </Card>
        </Col>
      </Row>

      <Row className="mt-4">
        <Col md="12">
          <Card>
            <CardBody>
              <CardTitle tag="h5">
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
