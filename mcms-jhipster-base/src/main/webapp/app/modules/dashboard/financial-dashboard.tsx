import React, { useEffect, useState } from 'react';
import { Row, Col, Card, CardBody, CardTitle, Button } from 'reactstrap';
import axios from 'axios';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChartLine, faDollarSign, faMoneyBillTrendUp, faPercent, faDownload } from '@fortawesome/free-solid-svg-icons';

interface FinancialDashboardData {
  totalCosts: number;
  totalRevenue: number;
  profit: number;
  profitMarginPercent: number;
  activeBatches: number;
  totalBatches: number;
  costRecordCount: number;
  salesOrderCount: number;
}

export const FinancialDashboard = () => {
  const [dashboardData, setDashboardData] = useState<FinancialDashboardData | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        const response = await axios.get<FinancialDashboardData>('/api/dashboard/financial');
        setDashboardData(response.data);
        setError(null);
      } catch (err) {
        console.error('Error fetching financial dashboard:', err);
        setError('Failed to load financial dashboard data');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
    }).format(value);
  };

  const formatPercent = (value: number) => {
    return `${value.toFixed(2)}%`;
  };

  if (loading) {
    return (
      <div className="text-center mt-5">
        <div className="spinner-border" role="status">
          <span className="sr-only">Loading...</span>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert alert-danger" role="alert">
        {error}
      </div>
    );
  }

  if (!dashboardData) {
    return <div>No data available</div>;
  }

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="mb-0">
          <FontAwesomeIcon icon={faChartLine} className="me-2" />
          Financial Dashboard
        </h2>
        <Button color="success" tag="a" href="/api/dashboard/financial/export">
          <FontAwesomeIcon icon={faDownload} className="me-2" />
          Export to CSV
        </Button>
      </div>

      <Row>
        <Col md="3" className="mb-4">
          <Card className="text-white bg-primary">
            <CardBody>
              <CardTitle tag="h5">
                <FontAwesomeIcon icon={faDollarSign} className="me-2" />
                Total Revenue
              </CardTitle>
              <h3>{formatCurrency(dashboardData.totalRevenue)}</h3>
              <small>{dashboardData.salesOrderCount} sales orders</small>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" className="mb-4">
          <Card className="text-white bg-danger">
            <CardBody>
              <CardTitle tag="h5">
                <FontAwesomeIcon icon={faDollarSign} className="me-2" />
                Total Costs
              </CardTitle>
              <h3>{formatCurrency(dashboardData.totalCosts)}</h3>
              <small>{dashboardData.costRecordCount} cost records</small>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" className="mb-4">
          <Card className={`text-white ${dashboardData.profit >= 0 ? 'bg-success' : 'bg-warning'}`}>
            <CardBody>
              <CardTitle tag="h5">
                <FontAwesomeIcon icon={faMoneyBillTrendUp} className="me-2" />
                Profit
              </CardTitle>
              <h3>{formatCurrency(dashboardData.profit)}</h3>
              <small>Net profit/loss</small>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" className="mb-4">
          <Card className={`text-white ${dashboardData.profitMarginPercent >= 0 ? 'bg-info' : 'bg-secondary'}`}>
            <CardBody>
              <CardTitle tag="h5">
                <FontAwesomeIcon icon={faPercent} className="me-2" />
                Profit Margin
              </CardTitle>
              <h3>{formatPercent(dashboardData.profitMarginPercent)}</h3>
              <small>Percentage</small>
            </CardBody>
          </Card>
        </Col>
      </Row>

      <Row className="mt-4">
        <Col md="12">
          <Card>
            <CardBody>
              <CardTitle tag="h5">Production Overview</CardTitle>
              <Row>
                <Col md="6">
                  <p>
                    <strong>Active Batches:</strong> {dashboardData.activeBatches}
                  </p>
                </Col>
                <Col md="6">
                  <p>
                    <strong>Total Batches:</strong> {dashboardData.totalBatches}
                  </p>
                </Col>
              </Row>
            </CardBody>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default FinancialDashboard;
