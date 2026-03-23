import './environmental-dashboard.scss';

import React, { useEffect, useState } from 'react';
import { Row, Col, Card, CardBody, CardTitle, Badge, UncontrolledTooltip } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link } from 'react-router-dom';
import axios from 'axios';

interface RoomData {
  id: number;
  name: string;
  code: string;
  roomType: string;
  status: string;
  capacityBags: number;
  currentOccupancy: number;
  areaSqM: number;
  hasHVAC: boolean;
  hasMisting: boolean;
  sensorCount: number;
  activeSensorCount: number;
  totalAlerts: number;
  unacknowledgedAlerts: number;
  criticalAlerts: number;
}

interface EnvironmentalDashboardData {
  rooms: RoomData[];
  totalRooms: number;
  activeRooms: number;
  totalSensors: number;
  activeSensors: number;
  totalAlerts: number;
  unacknowledgedAlerts: number;
  criticalAlerts: number;
}

// Helper function to get badge color based on room status (Feature #393)
const getRoomStatusBadgeColor = (status: string): string => {
  switch (status) {
    case 'ACTIVE':
      return 'success'; // green
    case 'MAINTENANCE':
      return 'warning'; // yellow
    case 'DISINFECTION':
      return 'warning'; // orange/yellow
    case 'INACTIVE':
      return 'secondary'; // gray
    default:
      return 'secondary';
  }
};

// Helper function to get card border class based on room status (Feature #403)
const getRoomStatusCardClass = (status: string): string => {
  switch (status) {
    case 'ACTIVE':
      return 'room-card-active'; // green border
    case 'MAINTENANCE':
      return 'room-card-maintenance'; // yellow border
    case 'DISINFECTION':
      return 'room-card-disinfection'; // orange border
    case 'INACTIVE':
      return 'room-card-inactive'; // gray border
    default:
      return 'room-card-inactive';
  }
};

// Helper function to get sensor status indicator color
const getSensorStatusColor = (sensorCount: number, activeSensorCount: number): string => {
  if (sensorCount === 0) return 'secondary'; // gray - no sensors
  if (activeSensorCount === sensorCount) return 'success'; // green - all active
  if (activeSensorCount > 0) return 'warning'; // yellow - some active
  return 'danger'; // red - none active
};

