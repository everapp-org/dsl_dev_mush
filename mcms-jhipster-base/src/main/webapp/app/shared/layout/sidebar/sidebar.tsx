import './sidebar.scss';

import React, { useState } from 'react';
import { NavLink as Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Collapse } from 'reactstrap';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

export interface ISidebarProps {
  isAuthenticated: boolean;
}

const Sidebar = (props: ISidebarProps) => {
  const [isOpen, setIsOpen] = useState(true);
  const [openSections, setOpenSections] = useState({
    dashboard: false,
    production: true,
    facility: false,
    quality: false,
    supplyChain: false,
    inventory: false,
    products: false,
    finance: false,
    tasks: false,
    administration: false,
  });

  const isAdminOrManager = useAppSelector(state =>
    hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN, AUTHORITIES.MANAGER]),
  );

  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));

  const toggleSidebar = () => setIsOpen(!isOpen);

  const toggleSection = section => {
    setOpenSections(prev => ({
      ...prev,
      [section]: !prev[section],
    }));
  };

  if (!props.isAuthenticated) {
    return null;
  }

  return (
    <div className={`sidebar ${isOpen ? 'sidebar-open' : 'sidebar-closed'}`}>
      <div className="sidebar-toggle" onClick={toggleSidebar}>
        <FontAwesomeIcon icon={isOpen ? 'chevron-left' : 'chevron-right'} />
      </div>

      {isOpen && (
        <div className="sidebar-content">
          {/* Dashboard */}
          <div className="sidebar-section">
            <div className="sidebar-section-header" onClick={() => toggleSection('dashboard')}>
              <FontAwesomeIcon icon="tachometer-alt" className="section-icon" />
              <span className="section-title">Dashboard</span>
              <FontAwesomeIcon icon={openSections.dashboard ? 'chevron-down' : 'chevron-right'} className="chevron-icon" />
            </div>
            <Collapse isOpen={openSections.dashboard}>
              <div className="sidebar-items">
                <Link to="/" className="sidebar-item" title="Home">
                  <FontAwesomeIcon icon="home" className="item-icon" />
                  <span>Home</span>
                </Link>
              </div>
            </Collapse>
          </div>

          {/* Production */}
          <div className="sidebar-section">
            <div className="sidebar-section-header" onClick={() => toggleSection('production')}>
              <FontAwesomeIcon icon="industry" className="section-icon" />
              <span className="section-title">Production</span>
              <FontAwesomeIcon icon={openSections.production ? 'chevron-down' : 'chevron-right'} className="chevron-icon" />
            </div>
            <Collapse isOpen={openSections.production}>
              <div className="sidebar-items">
                <Link to="/batch" className="sidebar-item" title="Batches">
                  <FontAwesomeIcon icon="boxes" className="item-icon" />
                  <span>Batches</span>
                </Link>
                <Link to="/strain" className="sidebar-item" title="Strains">
                  <FontAwesomeIcon icon="dna" className="item-icon" />
                  <span>Strains</span>
                </Link>
                <Link to="/substrate-recipe" className="sidebar-item" title="Substrate Recipes">
                  <FontAwesomeIcon icon="flask" className="item-icon" />
                  <span>Substrate Recipes</span>
                </Link>
                <Link to="/phase-execution" className="sidebar-item" title="Phase Executions">
                  <FontAwesomeIcon icon="tasks" className="item-icon" />
                  <span>Phase Executions</span>
                </Link>
                <Link to="/flush-cycle" className="sidebar-item" title="Flush Cycles">
                  <FontAwesomeIcon icon="sync" className="item-icon" />
                  <span>Flush Cycles</span>
                </Link>
                <Link to="/harvest-record" className="sidebar-item" title="Harvest Records">
                  <FontAwesomeIcon icon="leaf" className="item-icon" />
                  <span>Harvest Records</span>
                </Link>
              </div>
            </Collapse>
          </div>

          {/* Facility */}
          <div className="sidebar-section">
            <div className="sidebar-section-header" onClick={() => toggleSection('facility')}>
              <FontAwesomeIcon icon="building" className="section-icon" />
              <span className="section-title">Facility</span>
              <FontAwesomeIcon icon={openSections.facility ? 'chevron-down' : 'chevron-right'} className="chevron-icon" />
            </div>
            <Collapse isOpen={openSections.facility}>
              <div className="sidebar-items">
                <Link to="/room" className="sidebar-item" title="Rooms">
                  <FontAwesomeIcon icon="door-open" className="item-icon" />
                  <span>Rooms</span>
                </Link>
                <Link to="/sensor" className="sidebar-item" title="Sensors">
                  <FontAwesomeIcon icon="microchip" className="item-icon" />
                  <span>Sensors</span>
                </Link>
                <Link to="/sensor-reading" className="sidebar-item" title="Sensor Readings">
                  <FontAwesomeIcon icon="chart-line" className="item-icon" />
                  <span>Sensor Readings</span>
                </Link>
                <Link to="/environmental-target" className="sidebar-item" title="Environmental Targets">
                  <FontAwesomeIcon icon="bullseye" className="item-icon" />
                  <span>Environmental Targets</span>
                </Link>
                <Link to="/environmental-alert" className="sidebar-item" title="Environmental Alerts">
                  <FontAwesomeIcon icon="bell" className="item-icon" />
                  <span>Environmental Alerts</span>
                </Link>
              </div>
            </Collapse>
          </div>

          {/* Quality */}
          <div className="sidebar-section">
            <div className="sidebar-section-header" onClick={() => toggleSection('quality')}>
              <FontAwesomeIcon icon="check-circle" className="section-icon" />
              <span className="section-title">Quality</span>
              <FontAwesomeIcon icon={openSections.quality ? 'chevron-down' : 'chevron-right'} className="chevron-icon" />
            </div>
            <Collapse isOpen={openSections.quality}>
              <div className="sidebar-items">
                <Link to="/contamination-event" className="sidebar-item" title="Contamination Events">
                  <FontAwesomeIcon icon="exclamation-triangle" className="item-icon" />
                  <span>Contamination Events</span>
                </Link>
                <Link to="/mandatory-field-check" className="sidebar-item" title="Mandatory Checks">
                  <FontAwesomeIcon icon="clipboard-check" className="item-icon" />
                  <span>Mandatory Checks</span>
                </Link>
              </div>
            </Collapse>
          </div>

          {/* Supply Chain */}
          <div className="sidebar-section">
            <div className="sidebar-section-header" onClick={() => toggleSection('supplyChain')}>
              <FontAwesomeIcon icon="truck" className="section-icon" />
              <span className="section-title">Supply Chain</span>
              <FontAwesomeIcon icon={openSections.supplyChain ? 'chevron-down' : 'chevron-right'} className="chevron-icon" />
            </div>
            <Collapse isOpen={openSections.supplyChain}>
              <div className="sidebar-items">
                <Link to="/supplier" className="sidebar-item" title="Suppliers">
                  <FontAwesomeIcon icon="parachute-box" className="item-icon" />
                  <span>Suppliers</span>
                </Link>
                <Link to="/supply-order" className="sidebar-item" title="Supply Orders">
                  <FontAwesomeIcon icon="shopping-cart" className="item-icon" />
                  <span>Supply Orders</span>
                </Link>
                <Link to="/supply-order-line" className="sidebar-item" title="Supply Order Lines">
                  <FontAwesomeIcon icon="list" className="item-icon" />
                  <span>Supply Order Lines</span>
                </Link>
                <Link to="/customer" className="sidebar-item" title="Customers">
                  <FontAwesomeIcon icon="users" className="item-icon" />
                  <span>Customers</span>
                </Link>
                <Link to="/sales-order" className="sidebar-item" title="Sales Orders">
                  <FontAwesomeIcon icon="receipt" className="item-icon" />
                  <span>Sales Orders</span>
                </Link>
                <Link to="/sales-order-line" className="sidebar-item" title="Sales Order Lines">
                  <FontAwesomeIcon icon="list-alt" className="item-icon" />
                  <span>Sales Order Lines</span>
                </Link>
              </div>
            </Collapse>
          </div>

          {/* Inventory */}
          <div className="sidebar-section">
            <div className="sidebar-section-header" onClick={() => toggleSection('inventory')}>
              <FontAwesomeIcon icon="warehouse" className="section-icon" />
              <span className="section-title">Inventory</span>
              <FontAwesomeIcon icon={openSections.inventory ? 'chevron-down' : 'chevron-right'} className="chevron-icon" />
            </div>
            <Collapse isOpen={openSections.inventory}>
              <div className="sidebar-items">
                <Link to="/material" className="sidebar-item" title="Materials">
                  <FontAwesomeIcon icon="box" className="item-icon" />
                  <span>Materials</span>
                </Link>
                <Link to="/inventory-lot" className="sidebar-item" title="Inventory Lots">
                  <FontAwesomeIcon icon="layer-group" className="item-icon" />
                  <span>Inventory Lots</span>
                </Link>
                <Link to="/stock-movement" className="sidebar-item" title="Stock Movements">
                  <FontAwesomeIcon icon="exchange-alt" className="item-icon" />
                  <span>Stock Movements</span>
                </Link>
                <Link to="/batch-material-usage" className="sidebar-item" title="Batch Material Usage">
                  <FontAwesomeIcon icon="cogs" className="item-icon" />
                  <span>Batch Material Usage</span>
                </Link>
              </div>
            </Collapse>
          </div>

          {/* Products */}
          <div className="sidebar-section">
            <div className="sidebar-section-header" onClick={() => toggleSection('products')}>
              <FontAwesomeIcon icon="cube" className="section-icon" />
              <span className="section-title">Products</span>
              <FontAwesomeIcon icon={openSections.products ? 'chevron-down' : 'chevron-right'} className="chevron-icon" />
            </div>
            <Collapse isOpen={openSections.products}>
              <div className="sidebar-items">
                <Link to="/product" className="sidebar-item" title="Products">
                  <FontAwesomeIcon icon="shopping-bag" className="item-icon" />
                  <span>Products</span>
                </Link>
              </div>
            </Collapse>
          </div>

          {/* Finance */}
          {isAdminOrManager && (
            <div className="sidebar-section">
              <div className="sidebar-section-header" onClick={() => toggleSection('finance')}>
                <FontAwesomeIcon icon="dollar-sign" className="section-icon" />
                <span className="section-title">Finance</span>
                <FontAwesomeIcon icon={openSections.finance ? 'chevron-down' : 'chevron-right'} className="chevron-icon" />
              </div>
              <Collapse isOpen={openSections.finance}>
                <div className="sidebar-items">
                  <Link to="/cost-record" className="sidebar-item" title="Cost Records">
                    <FontAwesomeIcon icon="money-bill" className="item-icon" />
                    <span>Cost Records</span>
                  </Link>
                  <Link to="/monthly-report" className="sidebar-item" title="Monthly Reports">
                    <FontAwesomeIcon icon="file-invoice-dollar" className="item-icon" />
                    <span>Monthly Reports</span>
                  </Link>
                </div>
              </Collapse>
            </div>
          )}

          {/* Tasks */}
          <div className="sidebar-section">
            <div className="sidebar-section-header" onClick={() => toggleSection('tasks')}>
              <FontAwesomeIcon icon="clipboard-list" className="section-icon" />
              <span className="section-title">Tasks</span>
              <FontAwesomeIcon icon={openSections.tasks ? 'chevron-down' : 'chevron-right'} className="chevron-icon" />
            </div>
            <Collapse isOpen={openSections.tasks}>
              <div className="sidebar-items">
                <Link to="/task" className="sidebar-item" title="Tasks">
                  <FontAwesomeIcon icon="check-square" className="item-icon" />
                  <span>Tasks</span>
                </Link>
              </div>
            </Collapse>
          </div>

          {/* Administration */}
          {isAdmin && (
            <div className="sidebar-section">
              <div className="sidebar-section-header" onClick={() => toggleSection('administration')}>
                <FontAwesomeIcon icon="user-shield" className="section-icon" />
                <span className="section-title">Administration</span>
                <FontAwesomeIcon icon={openSections.administration ? 'chevron-down' : 'chevron-right'} className="chevron-icon" />
              </div>
              <Collapse isOpen={openSections.administration}>
                <div className="sidebar-items">
                  <Link to="/admin/user-management" className="sidebar-item" title="User Management">
                    <FontAwesomeIcon icon="users-cog" className="item-icon" />
                    <span>User Management</span>
                  </Link>
                  <Link to="/admin/metrics" className="sidebar-item" title="Metrics">
                    <FontAwesomeIcon icon="tachometer-alt" className="item-icon" />
                    <span>Metrics</span>
                  </Link>
                  <Link to="/admin/health" className="sidebar-item" title="Health">
                    <FontAwesomeIcon icon="heart" className="item-icon" />
                    <span>Health</span>
                  </Link>
                  <Link to="/admin/configuration" className="sidebar-item" title="Configuration">
                    <FontAwesomeIcon icon="cog" className="item-icon" />
                    <span>Configuration</span>
                  </Link>
                  <Link to="/admin/logs" className="sidebar-item" title="Logs">
                    <FontAwesomeIcon icon="file-alt" className="item-icon" />
                    <span>Logs</span>
                  </Link>
                  <Link to="/admin/docs" className="sidebar-item" title="API Docs">
                    <FontAwesomeIcon icon="book" className="item-icon" />
                    <span>API Docs</span>
                  </Link>
                </div>
              </Collapse>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Sidebar;
