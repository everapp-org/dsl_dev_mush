#!/usr/bin/env node

/**
 * Script to add FormErrorSummary to all entity update forms (v2 - more flexible patterns)
 */

const fs = require('fs');
const path = require('path');

const formsToUpdate = [
  'batch-material-usage',
  'contamination-event',
  'cost-record',
  'environmental-alert',
  'environmental-target',
  'flush-cycle',
  'harvest-record',
  'inventory-lot',
  'mandatory-field-check',
  'monthly-report',
  'phase-execution',
  'product',
  'sales-order-line',
  'sales-order',
  'sensor-reading',
  'sensor',
  'stock-movement',
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

  // Check if already has FormErrorSummary
  if (content.includes('FormErrorSummary')) {
    console.log(`⏭️  Skipping ${entityName} - already has FormErrorSummary`);
    return;
  }

  // Add import after the react-jhipster import (more flexible pattern)
  const importRegex = /(import \{[^}]*ValidatedField[^}]*ValidatedForm[^}]*\} from 'react-jhipster';)/;
  if (!importRegex.test(content)) {
    console.log(`⚠️  Warning: ${entityName} - react-jhipster import not found`);
    return;
  }

  content = content.replace(
    importRegex,
    `$1\nimport { FormErrorSummary } from 'app/shared/error/form-error-summary';`
  );

  // Add <FormErrorSummary /> after <ValidatedForm> opening tag (handle ref attribute)
  // Pattern: <ValidatedForm [ref={formRef}] defaultValues={defaultValues()} onSubmit={saveEntity}>
  const formOpenRegex = /(<ValidatedForm(?:\s+ref=\{[^}]+\})?\s+defaultValues=\{defaultValues\(\)\}\s+onSubmit=\{saveEntity\}>)/;
  if (!formOpenRegex.test(content)) {
    console.log(`⚠️  Warning: ${entityName} - ValidatedForm opening tag not found`);
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

console.log('\n✨ Done! FormErrorSummary added to remaining forms.');