export const EnvironmentalDashboard = () => {
  const [data, setData] = useState<EnvironmentalDashboardData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const response = await axios.get<EnvironmentalDashboardData>('/api/dashboard/environmental');
        setData(response.data);
      } catch (error) {
        console.error('Error fetching environmental dashboard data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="environmental-dashboard-container">
        <h1>
          <FontAwesomeIcon icon="thermometer-half" /> Environmental Monitoring Dashboard
        </h1>
        <div className="text-center mt-5">
          <div className="spinner-border" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      </div>
    );
  }

  if (!data) {
    return (
      <div className="environmental-dashboard-container">
        <h1>
          <FontAwesomeIcon icon="thermometer-half" /> Environmental Monitoring Dashboard
        </h1>
        <p className="text-muted mt-4">Unable to load environmental monitoring data.</p>
      </div>
    );
  }

  return (
    <div className="environmental-dashboard-container">
      <h1>
        <FontAwesomeIcon icon="thermometer-half" /> Environmental Monitoring Dashboard
      </h1>
      <p className="lead">Real-time room and sensor status monitoring</p>

      {/* Summary KPIs */}
      <Row className="mt-4">
        <Col md="3" sm="6" className="mb-4">
          <Card className="kpi-card kpi-success">
            <CardBody>
              <CardTitle tag="h2">Active Rooms</CardTitle>
              <div className="kpi-value">
                {data.activeRooms} / {data.totalRooms}
              </div>
              <p className="kpi-description">Operational rooms</p>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" sm="6" className="mb-4">
          <Card className="kpi-card kpi-info">
            <CardBody>
              <CardTitle tag="h2">Active Sensors</CardTitle>
              <div className="kpi-value">
                {data.activeSensors} / {data.totalSensors}
              </div>
              <p className="kpi-description">Functional sensors</p>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" sm="6" className="mb-4">
          <Card className={`kpi-card ${data.unacknowledgedAlerts > 0 ? 'kpi-warning' : 'kpi-success'}`}>
            <CardBody>
              <CardTitle tag="h2">Pending Alerts</CardTitle>
              <div className="kpi-value">{data.unacknowledgedAlerts}</div>
              <p className="kpi-description">Unacknowledged alerts</p>
            </CardBody>
          </Card>
        </Col>

        <Col md="3" sm="6" className="mb-4">
          <Card className={`kpi-card ${data.criticalAlerts > 0 ? 'kpi-danger' : 'kpi-success'}`}>
            <CardBody>
              <CardTitle tag="h2">Critical Alerts</CardTitle>
              <div className="kpi-value">{data.criticalAlerts}</div>
              <p className="kpi-description">Requires immediate attention</p>
            </CardBody>
          </Card>
        </Col>
      </Row>

      {/* Room Status Grid (Feature #403) */}
      <h2 className="mt-4 mb-3">
        <FontAwesomeIcon icon="door-open" /> Room Status Grid
      </h2>

      {data.rooms.length === 0 ? (
        <p className="text-muted">No rooms configured yet.</p>
      ) : (
        <Row className="room-grid">
          {data.rooms.map(room => (
            <Col md="6" lg="4" xl="3" key={room.id} className="mb-4">
              <Card className={`room-card ${getRoomStatusCardClass(room.status)}`}>
                <CardBody>
                  <div className="d-flex justify-content-between align-items-start mb-2">
                    <div>
                      <h3 className="room-name">{room.name}</h3>
                      <span className="room-code text-muted">{room.code}</span>
                    </div>
                    <Badge color={getRoomStatusBadgeColor(room.status)} id={`room-status-${room.id}`}>
                      {room.status}
                    </Badge>
                    <UncontrolledTooltip target={`room-status-${room.id}`}>Current operational status</UncontrolledTooltip>
                  </div>

                  <div className="room-details">
                    <div className="detail-item">
                      <FontAwesomeIcon icon="layer-group" className="detail-icon" />
                      <span>
                        Type: <strong>{room.roomType}</strong>
                      </span>
                    </div>

                    <div className="detail-item">
                      <FontAwesomeIcon icon="boxes" className="detail-icon" />
                      <span>
                        Occupancy: <strong>{room.currentOccupancy || 0}</strong> / {room.capacityBags || 0} bags
                      </span>
                    </div>

                    <div className="detail-item">
                      <FontAwesomeIcon icon="ruler-combined" className="detail-icon" />
                      <span>
                        Area: <strong>{room.areaSqM || 0}</strong> m²
                      </span>
                    </div>

                    <div className="detail-item">
                      <FontAwesomeIcon
                        icon="microchip"
                        className="detail-icon"
                        id={`sensor-status-${room.id}`}
                        style={{
                          color:
                            getSensorStatusColor(room.sensorCount, room.activeSensorCount) === 'success'
                              ? '#28a745'
                              : getSensorStatusColor(room.sensorCount, room.activeSensorCount) === 'warning'
                                ? '#ffc107'
                                : getSensorStatusColor(room.sensorCount, room.activeSensorCount) === 'danger'
                                  ? '#dc3545'
                                  : '#6c757d',
                        }}
                      />
                      <span>
                        Sensors: <strong>{room.activeSensorCount}</strong> / {room.sensorCount}
                      </span>
                      <UncontrolledTooltip target={`sensor-status-${room.id}`}>
                        {room.sensorCount === 0
                          ? 'No sensors installed'
                          : room.activeSensorCount === room.sensorCount
                            ? 'All sensors active'
                            : room.activeSensorCount > 0
                              ? `${room.sensorCount - room.activeSensorCount} sensor(s) inactive`
                              : 'All sensors inactive!'}
                      </UncontrolledTooltip>
                    </div>

                    {room.unacknowledgedAlerts > 0 && (
                      <div className="detail-item alert-warning-item">
                        <FontAwesomeIcon icon="exclamation-triangle" className="detail-icon text-warning" />
                        <span>
                          <strong>{room.unacknowledgedAlerts}</strong> pending alert{room.unacknowledgedAlerts > 1 ? 's' : ''}
                          {room.criticalAlerts > 0 && <span className="text-danger"> ({room.criticalAlerts} critical)</span>}
                        </span>
                      </div>
                    )}

                    <div className="detail-item">
                      <FontAwesomeIcon
                        icon={room.hasHVAC ? 'check-circle' : 'times-circle'}
                        className="detail-icon"
                        style={{ color: room.hasHVAC ? '#28a745' : '#6c757d' }}
                      />
                      <span>HVAC: {room.hasHVAC ? 'Yes' : 'No'}</span>
                    </div>

                    <div className="detail-item">
                      <FontAwesomeIcon
                        icon={room.hasMisting ? 'check-circle' : 'times-circle'}
                        className="detail-icon"
                        style={{ color: room.hasMisting ? '#28a745' : '#6c757d' }}
                      />
                      <span>Misting: {room.hasMisting ? 'Yes' : 'No'}</span>
                    </div>
                  </div>

                  <div className="mt-3">
                    <Link to={`/room/${room.id}`} className="btn btn-sm btn-outline-primary w-100">
                      <FontAwesomeIcon icon="eye" /> View Details
                    </Link>
                  </div>
                </CardBody>
              </Card>
            </Col>
          ))}
        </Row>
      )}
    </div>
  );
};

export default EnvironmentalDashboard;
