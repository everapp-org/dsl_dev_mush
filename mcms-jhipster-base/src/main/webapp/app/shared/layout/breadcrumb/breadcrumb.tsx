import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Breadcrumb, BreadcrumbItem } from 'reactstrap';
import './breadcrumb.scss';

// Map of route paths to display names
const routeNames: Record<string, string> = {
  '': 'Dashboard',
  strain: 'Strains',
  'substrate-recipe': 'Substrate Recipes',
  batch: 'Batches',
  'phase-execution': 'Phase Executions',
  'flush-cycle': 'Flush Cycles',
  'harvest-record': 'Harvest Records',
  room: 'Rooms',
  'environmental-target': 'Environmental Targets',
  sensor: 'Sensors',
  'sensor-reading': 'Sensor Readings',
  'environmental-alert': 'Environmental Alerts',
  'contamination-event': 'Contamination Events',
  product: 'Products',
  supplier: 'Suppliers',
  'supply-order': 'Supply Orders',
  'supply-order-line': 'Supply Order Lines',
  customer: 'Customers',
  'sales-order': 'Sales Orders',
  'sales-order-line': 'Sales Order Lines',
  material: 'Materials',
  'inventory-lot': 'Inventory Lots',
  'stock-movement': 'Stock Movements',
  'batch-material-usage': 'Batch Material Usage',
  'cost-record': 'Cost Records',
  'mandatory-field-check': 'Mandatory Field Checks',
  'monthly-report': 'Monthly Reports',
  task: 'Tasks',
  admin: 'Administration',
  account: 'Account',
  finance: 'Finance',
  new: 'New',
  edit: 'Edit',
  delete: 'Delete',
  discard: 'Discard',
  'force-transition': 'Force Transition',
  dashboard: 'Dashboard',
};

// Check if a segment is a numeric ID
const isNumericId = (segment: string): boolean => {
  return /^\d+$/.test(segment);
};

// Get display name for a route segment
const getDisplayName = (segment: string, index: number, segments: string[]): string => {
  if (isNumericId(segment)) {
    // For numeric IDs, use the parent segment name + "Detail"
    const parentSegment = segments[index - 1];
    const parentName = routeNames[parentSegment] || parentSegment;
    // Remove trailing 's' if present and add Detail
    const singularName = parentName.endsWith('s') ? parentName.slice(0, -1) : parentName;
    return `${singularName} Detail`;
  }
  return routeNames[segment] || segment.charAt(0).toUpperCase() + segment.slice(1);
};

export const BreadcrumbNav = () => {
  const location = useLocation();

  // Don't show breadcrumb on login page or home page
  if (location.pathname === '/login' || location.pathname === '/' || location.pathname === '/logout') {
    return null;
  }

  // Split pathname into segments and filter out empty ones
  const pathSegments = location.pathname.split('/').filter(segment => segment !== '');

  // If no segments, we're on home
  if (pathSegments.length === 0) {
    return null;
  }

  // Build breadcrumb items
  const breadcrumbItems = [];

  // Always start with Dashboard
  breadcrumbItems.push(
    <BreadcrumbItem key="home">
      <Link to="/">Dashboard</Link>
    </BreadcrumbItem>,
  );

  // Build path progressively and create breadcrumb items
  let currentPath = '';
  pathSegments.forEach((segment, index) => {
    currentPath += `/${segment}`;
    const isLast = index === pathSegments.length - 1;
    const displayName = getDisplayName(segment, index, pathSegments);

    if (isLast) {
      // Last item is not clickable
      breadcrumbItems.push(
        <BreadcrumbItem key={currentPath} active>
          {displayName}
        </BreadcrumbItem>,
      );
    } else {
      // Intermediate items are clickable
      breadcrumbItems.push(
        <BreadcrumbItem key={currentPath}>
          <Link to={currentPath}>{displayName}</Link>
        </BreadcrumbItem>,
      );
    }
  });

  return (
    <div className="breadcrumb-container">
      <Breadcrumb listClassName="mb-0">{breadcrumbItems}</Breadcrumb>
    </div>
  );
};

export default BreadcrumbNav;
