import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/strain">
        Strain
      </MenuItem>
      <MenuItem icon="asterisk" to="/substrate-recipe">
        Substrate Recipe
      </MenuItem>
      <MenuItem icon="asterisk" to="/batch">
        Batch
      </MenuItem>
      <MenuItem icon="asterisk" to="/phase-execution">
        Phase Execution
      </MenuItem>
      <MenuItem icon="asterisk" to="/flush-cycle">
        Flush Cycle
      </MenuItem>
      <MenuItem icon="asterisk" to="/harvest-record">
        Harvest Record
      </MenuItem>
      <MenuItem icon="asterisk" to="/room">
        Room
      </MenuItem>
      <MenuItem icon="asterisk" to="/environmental-target">
        Environmental Target
      </MenuItem>
      <MenuItem icon="asterisk" to="/sensor">
        Sensor
      </MenuItem>
      <MenuItem icon="asterisk" to="/sensor-reading">
        Sensor Reading
      </MenuItem>
      <MenuItem icon="asterisk" to="/environmental-alert">
        Environmental Alert
      </MenuItem>
      <MenuItem icon="asterisk" to="/contamination-event">
        Contamination Event
      </MenuItem>
      <MenuItem icon="asterisk" to="/product">
        Product
      </MenuItem>
      <MenuItem icon="asterisk" to="/supplier">
        Supplier
      </MenuItem>
      <MenuItem icon="asterisk" to="/supply-order">
        Supply Order
      </MenuItem>
      <MenuItem icon="asterisk" to="/supply-order-line">
        Supply Order Line
      </MenuItem>
      <MenuItem icon="asterisk" to="/customer">
        Customer
      </MenuItem>
      <MenuItem icon="asterisk" to="/sales-order">
        Sales Order
      </MenuItem>
      <MenuItem icon="asterisk" to="/sales-order-line">
        Sales Order Line
      </MenuItem>
      <MenuItem icon="asterisk" to="/material">
        Material
      </MenuItem>
      <MenuItem icon="asterisk" to="/inventory-lot">
        Inventory Lot
      </MenuItem>
      <MenuItem icon="asterisk" to="/stock-movement">
        Stock Movement
      </MenuItem>
      <MenuItem icon="asterisk" to="/batch-material-usage">
        Batch Material Usage
      </MenuItem>
      <MenuItem icon="asterisk" to="/cost-record">
        Cost Record
      </MenuItem>
      <MenuItem icon="asterisk" to="/mandatory-field-check">
        Mandatory Field Check
      </MenuItem>
      <MenuItem icon="asterisk" to="/monthly-report">
        Monthly Report
      </MenuItem>
      <MenuItem icon="asterisk" to="/task">
        Task
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
