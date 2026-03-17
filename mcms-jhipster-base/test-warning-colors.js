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

const candidates = ['#b8860b', '#996600', '#8b7500', '#856404', '#7a5900', '#6c5400'];
console.log('Testing darker warning colors on white background:');
console.log('Need: 4.5:1 minimum for WCAG AA');
console.log('');
candidates.forEach(color => {
  const ratio = getContrastRatio(color, '#ffffff');
  const pass = ratio >= 4.5 ? '✓ PASS' : '✗ FAIL';
  console.log(`  ${pass} ${color}: ${ratio.toFixed(2)}:1`);
});
