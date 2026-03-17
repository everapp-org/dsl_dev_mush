#!/usr/bin/env node

/**
 * Fix Heading Hierarchy Issues (Feature #277)
 *
 * WCAG 2.1 SC 1.3.1 requires proper heading hierarchy:
 * - Each page should have exactly one H1
 * - No skipped levels (H1 → H3 is invalid, should be H1 → H2 → H3)
 * - Headings should represent document structure
 *
 * Changes:
 * 1. Convert all entity page H2s to H1s (list, detail, update pages)
 * 2. Fix dashboard CardTitle hierarchy (H6 → H3, H5 → H3)
 * 3. Adjust detail page subsections (H3 stays H3 when H2 becomes H1)
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const appDir = path.join(__dirname, 'mcms-jhipster-base/src/main/webapp/app');

function getAllTsxFiles(dir, fileList = []) {
  const files = fs.readdirSync(dir);
  files.forEach(file => {
    const filePath = path.join(dir, file);
    const stat = fs.statSync(filePath);
    if (stat.isDirectory()) {
      getAllTsxFiles(filePath, fileList);
    } else if (file.endsWith('.tsx') || file.endsWith('.ts')) {
      fileList.push(filePath);
    }
  });
  return fileList;
}

function fixHeadingHierarchy() {
  console.log('🔍 Scanning for heading hierarchy issues...\n');

  const allFiles = getAllTsxFiles(appDir);
  let filesModified = 0;
  let changesApplied = 0;

  allFiles.forEach(file => {
    let content = fs.readFileSync(file, 'utf8');
    const originalContent = content;
    let fileChanges = 0;

    // Fix 1: Entity list pages - Convert H2 to H1 for main headings
    // Pattern: <h2 id="entity-heading" data-cy="EntityHeading">
    const listHeadingRegex = /<h2\s+(id="[^"]+-heading"\s+data-cy="[^"]+Heading"[^>]*)>/g;
    if (listHeadingRegex.test(content)) {
      content = content.replace(listHeadingRegex, '<h1 $1>');
      const closeTag = content.replace(/<\/h2>/g, (match, offset) => {
        // Only replace closing tag if it's for the heading we just changed
        if (content.substring(0, offset).match(/<h1[^>]*>(?![\s\S]*<h2)/)) {
          fileChanges++;
          return '</h1>';
        }
        return match;
      });
      content = closeTag;
    }

    // Reset regex
    content = fs.readFileSync(file, 'utf8');

    // Fix 2: Entity detail pages - Convert H2 to H1 for details heading
    // Pattern: <h2 data-cy="entityDetailsHeading">
    const detailHeadingRegex = /<h2(\s+data-cy="[^"]+DetailsHeading"[^>]*)>/g;
    if (detailHeadingRegex.test(content)) {
      content = content.replace(detailHeadingRegex, '<h1$1>');
      fileChanges++;
    }

    // Fix 3: Entity update pages - Convert H2 to H1 for create/edit heading
    // Pattern: <h2 id="mcmsApp.entity.home.createOrEditLabel" data-cy="EntityCreateUpdateHeading">
    const updateHeadingRegex = /<h2(\s+id="mcmsApp\.[^"]+\.home\.createOrEditLabel"\s+data-cy="[^"]+CreateUpdateHeading"[^>]*)>/g;
    if (updateHeadingRegex.test(content)) {
      content = content.replace(updateHeadingRegex, '<h1$1>');
      fileChanges++;
    }

    // Fix 4: Dashboard and other pages - Convert standalone H2 to H1
    // Pattern: <h2> at start of main content (not inside cards)
    // Target specific patterns like: <h2>\n        <FontAwesomeIcon icon="dashboard" /> Dashboard
    const dashboardHeadingRegex = /<h2>\s*(<FontAwesomeIcon[^>]*\/>\s*)?([^<]+)<\/h2>/g;
    const matches = content.match(dashboardHeadingRegex);
    if (matches && file.includes('dashboard.tsx') && !file.includes('financial-dashboard')) {
      content = content.replace(
        /<h2>(\s*<FontAwesomeIcon[^>]*\/>\s*Dashboard)<\/h2>/g,
        '<h1>$1</h1>'
      );
      fileChanges++;
    }

    // Fix 5: Dashboard CardTitle - Convert H6 to H3 (fix skipped levels)
    // Pattern: <CardTitle tag="h6">
    if (file.includes('dashboard.tsx') && content.includes('<CardTitle tag="h6">')) {
      content = content.replace(/<CardTitle tag="h6">/g, '<CardTitle tag="h3">');
      fileChanges++;
    }

    // Fix 6: Dashboard CardTitle - Convert H5 to H3 (fix skipped levels)
    // Pattern: <CardTitle tag="h5">
    if (file.includes('dashboard.tsx') && content.includes('<CardTitle tag="h5">')) {
      content = content.replace(/<CardTitle tag="h5">/g, '<CardTitle tag="h3">');
      fileChanges++;
    }

    // Fix 7: Financial dashboard - Convert H5 to H3
    if (file.includes('financial-dashboard.tsx') && content.includes('<CardTitle tag="h5">')) {
      content = content.replace(/<CardTitle tag="h5">/g, '<CardTitle tag="h3">');
      fileChanges++;
    }

    // Fix 8: Administration pages - Convert H2 to H1
    const adminHeadingRegex = /<h2(\s+id="[^"]*"\s+data-cy="[^"]*"[^>]*)>/g;
    if (file.includes('modules/administration') && adminHeadingRegex.test(content)) {
      content = content.replace(adminHeadingRegex, '<h1$1>');
      fileChanges++;
    }

    // Fix 9: Account pages - H1 already exists, verify no issues
    // These pages already use H1 correctly: activate, register, password-reset

    // Fix 10: Update corresponding closing tags
    // Count opening and closing tags to ensure balance
    const h1Opens = (content.match(/<h1[^>]*>/g) || []).length;
    const h1Closes = (content.match(/<\/h1>/g) || []).length;
    const h2Opens = (content.match(/<h2[^>]*>/g) || []).length;
    const h2Closes = (content.match(/<\/h2>/g) || []).length;

    // If we converted H2 to H1, adjust closing tags
    if (fileChanges > 0 && h1Opens > h1Closes) {
      const diff = h1Opens - h1Closes;
      let closingsReplaced = 0;
      content = content.replace(/<\/h2>/g, (match) => {
        if (closingsReplaced < diff) {
          closingsReplaced++;
          return '</h1>';
        }
        return match;
      });
    }

    // Write back if changed
    if (content !== originalContent && fileChanges > 0) {
      fs.writeFileSync(file, content, 'utf8');
      filesModified++;
      changesApplied += fileChanges;
      const relativePath = path.relative(__dirname, file);
      console.log(`✅ ${relativePath} (${fileChanges} change${fileChanges > 1 ? 's' : ''})`);
    }
  });

  console.log(`\n📊 Summary:`);
  console.log(`   Files scanned: ${allFiles.length}`);
  console.log(`   Files modified: ${filesModified}`);
  console.log(`   Total changes: ${changesApplied}`);

  return filesModified;
}

// Run the fix
try {
  const modified = fixHeadingHierarchy();

  if (modified > 0) {
    console.log('\n✨ Heading hierarchy fixed!');
    console.log('\n📝 Next steps:');
    console.log('   1. Run: npm run lint (in mcms-jhipster-base/)');
    console.log('   2. Verify: Check browser for heading hierarchy');
    console.log('   3. Test: Use browser accessibility tools to validate');
  } else {
    console.log('\n✅ No heading hierarchy issues found!');
  }

  process.exit(0);
} catch (error) {
  console.error('\n❌ Error:', error.message);
  process.exit(1);
}
