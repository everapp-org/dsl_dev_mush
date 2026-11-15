#!/usr/bin/env node

/**
 * Batch update script for Features #361 and #362
 * - Feature #361: Add placeholder text to dropdowns
 * - Feature #362: Add Reset button to forms
 */

const fs = require('fs');
const path = require('path');

const formsToUpdate = [
  'flush-cycle/flush-cycle-update.tsx',
  'supply-order/supply-order-update.tsx',
  'sales-order-line/sales-order-line-update.tsx',
  'harvest-record/harvest-record-update.tsx',
  'sensor/sensor-update.tsx',
  'environmental-target/environmental-target-update.tsx',
  'cost-record/cost-record-update.tsx',
  'sales-order/sales-order-update.tsx',
  'mandatory-field-check/mandatory-field-check-update.tsx',
  'phase-execution/phase-execution-update.tsx',
  'batch-material-usage/batch-material-usage-update.tsx',
  'task/task-update.tsx',
  'contamination-event/contamination-event-update.tsx',
  'sensor-reading/sensor-reading-update.tsx',
  'supply-order-line/supply-order-line-update.tsx',
  'monthly-report/monthly-report-update.tsx',
  'inventory-lot/inventory-lot-update.tsx',
  'stock-movement/stock-movement-update.tsx',
  'environmental-alert/environmental-alert-update.tsx',
];

const basePath = '/home/gpetrov/src/dsl_dev_mush/mcms-jhipster-base/src/main/webapp/app/entities';

formsToUpdate.forEach(formPath => {
  const fullPath = path.join(basePath, formPath);
  console.log(`\nProcessing: ${formPath}`);

  if (!fs.existsSync(fullPath)) {
    console.log(`  ❌ File not found: ${fullPath}`);
    return;
  }

  let content = fs.readFileSync(fullPath, 'utf8');
  let modified = false;

  // Step 1: Add useRef import if not present
  if (!content.includes('useRef')) {
    content = content.replace(
      /import React, \{ useEffect \}/,
      'import React, { useEffect, useRef }'
    );
    console.log('  ✓ Added useRef import');
    modified = true;
  }

  // Step 2: Add formRef declaration after component start
  if (!content.includes('formRef')) {
    // Find the line after updateSuccess declaration
    const updateSuccessMatch = content.match(/const updateSuccess[^;]+;/);
    if (updateSuccessMatch) {
      const insertAfter = updateSuccessMatch[0];
      content = content.replace(
        insertAfter,
        `${insertAfter}\n\n  // Feature #362: Form ref for reset functionality\n  const formRef = useRef<any>(null);`
      );
      console.log('  ✓ Added formRef declaration');
      modified = true;
    }
  }

  // Step 3: Add handleReset function before return statement
  if (!content.includes('handleReset')) {
    const returnMatch = content.match(/\n  return \(/);
    if (returnMatch) {
      const resetFunction = `
  // Feature #362: Reset button handler
  const handleReset = () => {
    if (formRef.current) {
      formRef.current.reset(defaultValues());
    }
  };
`;
      content = content.replace(/\n  return \(/, `${resetFunction}\n  return (`);
      console.log('  ✓ Added handleReset function');
      modified = true;
    }
  }

  // Step 4: Add ref to ValidatedForm
  if (!content.includes('ref={formRef}')) {
    content = content.replace(
      /<ValidatedForm defaultValues=/,
      '<ValidatedForm ref={formRef} defaultValues='
    );
    console.log('  ✓ Added ref to ValidatedForm');
    modified = true;
  }

  // Step 5: Update dropdown placeholders - find all empty option tags and add meaningful text
  // Pattern: <option value="" key="0" />
  const emptyOptionRegex = /<option value="" key="0" \/>/g;
  const matches = content.match(emptyOptionRegex);
  if (matches) {
    // For each empty option, we need to determine the entity name from context
    // This is a simplified approach - we'll look at the id/name attribute before it
    content = content.replace(
      /<ValidatedField[^>]*id="[^"]*-(\w+)"[^>]*type="select"[^>]*>\s*<option value="" key="0" \/>/g,
      (match, entityName) => {
        const placeholder = `Select ${entityName}...`;
        return match.replace('<option value="" key="0" />', `<option value="" key="0">\n                ${placeholder}\n              </option>`);
      }
    );
    console.log(`  ✓ Updated ${matches.length} dropdown placeholder(s)`);
    modified = true;
  }

  // Step 6: Add Reset button before Save button
  if (!content.includes('id="reset-form"')) {
    const saveButtonPattern = /(&nbsp;\s*<Button\s+color="primary"\s+id="save-entity")/;
    if (saveButtonPattern.test(content)) {
      content = content.replace(
        saveButtonPattern,
        `&nbsp;\n              <Button color="secondary" id="reset-form" data-cy="entityResetButton" type="button" onClick={handleReset}>\n                <FontAwesomeIcon icon="undo" />\n                &nbsp; Reset\n              </Button>\n              $1`
      );
      console.log('  ✓ Added Reset button');
      modified = true;
    }
  }

  if (modified) {
    fs.writeFileSync(fullPath, content, 'utf8');
    console.log(`  ✅ Successfully updated ${formPath}`);
  } else {
    console.log(`  ⏭️  No changes needed for ${formPath}`);
  }
});

console.log('\n✅ Batch update complete!');
