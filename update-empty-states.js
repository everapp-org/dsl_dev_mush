#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

// Entity configurations
const entities = [
  // Rich empty states (already have detailed messages)
  { file: 'batch/batch.tsx', name: 'Batch', plural: 'batches', hasCreateButton: true, needsReferrerSearch: true },
  { file: 'strain/strain.tsx', name: 'Strain', plural: 'strains', hasCreateButton: true },

  // Simple empty states (just alert warnings)
  { file: 'product/product.tsx', name: 'Product', plural: 'products', hasCreateButton: true },
  { file: 'customer/customer.tsx', name: 'Customer', plural: 'customers', hasCreateButton: true },
  { file: 'supplier/supplier.tsx', name: 'Supplier', plural: 'suppliers', hasCreateButton: true },
  { file: 'room/room.tsx', name: 'Room', plural: 'rooms', hasCreateButton: true },
  { file: 'material/material.tsx', name: 'Material', plural: 'materials', hasCreateButton: true },
  { file: 'task/task.tsx', name: 'Task', plural: 'tasks', hasCreateButton: true },
  { file: 'sales-order/sales-order.tsx', name: 'Sales Order', plural: 'sales orders', hasCreateButton: true },
  { file: 'harvest-record/harvest-record.tsx', name: 'Harvest Record', plural: 'harvest records', hasCreateButton: true },
  { file: 'substrate-recipe/substrate-recipe.tsx', name: 'Substrate Recipe', plural: 'substrate recipes', hasCreateButton: true },
];

const entitiesDir = 'mcms-jhipster-base/src/main/webapp/app/entities';
let updatedCount = 0;

entities.forEach(entity => {
  const filePath = path.join(entitiesDir, entity.file);

  if (!fs.existsSync(filePath)) {
    console.log(`⚠ Skipped (not found): ${filePath}`);
    return;
  }

  let content = fs.readFileSync(filePath, 'utf8');
  let modified = false;

  // Add EmptyState import if not already present
  if (!content.includes('EmptyState')) {
    // Find the last import statement before the export
    const importRegex = /(import.*from.*;\n)(\nexport)/;
    if (importRegex.test(content)) {
      content = content.replace(
        importRegex,
        `$1import { EmptyState } from 'app/shared/components/EmptyState';\n$2`
      );
      modified = true;
    }
  }

  // Pattern 1: Replace rich empty state (batch.tsx, strain.tsx)
  const richEmptyStateRegex = /\) : \(\s*<>\s*<h4>No.*?Found<\/h4>\s*<p[^>]*>.*?<\/p>\s*(<Link[^>]*>.*?<\/Link>|{.*?<Link.*?<\/Link>.*?})\s*<\/>\s*\)/s;

  if (richEmptyStateRegex.test(content)) {
    const createLinkState = entity.needsReferrerSearch ? ' createLinkState={{ referrerSearch: pageLocation.search }}' : '';
    const replacement = `) : (\n          <EmptyState\n            entityName="${entity.name}"\n            entityNamePlural="${entity.plural}"\n            showCreateButton={${entity.hasCreateButton ? 'canModifyBatch' : 'true'}}\n            createLink="/${entity.file.split('/')[0]}/new"${createLinkState}\n          />\n        )`;
    content = content.replace(richEmptyStateRegex, replacement);
    modified = true;
  }

  // Pattern 2: Replace simple alert warning empty state
  const simpleEmptyStateRegex = /\) : \(\s*!loading && <div className="alert alert-warning">No [^<]+<\/div>\s*\)/;

  if (simpleEmptyStateRegex.test(content)) {
    const entityPath = entity.file.split('/')[0];
    const replacement = `) : (\n          !loading && (\n            <EmptyState\n              entityName="${entity.name}"\n              entityNamePlural="${entity.plural}"\n              showCreateButton={${entity.hasCreateButton}}\n              createLink="/${entityPath}/new"\n            />\n          )\n        )`;
    content = content.replace(simpleEmptyStateRegex, replacement);
    modified = true;
  }

  if (modified) {
    fs.writeFileSync(filePath, content, 'utf8');
    console.log(`✓ Updated: ${filePath}`);
    updatedCount++;
  } else {
    console.log(`○ No change needed: ${filePath}`);
  }
});

console.log(`\nTotal files updated: ${updatedCount}`);
