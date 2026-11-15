#!/usr/bin/env node

/**
 * Script to add FormErrorSummary to all entity update forms
 * that don't already have it for consistent form error display.
 */

const fs = require('fs');
const path = require('path');

const formsToUpdate = [
  'batch-material-usage',
  'contamination-event',
  'cost-record',
  'customer',
  'environmental-alert',
  'environmental-target',
  'flush-cycle',
  'harvest-record',
  'inventory-lot',
  'mandatory-field-check',
  'material',
  'monthly-report',
  'phase-execution',
  'product',
  'room',
  'sales-order-line',
  'sales-order',
  'sensor-reading',
  'sensor',
  'stock-movement',
  'substrate-recipe',
  'supply-order-line',
  'supply-order',
  'task',
];

const basePath = './mcms-jhipster-base/src/main/webapp/app/entities';

formsToUpdate.forEach(entityName => {
  const filePath = path.join(basePath, entityName, `${entityName}-update.tsx`);

  if (!fs.existsSync(filePath)) {
    console.log(`❌ File not found: ${filePath}`);
    return;
  }

  let content = fs.readFileSync(filePath, 'utf8');

  // Check if already has FormErrorSummary (shouldn't, but just in case)
  if (content.includes('FormErrorSummary')) {
    console.log(`⏭️  Skipping ${entityName} - already has FormErrorSummary`);
    return;
  }

  // Add import after the react-jhipster import
  const importRegex = /(import \{ ValidatedField, ValidatedForm \} from 'react-jhipster';)/;
  if (!importRegex.test(content)) {
    console.log(`⚠️  Warning: ${entityName} - react-jhipster import not found in expected format`);
    return;
  }

  content = content.replace(
    importRegex,
    `$1\nimport { FormErrorSummary } from 'app/shared/error/form-error-summary';`
  );

  // Add <FormErrorSummary /> after <ValidatedForm> opening tag
  // Look for the pattern: <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
  const formOpenRegex = /(<ValidatedForm defaultValues=\{defaultValues\(\)\} onSubmit=\{saveEntity\}>)/;
  if (!formOpenRegex.test(content)) {
    console.log(`⚠️  Warning: ${entityName} - ValidatedForm opening tag not found in expected format`);
    return;
  }

  content = content.replace(
    formOpenRegex,
    `$1\n              <FormErrorSummary />`
  );

  // Write the updated content back
  fs.writeFileSync(filePath, content, 'utf8');
  console.log(`✅ Updated ${entityName}`);
});

console.log('\n✨ Done! FormErrorSummary added to all forms.');
