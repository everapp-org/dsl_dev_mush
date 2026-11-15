#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

const entitiesDir = 'mcms-jhipster-base/src/main/webapp/app/entities';

// Find all *-update.tsx files
const entities = fs.readdirSync(entitiesDir);

let updatedCount = 0;

entities.forEach(entity => {
  const entityDir = path.join(entitiesDir, entity);
  const updateFile = path.join(entityDir, `${entity}-update.tsx`);

  if (!fs.existsSync(updateFile)) {
    return;
  }

  let content = fs.readFileSync(updateFile, 'utf8');
  let modified = false;

  // Check if file needs update (has the old pattern)
  if (content.includes('<p>Loading...</p>')) {
    // Add LoadingSpinner import if not already present
    if (!content.includes('LoadingSpinner')) {
      // Find the line with other imports from 'reactstrap'
      const reactstrapImportRegex = /(import.*from 'reactstrap';)/;
      if (reactstrapImportRegex.test(content)) {
        content = content.replace(
          reactstrapImportRegex,
          `$1\nimport { LoadingSpinner } from 'app/shared/components/LoadingSpinner';`
        );
        modified = true;
      }
    }

    // Replace the loading paragraph with LoadingSpinner
    const loadingParagraphRegex = /\{loading \? \(\s*<p>Loading\.\.\.<\/p>\s*\) : \(/;
    if (loadingParagraphRegex.test(content)) {
      content = content.replace(
        loadingParagraphRegex,
        '{loading ? (\n            <LoadingSpinner size="large" message="Loading form..." centered minHeight="200px" />\n          ) : ('
      );
      modified = true;
    }

    if (modified) {
      fs.writeFileSync(updateFile, content, 'utf8');
      console.log(`✓ Updated: ${updateFile}`);
      updatedCount++;
    }
  }
});

console.log(`\nTotal files updated: ${updatedCount}`);
