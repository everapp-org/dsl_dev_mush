#!/usr/bin/env node

/**
 * Feature #279: Add aria-disabled attribute to all disabled buttons
 *
 * This script systematically adds aria-disabled={disabled} to all Button components
 * that have a disabled prop but are missing the aria-disabled attribute.
 */

const fs = require('fs');
const path = require('path');

const webappDir = path.join(__dirname, 'mcms-jhipster-base/src/main/webapp/app');
let filesModified = 0;
let filesSkipped = 0;

function processFile(filePath) {
  const content = fs.readFileSync(filePath, 'utf8');

  // Skip if file doesn't contain disabled prop
  if (!content.includes('disabled={')) {
    filesSkipped++;
    return;
  }

  // Skip if file already has aria-disabled
  if (content.includes('aria-disabled')) {
    filesSkipped++;
    return;
  }

  let modified = content;
  let changesMade = false;

  // Pattern 1: <Button ... disabled={something} ...>
  // We need to add aria-disabled={something} after the disabled prop

  // Match Button tags with disabled prop
  const buttonPattern = /(<Button\s[^>]*?)(disabled=\{([^}]+)\})([^>]*?>)/g;

  modified = modified.replace(buttonPattern, (match, before, disabledProp, disabledValue, after) => {
    // Check if aria-disabled is already present in this specific button
    if (before.includes('aria-disabled') || after.includes('aria-disabled')) {
      return match;
    }

    changesMade = true;
    // Add aria-disabled right after disabled
    return `${before}${disabledProp} aria-disabled={${disabledValue}}${after}`;
  });

  if (changesMade) {
    fs.writeFileSync(filePath, modified, 'utf8');
    console.log(`✓ Updated: ${path.relative(webappDir, filePath)}`);
    filesModified++;
  } else {
    filesSkipped++;
  }
}

function walkDir(dir) {
  const files = fs.readdirSync(dir);

  files.forEach(file => {
    const filePath = path.join(dir, file);
    const stat = fs.statSync(filePath);

    if (stat.isDirectory()) {
      walkDir(filePath);
    } else if (file.endsWith('.tsx')) {
      processFile(filePath);
    }
  });
}

console.log('🔍 Scanning for buttons with disabled prop...\n');
walkDir(webappDir);

console.log('\n📊 Summary:');
console.log(`   Files modified: ${filesModified}`);
console.log(`   Files skipped: ${filesSkipped}`);
console.log(`   Total processed: ${filesModified + filesSkipped}\n`);

if (filesModified > 0) {
  console.log('✅ aria-disabled attributes added successfully!');
} else {
  console.log('ℹ️  No changes needed - all files already compliant or no disabled buttons found.');
}
