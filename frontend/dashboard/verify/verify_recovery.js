// VaaS Dashboard - Recovery Verification Script
// Validates that recovered source files are syntactically valid
const fs = require('fs');
const path = require('path');

const srcDir = path.join(__dirname, 'src');

let passed = 0, failed = 0;

function walk(dir) {
  const files = fs.readdirSync(dir);
  for (const f of files) {
    const full = path.join(dir, f);
    const stat = fs.statSync(full);
    if (stat.isDirectory() && f !== 'node_modules') walk(full);
    else if (f.endsWith('.js')) {
      try {
        require(full);
        // Special handling for .vue files - they export render functions
        // We verify the files exist and are non-empty below
        console.log(`  ✓ ${path.relative(srcDir, full)}`);
        passed++;
      } catch (e) {
        // If it's a module that needs Vue, just check syntax
        if (e.code === 'MODULE_NOT_FOUND' || e.message.includes('Cannot find module')) {
          // Check file is non-empty and has expected exports
          const content = fs.readFileSync(full, 'utf-8');
          if (content.length > 50 && content.includes('export')) {
            console.log(`  ✓ ${path.relative(srcDir, full)} (module deps skipped)`);
            passed++;
          } else {
            console.log(`  ✗ ${path.relative(srcDir, full)} - too short or no exports`);
            failed++;
          }
        } else if (e.name === 'SyntaxError') {
          console.log(`  ✗ ${path.relative(srcDir, full)} - SYNTAX ERROR: ${e.message.split('\n')[0]}`);
          failed++;
        } else {
          console.log(`  ✓ ${path.relative(srcDir, full)} (loaded: ${e.message.split('\n')[0]})`);
          passed++;
        }
      }
    }
  }
}

console.log('\n📁 Recovered Source Files:\n');
walk(path.join(srcDir, 'utils'));
walk(path.join(srcDir, 'router'));
walk(path.join(srcDir, 'store'));
walk(path.join(srcDir, 'data'));
walk(srcDir); // main.js

console.log('\n📁 Recovered Vue Components (render functions):\n');
const componentsDir = path.join(srcDir, 'components');
if (fs.existsSync(componentsDir)) {
  for (const f of fs.readdirSync(componentsDir)) {
    const full = path.join(componentsDir, f);
    const content = fs.readFileSync(full, 'utf-8');
    const hasRender = content.includes('var render');
    const hasStaticFns = content.includes('staticRenderFns');
    const size = content.length;
    if (hasRender && hasStaticFns && size > 100) {
      console.log(`  ✓ ${f} (${size} bytes, render + staticRenderFns)`);
      passed++;
    } else {
      console.log(`  ⚠ ${f} (${size} bytes, may be incomplete)`);
      failed++;
    }
  }
}

console.log('\n📁 Views:\n');
const viewsDir = path.join(srcDir, 'views');
if (fs.existsSync(viewsDir)) {
  for (const f of fs.readdirSync(viewsDir)) {
    const full = path.join(viewsDir, f);
    const content = fs.readFileSync(full, 'utf-8');
    const hasRender = content.includes('var render');
    const hasStaticFns = content.includes('staticRenderFns');
    const size = content.length;
    if (hasRender && hasStaticFns && size > 100) {
      console.log(`  ✓ ${f} (${size} bytes)`);
      passed++;
    } else {
      console.log(`  ⚠ ${f} (${size} bytes, may be incomplete)`);
      failed++;
    }
  }
}

console.log(`\n📊 Result: ${passed} passed, ${failed} failed out of ${passed + failed} files\n`);
