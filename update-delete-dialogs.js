const fs = require('fs');
const path = require('path');

const baseDir = '/home/gpetrov/src/dsl_dev_mush/mcms-jhipster-base/src/main/webapp/app/entities';

// List of entities to update (excluding batch which has custom logic, and strain/room which are already done)
const entities = [
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

let updatedCount = 0;
let skippedCount = 0;

entities.forEach(entity => {
  const filePath = path.join(baseDir, entity, `${entity}-delete-dialog.tsx`);

  if (!fs.existsSync(filePath)) {
    console.log(`⚠️  File not found: ${entity}`);
    skippedCount++;
    return;
  }

  let content = fs.readFileSync(filePath, 'utf8');

  // Skip if already has updating state
  if (content.includes('const updating = useAppSelector')) {
    console.log(`✓ Already updated: ${entity}`);
    skippedCount++;
    return;
  }

  // Skip if uses custom logic (like batch with isDeleting)
  if (content.includes('isDeleting') || content.includes('confirmDelete = async')) {
    console.log(`⚠️  Uses custom logic, skipping: ${entity}`);
    skippedCount++;
    return;
  }

  // Extract entity name for state (e.g., "batch-material-usage" -> "batchMaterialUsage")
  const stateName = entity.replace(/-([a-z])/g, (g) => g[1].toUpperCase());

  // Add updating state after updateSuccess
  content = content.replace(
    /const updateSuccess = useAppSelector\(state => state\.(\w+)\.updateSuccess\);/,
    `const updateSuccess = useAppSelector(state => state.$1.updateSuccess);\n  const updating = useAppSelector(state => state.$1.updating);`
  );

  // Update the delete button - handle both regular Button and with existing attributes
  // Pattern 1: Simple button
  content = content.replace(
    /(<Button[^>]*id="jhi-confirm-delete-[^"]*"[^>]*color="danger"[^>]*onClick=\{confirmDelete\})>/,
    `$1\n          disabled={updating}\n          aria-disabled={updating}\n        >`
  );

  // Update icon and text
  content = content.replace(
    /<FontAwesomeIcon icon="trash" \/>\s*&nbsp;\s*Delete/,
    `<FontAwesomeIcon icon={updating ? 'spinner' : 'trash'} spin={updating} />\n          &nbsp; {updating ? 'Deleting...' : 'Delete'}`
  );

  // Write back
  fs.writeFileSync(filePath, content, 'utf8');
  console.log(`✅ Updated: ${entity}`);
  updatedCount++;
});

console.log(`\n📊 Summary:`);
console.log(`   ✅ Updated: ${updatedCount}`);
console.log(`   ⏭️  Skipped: ${skippedCount}`);
console.log(`   📝 Total: ${entities.length}`);
