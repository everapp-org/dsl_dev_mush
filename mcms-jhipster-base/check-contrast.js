// WCAG AA Color Contrast Checker
// Calculates contrast ratios between color pairs

function hexToRgb(hex) {
  hex = hex.replace('#', '');
  if (hex.length === 3) {
    hex = hex.split('').map(c => c + c).join('');
  }
  return {
    r: parseInt(hex.substr(0, 2), 16),
    g: parseInt(hex.substr(2, 2), 16),
    b: parseInt(hex.substr(4, 2), 16)
  };
}

function getLuminance(rgb) {
  const { r, g, b } = rgb;
  const [rs, gs, bs] = [r, g, b].map(c => {
    c = c / 255;
    return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
  });
  return 0.2126 * rs + 0.7152 * gs + 0.0722 * bs;
}

function getContrastRatio(color1, color2) {
  const lum1 = getLuminance(hexToRgb(color1));
  const lum2 = getLuminance(hexToRgb(color2));
  const lighter = Math.max(lum1, lum2);
  const darker = Math.min(lum1, lum2);
  return (lighter + 0.05) / (darker + 0.05);
}

function checkWCAG(ratio, level = 'AA') {
  const normalTextAA = 4.5;
  const largeTextAA = 3.0;

  return {
    normalText: ratio >= normalTextAA,
    largeText: ratio >= largeTextAA,
    ratio: ratio.toFixed(2)
  };
}

// Test color pairs from the codebase
const colorPairs = [
  { name: 'Links (app.scss line 11)', fg: '#533f03', bg: '#fafafa' },
  { name: 'Error text (app.scss line 123)', fg: '#ffffff', bg: '#ff0000' },
  { name: 'Browser upgrade (app.scss line 71)', fg: '#000000', bg: '#cccccc' },
  { name: 'Header secondary (header.scss line 70)', fg: '#cccccc', bg: '#353d47' },
  { name: 'Sidebar text (sidebar.scss line 7)', fg: '#ecf0f1', bg: '#2c3e50' },
  { name: 'Sidebar secondary (sidebar.scss line 91)', fg: '#bdc3c7', bg: '#2c3e50' },
  { name: 'Breadcrumb muted (breadcrumb.scss line 27)', fg: '#6c757d', bg: '#f8f9fa' },
  { name: 'Dashboard muted text', fg: '#6c757d', bg: '#ffffff' },
  { name: 'Dashboard warning (yellow)', fg: '#ffc107', bg: '#ffffff' },
  { name: 'Mobile hint text', fg: '#6c757d', bg: '#f8f9fa' }
];

console.log('WCAG AA Color Contrast Analysis');
console.log('='.repeat(80));
console.log('WCAG AA Requirements:');
console.log('  - Normal text (< 18pt): 4.5:1');
console.log('  - Large text (>= 18pt): 3.0:1');
console.log('='.repeat(80));
console.log('');

const failures = [];

colorPairs.forEach(pair => {
  const ratio = getContrastRatio(pair.fg, pair.bg);
  const wcag = checkWCAG(ratio);

  const status = wcag.normalText ? '✓ PASS' : '✗ FAIL';
  console.log(`${status} | ${pair.name}`);
  console.log(`       Foreground: ${pair.fg} | Background: ${pair.bg}`);
  console.log(`       Contrast Ratio: ${wcag.ratio}:1`);
  console.log(`       Normal Text: ${wcag.normalText ? 'PASS' : 'FAIL'} | Large Text: ${wcag.largeText ? 'PASS' : 'FAIL'}`);
  console.log('');

  if (!wcag.normalText) {
    failures.push({ ...pair, ratio: wcag.ratio });
  }
});

console.log('='.repeat(80));
console.log(`Total Pairs Tested: ${colorPairs.length}`);
console.log(`Failures: ${failures.length}`);
console.log('='.repeat(80));

if (failures.length > 0) {
  console.log('');
  console.log('FAILED COMBINATIONS (Need Fixing):');
  failures.forEach(f => {
    console.log(`  - ${f.name}: ${f.fg} on ${f.bg} (${f.ratio}:1)`);
  });
}
