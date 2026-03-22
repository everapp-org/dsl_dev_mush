#!/usr/bin/env node
/**
 * Feature #299: Button disabled during submit
 *
 * This script updates all entity *-update.tsx files to show:
 * 1. Button disabled during submit
 * 2. Button text changes to "Saving..."
 * 3. Spinner icon while saving
 */

const fs = require('fs');
const path = require('path');

const walkDir = (dir, callback) => {
  fs.readdirSync(dir).forEach(f => {
    let dirPath = path.join(dir, f);
    let isDirectory = fs.statSync(dirPath).isDirectory();
    isDirectory ? walkDir(dirPath, callback) : callback(path.join(dir, f));
  });
};

const updateSaveButton = (filePath) => {
  let content = fs.readFileSync(filePath, 'utf8');
  let modified = false;

  // Check if already fully implemented
  if (content.includes(`updating ? 'Saving...' : 'Save'`)) {
    console.log(`⏭️  Already updated: ${path.basename(filePath)}`);
    return false;
  }

  // Pattern: Simple save button without dynamic text
  // Match multi-line button with disabled={updating}
  const pattern = /(<Button[\s\S]*?id="save-entity"[\s\S]*?disabled=\{updating\}[\s\S]*?>[\s\S]*?<FontAwesomeIcon icon=)"save"( \/>[\s\S]*?&nbsp; )Save([\s\S]*?<\/Button>)/;

  const match = content.match(pattern);
  if (match) {
    const replacement = `$1{updating ? 'spinner' : 'save'} spin={updating}$2{updating ? 'Saving...' : 'Save'}$3`;
    content = content.replace(pattern, replacement);
    modified = true;
    console.log(`✅ Updated: ${path.basename(filePath)}`);
    fs.writeFileSync(filePath, content, 'utf8');
    return true;
  }

  console.log(`⚠️  Skipped: ${path.basename(filePath)}`);
  return false;
};

// Find all entity update files
const entitiesDir = 'mcms-jhipster-base/src/main/webapp/app/entities';
let updated = 0;
let skipped = 0;
let alreadyDone = 0;

walkDir(entitiesDir, (filePath) => {
  if (filePath.endsWith('-update.tsx')) {
    const result = updateSaveButton(filePath);
    if (result === true) {
      updated++;
    } else if (filePath.includes(`updating ? 'Saving...'`)) {
      alreadyDone++;
    } else {
      skipped++;
    }
  }
});

console.log(`\n✅ Updated: ${updated}`);
console.log(`⏭️  Already done: ${alreadyDone}`);
console.log(`⚠️  Skipped: ${skipped}`);
