// P7 重写版 - 同步探测（与 v2 同结构）
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

const TARGET_URL = 'http://localhost:8083/';
const OUT = '/Users/yelinshan/work/天安/博世探路者系统/vaas-reproduction/docs/dashboard-baseline/p7-baseline';
const SHOTS = path.join(OUT, 'screenshots');
fs.mkdirSync(SHOTS, { recursive: true });

let step = 0;
function log(msg, obj) {
    step++;
    const ts = new Date().toISOString().slice(11, 23);
    const line = `[${step}|${ts}] ${msg}`;
    console.log(line);
    if (obj !== undefined) {
        const path2 = path.join(OUT, 'v2-log.jsonl');
        fs.appendFileSync(path2, JSON.stringify({ step, ts, msg, obj }) + '\n');
    }
}

async function shot(page, name) {
    const p = path.join(SHOTS, name);
    await page.screenshot({ path: p, fullPage: false });
    log(`📸 ${name}`);
}

(async () => {
    const logFile = path.join(OUT, 'v2-log.jsonl');
    if (fs.existsSync(logFile)) fs.unlinkSync(logFile);

    const browser = await chromium.launch({ headless: true });
    const ctx = await browser.newContext({
        viewport: { width: 1920, height: 1080 },
        ignoreHTTPSErrors: true,
    });
    const page = await ctx.newPage();

    const errors = [];
    const apiResponses = [];
    page.on('pageerror', e => errors.push(`PAGE_ERR: ${e.message}`));
    page.on('console', m => { if (m.type() === 'error') errors.push(`CONSOLE_ERR: ${m.text()}`); });

    page.on('response', async (resp) => {
        const url = resp.url();
        if (url.includes('/spring/v1/') || url.includes('/admin/') || url.includes('amap')) {
            try {
                const ct = resp.headers()['content-type'] || '';
                if (ct.includes('json')) {
                    const body = await resp.json();
                    apiResponses.push({
                        url: url.replace('http://localhost:8083', '').replace('http://localhost:50410', ''),
                        status: resp.status(),
                        size: JSON.stringify(body).length,
                        body: body,
                    });
                }
            } catch (e) {}
        }
    });

    log('=== P7 dev server 访问 ===');
    log(`目标: ${TARGET_URL}`);
    try {
        const resp = await page.goto(TARGET_URL, { waitUntil: 'domcontentloaded', timeout: 30000 });
        log(`HTTP ${resp.status()}`);
    } catch (e) {
        log(`❌ 访问失败: ${e.message}`);
    }
    await page.waitForTimeout(4000);

    log('=== 默认状态截图 ===');
    await shot(page, '00-default.png');

    log('=== CSS 变量提取 ===');
    const designTokens = await page.evaluate(() => {
        const result = { rootVars: {}, body: {}, h1: {}, h3: {}, button: {}, panel: {} };
        const rootStyle = getComputedStyle(document.documentElement);
        for (let i = 0; i < rootStyle.length; i++) {
            const prop = rootStyle[i];
            if (prop.startsWith('--')) {
                const val = rootStyle.getPropertyValue(prop).trim();
                if (val) result.rootVars[prop] = val;
            }
        }
        const collect = (el, target) => {
            if (!el) return;
            const s = getComputedStyle(el);
            target.font = s.font;
            target.fontSize = s.fontSize;
            target.fontFamily = s.fontFamily;
            target.fontWeight = s.fontWeight;
            target.color = s.color;
            target.background = s.backgroundColor;
        };
        collect(document.body, result.body);
        collect(document.querySelector('h1, h2, h3'), result.h1);
        collect(document.querySelector('h3, h4, h5, h6'), result.h3);
        collect(document.querySelector('button, .el-button'), result.button);
        const panels = document.querySelectorAll('[class*="panel"], [class*="card"], [class*="box"]');
        panels.forEach((p, i) => {
            if (i > 5) return;
            const s = getComputedStyle(p);
            result.panel[`panel-${i}`] = {
                class: p.className?.toString().slice(0, 60),
                background: s.backgroundColor,
                border: s.border,
                borderRadius: s.borderRadius,
                padding: s.padding,
            };
        });
        return result;
    });
    fs.writeFileSync(path.join(OUT, 'design-tokens.json'), JSON.stringify(designTokens, null, 2));

    log('=== 全元素深度探测 ===');
    const allElements = await page.evaluate(() => {
        const results = [];
        document.querySelectorAll('button, a, input, select, textarea, [role="button"], [role="tab"], [tabindex]').forEach((el, i) => {
            const r = el.getBoundingClientRect();
            if (r.width === 0 || r.height === 0) return;
            results.push({
                type: 'interactive',
                tag: el.tagName,
                role: el.getAttribute('role') || '',
                class: (el.className?.toString() || '').slice(0, 100),
                text: (el.textContent || '').trim().slice(0, 50),
                title: el.title || '',
                x: Math.round(r.x), y: Math.round(r.y),
                w: Math.round(r.width), h: Math.round(r.height),
            });
        });
        document.querySelectorAll('canvas').forEach((el, i) => {
            const r = el.getBoundingClientRect();
            results.push({ type: 'canvas', index: i, class: (el.className?.toString() || '').slice(0, 80), w: Math.round(r.width), h: Math.round(r.height) });
        });
        document.querySelectorAll('svg').forEach((el, i) => {
            const r = el.getBoundingClientRect();
            if (r.width === 0) return;
            results.push({ type: 'svg', index: i, class: (el.className?.toString() || '').slice(0, 80), viewBox: el.getAttribute('viewBox') || '', w: Math.round(r.width), h: Math.round(r.height) });
        });
        document.querySelectorAll('[class*="amap-"]').forEach((el, i) => {
            if (i > 10) return;
            const r = el.getBoundingClientRect();
            if (r.width === 0) return;
            results.push({ type: 'amap', class: (el.className?.toString() || '').slice(0, 100), w: Math.round(r.width), h: Math.round(r.height) });
        });
        document.querySelectorAll('[class*="echarts"]').forEach((el, i) => {
            const r = el.getBoundingClientRect();
            if (r.width === 0) return;
            results.push({ type: 'echarts', class: (el.className?.toString() || '').slice(0, 100), w: Math.round(r.width), h: Math.round(r.height) });
        });
        document.querySelectorAll('li, [class*="list-item"], [class*="item"]').forEach((el, i) => {
            if (i > 30) return;
            const r = el.getBoundingClientRect();
            if (r.width === 0) return;
            results.push({ type: 'list-item', tag: el.tagName, class: (el.className?.toString() || '').slice(0, 80), text: (el.textContent || '').trim().slice(0, 60) });
        });
        return results;
    });
    fs.writeFileSync(path.join(OUT, 'all-elements.json'), JSON.stringify(allElements, null, 2));

    log('=== 全文本内容 ===');
    const allTexts = await page.evaluate(() => {
        const results = [];
        const walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, null, false);
        let node;
        while (node = walker.nextNode()) {
            const t = node.textContent.trim();
            if (t && t.length > 1 && t.length < 200) {
                const parent = node.parentElement;
                if (parent && !['SCRIPT', 'STYLE'].includes(parent.tagName)) {
                    results.push({ text: t, parentTag: parent.tagName, parentClass: (parent.className?.toString() || '').slice(0, 60) });
                }
            }
        }
        return results;
    });
    fs.writeFileSync(path.join(OUT, 'all-texts.json'), JSON.stringify(allTexts, null, 2));
    log(`文本节点数: ${allTexts.length}`);

    await page.waitForTimeout(5000);
    await shot(page, '01-after-data-loaded.png');

    log('=== 交互探测 ===');
    try {
        const liveData = page.getByText('实时数据', { exact: false }).first();
        if (await liveData.count() > 0) {
            await liveData.click();
            // 等 drawer 完全打开（包含动画 + 内部 ECharts 渲染 + 数据加载）
            await page.waitForTimeout(5000);
            await shot(page, '02-click-实时数据.png');
            log(`  drawer 应已展开，重新扫描元素`);

            // 重新提取元素（drawer 内部）
            const drawerElems = await page.evaluate(() => {
                const drawer = document.querySelector('.el-drawer, [class*="drawer"]');
                if (!drawer) return { found: false };
                const all = drawer.querySelectorAll('*');
                const results = {
                    found: true,
                    drawerClass: drawer.className?.toString().slice(0, 100),
                    drawerSize: { w: drawer.offsetWidth, h: drawer.offsetHeight },
                    totalElements: all.length,
                    texts: [],
                    buttons: [],
                    options: [],
                    tables: [],
                };
                all.forEach(el => {
                    const t = (el.textContent || '').trim();
                    if (t && t.length > 1 && t.length < 100 && el.children.length === 0) {
                        results.texts.push(t);
                    }
                    if (el.tagName === 'BUTTON' || el.classList?.contains('el-button')) {
                        results.buttons.push((el.textContent || '').trim().slice(0, 30));
                    }
                    if (el.classList?.contains('el-option') || el.tagName === 'OPTION') {
                        results.options.push((el.textContent || '').trim());
                    }
                    if (el.classList?.contains('el-table') || el.classList?.contains('el-table__row')) {
                        results.tables.push({ tag: el.tagName, text: (el.textContent || '').trim().slice(0, 80) });
                    }
                });
                return results;
            });
            log('drawer 元素扫描', {
                found: drawerElems.found,
                size: drawerElems.drawerSize,
                totalElements: drawerElems.totalElements,
                textsCount: drawerElems.texts?.length,
                buttonsCount: drawerElems.buttons?.length,
                optionsCount: drawerElems.options?.length,
            });
            fs.writeFileSync(path.join(OUT, 'drawer-elements.json'), JSON.stringify(drawerElems, null, 2));

            // 重新提取全文本（drawer 打开后）
            const drawerTexts = await page.evaluate(() => {
                const results = [];
                const drawer = document.querySelector('.el-drawer, [class*="drawer"]');
                if (!drawer) return results;
                const walker = document.createTreeWalker(drawer, NodeFilter.SHOW_TEXT, null, false);
                let node;
                while (node = walker.nextNode()) {
                    const t = node.textContent.trim();
                    if (t && t.length > 1 && t.length < 200) {
                        const parent = node.parentElement;
                        if (parent && !['SCRIPT', 'STYLE'].includes(parent.tagName)) {
                            results.push({
                                text: t,
                                parentTag: parent.tagName,
                                parentClass: (parent.className?.toString() || '').slice(0, 60),
                            });
                        }
                    }
                }
                return results;
            });
            log(`drawer 文本节点数: ${drawerTexts.length}`);

            // 合并到 all-texts
            const allTextsMerged = [...allTexts, ...drawerTexts];
            fs.writeFileSync(path.join(OUT, 'all-texts.json'), JSON.stringify(allTextsMerged, null, 2));
            log(`合并后总文本节点数: ${allTextsMerged.length}`);

            // 单独截图 drawer
            if (drawerElems.drawerSize && drawerElems.drawerSize.w > 100) {
                const drawerLoc = page.locator('.el-drawer').first();
                await drawerLoc.screenshot({ path: path.join(SHOTS, '02b-drawer.png') });
                log('📸 02b-drawer.png (drawer 单独截图)');
            }

            // 探测气象站下拉
            try {
                const select = page.locator('.el-drawer .el-select').first();
                if (await select.count() > 0) {
                    await select.click();
                    await page.waitForTimeout(1500);
                    await shot(page, '03-sensor-dropdown.png');
                    // 获取所有下拉项
                    const options = await page.evaluate(() => {
                        return Array.from(document.querySelectorAll('.el-select-dropdown__item')).map(e => (e.textContent || '').trim());
                    });
                    log(`下拉项: ${options.join(' | ')}`);
                    fs.writeFileSync(path.join(OUT, 'dropdown-options.json'), JSON.stringify(options, null, 2));
                }
            } catch (e) {
                log(`下拉探测失败: ${e.message.split('\n')[0]}`);
            }
        }
    } catch (e) {
        log(`点击"实时数据"失败: ${e.message.split('\n')[0]}`);
    }

    fs.writeFileSync(path.join(OUT, 'api-responses.json'), JSON.stringify(apiResponses, null, 2));
    log(`API 响应数: ${apiResponses.length}`);

    const apiFields = apiResponses.map(r => {
        const body = r.body;
        let fields = null;
        if (body && typeof body === 'object') {
            if (Array.isArray(body.data)) {
                fields = { type: 'array', sampleSize: body.data.length, itemFields: body.data[0] ? Object.keys(body.data[0]) : [] };
            } else if (body.data && typeof body.data === 'object') {
                fields = { type: 'object', keys: Object.keys(body.data) };
            } else {
                fields = { type: typeof body, keys: Object.keys(body || {}) };
            }
        }
        return { url: r.url, status: r.status, size: r.size, fields };
    });
    fs.writeFileSync(path.join(OUT, 'api-fields.json'), JSON.stringify(apiFields, null, 2));

    fs.writeFileSync(path.join(OUT, 'errors.json'), JSON.stringify(errors, null, 2));
    log(`错误数: ${errors.length}`);

    await browser.close();
    log('=== P7 探测完成 ===');
})();
