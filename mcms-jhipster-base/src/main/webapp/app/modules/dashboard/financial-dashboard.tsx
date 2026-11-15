import React, { useEffect, useState } from 'react';
import { Row, Col, Card, CardBody, CardTitle, Button } from 'reactstrap';
import axios from 'axios';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChartLine, faDollarSign, faMoneyBillTrendUp, faPercent, faDownload } from '@fortawesome/free-solid-svg-icons';
import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  Legend,
  Tooltip,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  LineChart,
  Line,
} from 'recharts';

interface FinancialDashboardData {
  totalCosts: number;
  totalRevenue: number;
  profit: number;
  profitMarginPercent: number;
  activeBatches: number;
  totalBatches: number;
  costRecordCount: number;
  salesOrderCount: number;
  costBreakdown: { [key: string]: number };
  revenueTrend: Array<{ month: string; revenue: number }>;
  profitability: Array<{ month: string; revenue: number; costs: number; profit: number }>;
}

// Professional color palette for charts
const CHART_COLORS = {
  primary: '#007bff',
  success: '#28a745',
  danger: '#dc3545',
  warning: '#ffc107',
  info: '#17a2b8',
  secondary: '#6c757d',
  purple: '#6f42c1',
  orange: '#fd7e14',
  teal: '#20c997',
  indigo: '#6610f2',
};

