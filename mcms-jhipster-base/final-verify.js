function hexToRgb(hex) {
  hex = hex.replace('#', '');
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

console.log('='.repeat(80));
console.log('FINAL WCAG AA COLOR CONTRAST VERIFICATION - Feature #270');
console.log('='.repeat(80));
console.log('');

const finalFixes = [
  { name: 'Error text', fg: '#ffffff', bg: '#d00000' },
  { name: 'Breadcrumb muted', fg: '#5a6268', bg: '#f8f9fa' },
  { name: 'Mobile hint text', fg: '#5a6268', bg: '#f8f9fa' },
  { name: 'Warning text', fg: '#856404', bg: '#ffffff' },
  { name: 'Warning badge', fg: '#ffffff', bg: '#856404' },
  { name: 'Muted text', fg: '#5a6268', bg: '#ffffff' },
  { name: 'Invalid feedback', fg: '#c00000', bg: '#ffffff' }
];

let allPass = true;

finalFixes.forEach(fix => {
  const ratio = getContrastRatio(fix.fg, fix.bg);
  const pass = ratio >= 4.5;
  const status = pass ? '✓ PASS' : '✗ FAIL';

  console.log(`${status} ${fix.name}`);
  console.log(`    Colors: ${fix.fg} on ${fix.bg}`);
  console.log(`    Contrast Ratio: ${ratio.toFixed(2)}:1`);
  console.log('');

  if (!pass) allPass = false;
});

console.log('='.repeat(80));
if (allPass) {
  console.log('✓✓✓ ALL COLOR COMBINATIONS MEET WCAG AA STANDARDS ✓✓✓');
  console.log('Feature #270 implementation complete!');
} else {
  console.log('✗ Some combinations still need fixing');
}
console.log('='.repeat(80));