const COST_CATEGORY_COLORS: { [key: string]: string } = {
  SUBSTRATE: CHART_COLORS.primary,
  SPAWN: CHART_COLORS.success,
  LABOR: CHART_COLORS.warning,
  UTILITIES: CHART_COLORS.info,
  PACKAGING: CHART_COLORS.purple,
  SHIPPING: CHART_COLORS.orange,
  EQUIPMENT: CHART_COLORS.teal,
  OTHER: CHART_COLORS.secondary,
};

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

  // Format category name for display
  const formatCategoryName = (category: string) => {
    return category
      .toLowerCase()
      .split('_')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  };

  // Transform cost breakdown for pie chart
  const getCostBreakdownData = () => {
    if (!dashboardData?.costBreakdown) return [];
    return Object.entries(dashboardData.costBreakdown).map(([category, value]) => ({
      name: formatCategoryName(category),
      value: Number(value),
      category,
    }));
  };

  // Custom tooltip for currency values
  const CustomTooltip = ({ active, payload }: any) => {
    if (active && payload && payload.length) {
      return (
        <div
          style={{
            backgroundColor: 'white',
            padding: '10px',
            border: '1px solid #ccc',
            borderRadius: '4px',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
          }}
        >
          {payload.map((entry: any, index: number) => (
            <p key={index} style={{ margin: '4px 0', color: entry.color, fontWeight: 'bold' }}>
              {entry.name}: {formatCurrency(entry.value)}
            </p>
          ))}
        </div>
      );
    }
    return null;
  };

  // Custom tooltip for pie chart
  const PieTooltip = ({ active, payload }: any) => {
    if (active && payload && payload.length) {
      const data = payload[0];
      return (
        <div
          style={{
            backgroundColor: 'white',
            padding: '10px',
            border: '1px solid #ccc',
            borderRadius: '4px',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
          }}
        >
          <p style={{ margin: '4px 0', fontWeight: 'bold' }}>{data.name}</p>
          <p style={{ margin: '4px 0', color: data.payload.fill }}>{formatCurrency(data.value)}</p>
        </div>
      );
    }
    return null;
  };

  // Custom label for pie chart
  const renderCustomizedLabel = (entry: any) => {
    const percent = ((entry.value / entry.payload.total) * 100).toFixed(1);
    return `${percent}%`;
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

  const costBreakdownData = getCostBreakdownData();
  const totalCostSum = costBreakdownData.reduce((sum, item) => sum + item.value, 0);
  const costBreakdownWithTotal = costBreakdownData.map(item => ({ ...item, total: totalCostSum }));

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1 className="mb-0">
          <FontAwesomeIcon icon={faChartLine} className="me-2" />
          Financial Dashboard
        </h1>
        <Button color="success" tag="a" href="/api/dashboard/financial/export">
          <FontAwesomeIcon icon={faDownload} className="me-2" />
          Export to CSV
        </Button>
      </div>

      {/* KPI Cards */}
      <Row>
        <Col md="3" className="mb-4">
          <Card className="text-white bg-primary">
            <CardBody>
              <CardTitle tag="h2">
                <FontAwesomeIcon icon={faDollarSign} className="me-2" />
                Total Revenue
              </CardTitle>
              <div className="display-6">{formatCurrency(dashboardData.totalRevenue)}</div>
              <small>{dashboardData.salesOrderCount} sales orders</small>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" className="mb-4">
          <Card className="text-white bg-danger">
            <CardBody>
              <CardTitle tag="h2">
                <FontAwesomeIcon icon={faDollarSign} className="me-2" />
                Total Costs
              </CardTitle>
              <div className="display-6">{formatCurrency(dashboardData.totalCosts)}</div>
              <small>{dashboardData.costRecordCount} cost records</small>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" className="mb-4">
          <Card className={`text-white ${dashboardData.profit >= 0 ? 'bg-success' : 'bg-warning'}`}>
            <CardBody>
              <CardTitle tag="h2">
                <FontAwesomeIcon icon={faMoneyBillTrendUp} className="me-2" />
                Profit
              </CardTitle>
              <div className="display-6">{formatCurrency(dashboardData.profit)}</div>
              <small>Net profit/loss</small>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" className="mb-4">
          <Card className={`text-white ${dashboardData.profitMarginPercent >= 0 ? 'bg-info' : 'bg-secondary'}`}>
            <CardBody>
              <CardTitle tag="h2">
                <FontAwesomeIcon icon={faPercent} className="me-2" />
                Profit Margin
              </CardTitle>
              <div className="display-6">{formatPercent(dashboardData.profitMarginPercent)}</div>
              <small>Percentage</small>
            </CardBody>
          </Card>
        </Col>
      </Row>

      {/* Charts Row */}
      <Row className="mt-4">
        {/* Cost Breakdown Pie Chart */}
        <Col md="4" className="mb-4">
          <Card>
            <CardBody>
              <CardTitle tag="h3" className="text-center mb-3">
                Cost Breakdown by Category
              </CardTitle>
              {costBreakdownData.length > 0 ? (
                <ResponsiveContainer width="100%" height={350}>
                  <PieChart>
                    <Pie
                      data={costBreakdownWithTotal}
                      dataKey="value"
                      nameKey="name"
                      cx="50%"
                      cy="50%"
                      outerRadius={100}
                      label={renderCustomizedLabel}
                      labelLine={false}
                    >
                      {costBreakdownData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COST_CATEGORY_COLORS[entry.category] || CHART_COLORS.secondary} />
                      ))}
                    </Pie>
                    <Tooltip content={<PieTooltip />} />
                    <Legend verticalAlign="bottom" height={36} formatter={value => <span style={{ fontSize: '0.9rem' }}>{value}</span>} />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <div className="text-center text-muted py-5">No cost data available</div>
              )}
            </CardBody>
          </Card>
        </Col>

        {/* Revenue Trend Line Chart */}
        <Col md="4" className="mb-4">
          <Card>
            <CardBody>
              <CardTitle tag="h3" className="text-center mb-3">
                Revenue Trend
              </CardTitle>
              {dashboardData.revenueTrend && dashboardData.revenueTrend.length > 0 ? (
                <ResponsiveContainer width="100%" height={350}>
                  <LineChart data={dashboardData.revenueTrend} margin={{ top: 10, right: 20, left: 20, bottom: 30 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                    <XAxis dataKey="month" angle={-45} textAnchor="end" height={80} tick={{ fontSize: 12 }} stroke="#666" />
                    <YAxis tickFormatter={value => `$${(value / 1000).toFixed(0)}k`} tick={{ fontSize: 12 }} stroke="#666" />
                    <Tooltip content={<CustomTooltip />} />
                    <Legend
                      wrapperStyle={{ paddingTop: '10px' }}
                      formatter={value => <span style={{ fontSize: '0.9rem' }}>{value}</span>}
                    />
                    <Line
                      type="monotone"
                      dataKey="revenue"
                      name="Revenue"
                      stroke={CHART_COLORS.primary}
                      strokeWidth={3}
                      dot={{ r: 5, fill: CHART_COLORS.primary }}
                      activeDot={{ r: 7 }}
                    />
                  </LineChart>
                </ResponsiveContainer>
              ) : (
                <div className="text-center text-muted py-5">No revenue data available</div>
              )}
            </CardBody>
          </Card>
        </Col>

        {/* Profitability Bar Chart */}
        <Col md="4" className="mb-4">
          <Card>
            <CardBody>
              <CardTitle tag="h3" className="text-center mb-3">
                Monthly Profitability
              </CardTitle>
              {dashboardData.profitability && dashboardData.profitability.length > 0 ? (
                <ResponsiveContainer width="100%" height={350}>
                  <BarChart data={dashboardData.profitability} margin={{ top: 10, right: 20, left: 20, bottom: 30 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                    <XAxis dataKey="month" angle={-45} textAnchor="end" height={80} tick={{ fontSize: 12 }} stroke="#666" />
                    <YAxis tickFormatter={value => `$${(value / 1000).toFixed(0)}k`} tick={{ fontSize: 12 }} stroke="#666" />
                    <Tooltip content={<CustomTooltip />} />
                    <Legend
                      wrapperStyle={{ paddingTop: '10px' }}
                      formatter={value => <span style={{ fontSize: '0.9rem' }}>{value}</span>}
                    />
                    <Bar dataKey="revenue" name="Revenue" fill={CHART_COLORS.primary} radius={[8, 8, 0, 0]} />
                    <Bar dataKey="costs" name="Costs" fill={CHART_COLORS.danger} radius={[8, 8, 0, 0]} />
                    <Bar dataKey="profit" name="Profit" fill={CHART_COLORS.success} radius={[8, 8, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              ) : (
                <div className="text-center text-muted py-5">No profitability data available</div>
              )}
            </CardBody>
          </Card>
        </Col>
      </Row>

      {/* Production Overview */}
      <Row className="mt-4">
        <Col md="12">
          <Card>
            <CardBody>
              <CardTitle tag="h2">Production Overview</CardTitle>
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
