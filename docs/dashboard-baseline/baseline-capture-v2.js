// VaaS 原大屏 - 深度探测脚本 v2
// 目标: 全功能/全交互/全视觉规范探测
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

const TARGET_URL = 'https://vaas.wx-iov.com:444/#/dashboard';
const OUT = '/Users/yelinshan/work/天安/博世探路者系统/vaas-reproduction/docs/dashboard-baseline';
const SHOTS = path.join(OUT, 'screenshots-v2');
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

async function shotEl(page, selector, name) {
    try {
        const loc = page.locator(selector).first();
        if (await loc.count() > 0) {
            const p = path.join(SHOTS, name);
            await loc.screenshot({ path: p });
            log(`📸 ${name} (${selector})`);
        } else {
            log(`⚠️ 元素不存在: ${selector}`);
        }
    } catch (e) {
        log(`❌ 元素截图失败: ${selector} - ${e.message.split('\n')[0]}`);
    }
}

(async () => {
    // 清空旧 log
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

    // 拦截 API 响应，提取 JSON 字段
    page.on('response', async (resp) => {
        const url = resp.url();
        if (url.includes('/spring/v1/') || url.includes('/admin/')) {
            try {
                const ct = resp.headers()['content-type'] || '';
                if (ct.includes('json')) {
                    const body = await resp.json();
                    apiResponses.push({
                        url: url.replace('https://vaas.wx-iov.com:444', ''),
                        status: resp.status(),
                        size: JSON.stringify(body).length,
                        body: body,
                    });
                }
            } catch (e) {}
        }
    });

    log('=== 启动访问 ===');
    log(`目标: ${TARGET_URL}`);

    try {
        const resp = await page.goto(TARGET_URL, { waitUntil: 'domcontentloaded', timeout: 30000 });
        log(`HTTP ${resp.status()}`);
    } catch (e) {
        log(`❌ 访问失败: ${e.message}`);
    }

    await page.waitForTimeout(4000);  // 等待地图/SSE 加载
    log('=== 默认状态截图 ===');
    await shot(page, '00-default.png');

    // ========================================
    // 1. CSS 变量 + 主题色提取
    // ========================================
    log('=== CSS 变量提取 ===');
    const designTokens = await page.evaluate(() => {
        const result = { rootVars: {}, body: {}, h1: {}, h3: {}, button: {}, panel: {} };

        // 1.1 :root CSS 变量
        const rootStyle = getComputedStyle(document.documentElement);
        for (let i = 0; i < rootStyle.length; i++) {
            const prop = rootStyle[i];
            if (prop.startsWith('--')) {
                const val = rootStyle.getPropertyValue(prop).trim();
                if (val) result.rootVars[prop] = val;
            }
        }

        // 1.2 关键元素 computed style
        const collect = (el, target) => {
            if (!el) return;
            const s = getComputedStyle(el);
            target.font = s.font;
            target.fontSize = s.fontSize;
            target.fontFamily = s.fontFamily;
            target.fontWeight = s.fontWeight;
            target.color = s.color;
            target.background = s.backgroundColor;
            target.lineHeight = s.lineHeight;
            target.letterSpacing = s.letterSpacing;
        };

        collect(document.body, result.body);
        collect(document.querySelector('h1, h2, h3'), result.h1);
        collect(document.querySelector('h3, h4, h5, h6'), result.h3);
        collect(document.querySelector('button, .el-button'), result.button);

        // 1.3 主要面板背景色
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
    log('design-tokens.json 写入', { rootVarsCount: Object.keys(designTokens.rootVars).length, panels: Object.keys(designTokens.panel).length });

    // ========================================
    // 2. 全元素深度探测
    // ========================================
    log('=== 全元素深度探测 ===');
    const allElements = await page.evaluate(() => {
        const results = [];
        // 2.1 所有可见 button / a / input / select
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
        // 2.2 所有 canvas
        document.querySelectorAll('canvas').forEach((el, i) => {
            const r = el.getBoundingClientRect();
            results.push({
                type: 'canvas',
                index: i,
                class: (el.className?.toString() || '').slice(0, 80),
                w: Math.round(r.width), h: Math.round(r.height),
                x: Math.round(r.x), y: Math.round(r.y),
            });
        });
        // 2.3 所有 svg
        document.querySelectorAll('svg').forEach((el, i) => {
            const r = el.getBoundingClientRect();
            if (r.width === 0) return;
            results.push({
                type: 'svg',
                index: i,
                class: (el.className?.toString() || '').slice(0, 80),
                viewBox: el.getAttribute('viewBox') || '',
                w: Math.round(r.width), h: Math.round(r.height),
            });
        });
        // 2.4 高德地图元素
        document.querySelectorAll('[class*="amap-"]').forEach((el, i) => {
            if (i > 10) return;
            const r = el.getBoundingClientRect();
            if (r.width === 0) return;
            results.push({
                type: 'amap',
                class: (el.className?.toString() || '').slice(0, 100),
                w: Math.round(r.width), h: Math.round(r.height),
            });
        });
        // 2.5 ECharts
        document.querySelectorAll('[class*="echarts"], [class*="_chart_"]').forEach((el, i) => {
            const r = el.getBoundingClientRect();
            if (r.width === 0) return;
            results.push({
                type: 'echarts',
                class: (el.className?.toString() || '').slice(0, 100),
                w: Math.round(r.width), h: Math.round(r.height),
            });
        });
        // 2.6 列表项
        document.querySelectorAll('li, [class*="list-item"], [class*="item"]').forEach((el, i) => {
            if (i > 30) return;
            const r = el.getBoundingClientRect();
            if (r.width === 0) return;
            results.push({
                type: 'list-item',
                tag: el.tagName,
                class: (el.className?.toString() || '').slice(0, 80),
                text: (el.textContent || '').trim().slice(0, 60),
            });
        });
        return results;
    });
    fs.writeFileSync(path.join(OUT, 'all-elements.json'), JSON.stringify(allElements, null, 2));
    const elementStats = allElements.reduce((acc, e) => {
        acc[e.type] = (acc[e.type] || 0) + 1;
        return acc;
    }, {});
    log('all-elements.json 写入', elementStats);

    // ========================================
    // 3. 全文本内容
    // ========================================
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
    fs.writeFileSync(path.join(OUT, 'all-texts.json'), JSON.stringify(allTexts, null, 2));
    log(`文本节点数: ${allTexts.length}`);

    // ========================================
    // 4. 等待 5 秒后再截图（数据加载后）
    // ========================================
    log('=== 等待数据加载 ===');
    await page.waitForTimeout(5000);
    await shot(page, '01-after-data-loaded.png');

    // ========================================
    // 5. 交互探测
    // ========================================
    log('=== 交互探测 ===');

    // 5.1 点击"实时数据"展开内容面板
    try {
        const liveData = page.getByText('实时数据', { exact: false }).first();
        if (await liveData.count() > 0) {
            await liveData.click();
            await page.waitForTimeout(2000);
            await shot(page, '02-click-实时数据.png');

            // 5.2 截图内容面板（如果打开）
            await shotEl(page, '.content-panel, [class*="content"], [class*="panel-content"]', '03-content-panel.png');

            // 5.3 探测气象站下拉
            const sensorSelects = await page.locator('select, [class*="select"], [class*="dropdown"]').all();
            for (let i = 0; i < sensorSelects.length && i < 5; i++) {
                try {
                    await sensorSelects[i].click();
                    await page.waitForTimeout(1000);
                    await shot(page, `04-sensor-dropdown-${i}.png`);
                    // 关闭
                    await page.keyboard.press('Escape');
                } catch (e) {}
            }

            // 5.4 探测关闭按钮
            try {
                const closeBtn = page.locator('[class*="close"], .el-dialog__close').first();
                if (await closeBtn.count() > 0 && await closeBtn.isVisible()) {
                    await closeBtn.click();
                    await page.waitForTimeout(1000);
                    await shot(page, '05-after-close-panel.png');
                }
            } catch (e) {}
        }
    } catch (e) {
        log(`点击"实时数据"失败: ${e.message.split('\n')[0]}`);
    }

    // 5.5 hover 几个关键位置
    const hoverTargets = [
        { sel: 'text=实时车队数据', name: '06-hover-实时车队' },
        { sel: 'text=路网状态', name: '07-hover-路网状态' },
        { sel: 'text=实时气象数据', name: '08-hover-实时气象' },
        { sel: '[class*="amap-marker"]', name: '09-hover-amap-marker' },
    ];
    for (const t of hoverTargets) {
        try {
            const loc = page.locator(t.sel).first();
            if (await loc.count() > 0) {
                await loc.hover();
                await page.waitForTimeout(800);
                await shot(page, t.name + '.png');
            }
        } catch (e) {
            log(`hover ${t.sel} 失败: ${e.message.split('\n')[0]}`);
        }
    }

    // 5.6 点击车辆标记（地图上的车）
    try {
        const carMarkers = await page.locator('[class*="car"], [class*="vehicle"], [class*="marker"]').all();
        if (carMarkers.length > 0) {
            log(`找到 ${carMarkers.length} 个车辆/标记元素`);
            await carMarkers[0].click({ force: true });
            await page.waitForTimeout(1500);
            await shot(page, '10-click-marker.png');
        }
    } catch (e) {
        log(`点击车辆标记失败: ${e.message.split('\n')[0]}`);
    }

    // 5.7 探测时间轴
    try {
        const timeline = page.locator('[class*="timeline"], [class*="time-axis"], [class*="slider"]').first();
        if (await timeline.count() > 0) {
            await shotEl(page, '[class*="timeline"], [class*="time-axis"], [class*="slider"]', '11-timeline.png');
            // 拖动滑块
            const handle = page.locator('[class*="handle"], [class*="thumb"], [class*="slider-handle"]').first();
            if (await handle.count() > 0) {
                const box = await handle.boundingBox();
                if (box) {
                    await page.mouse.move(box.x + box.width / 2, box.y + box.height / 2);
                    await page.mouse.down();
                    await page.mouse.move(box.x + 200, box.y + box.height / 2);
                    await page.mouse.up();
                    await page.waitForTimeout(1000);
                    await shot(page, '12-timeline-dragged.png');
                }
            }
        }
    } catch (e) {
        log(`时间轴交互失败: ${e.message.split('\n')[0]}`);
    }

    // 5.8 切换"干湿/附着/温度"
    try {
        const radios = await page.locator('input[type="radio"], [class*="radio"], [class*="switch"]').all();
        for (let i = 0; i < Math.min(radios.length, 3); i++) {
            try {
                await radios[i].click({ force: true });
                await page.waitForTimeout(800);
                await shot(page, `13-radio-${i}.png`);
            } catch (e) {}
        }
    } catch (e) {
        log(`radio 切换失败: ${e.message.split('\n')[0]}`);
    }

    // 5.9 探测告警列表的"导出"按钮
    try {
        const exportBtn = page.getByText('导出', { exact: false }).first();
        if (await exportBtn.count() > 0) {
            await exportBtn.click();
            await page.waitForTimeout(1500);
            await shot(page, '14-export-clicked.png');
        }
    } catch (e) {}

    // ========================================
    // 6. API 响应字段
    // ========================================
    log('=== API 响应汇总 ===');
    fs.writeFileSync(path.join(OUT, 'api-responses.json'), JSON.stringify(apiResponses, null, 2));
    log(`捕获 ${apiResponses.length} 个 API 响应`);

    // 简化版（只保留字段结构，不保留值）
    const apiFields = apiResponses.map(r => {
        const body = r.body;
        let fields = null;
        if (body && typeof body === 'object') {
            if (Array.isArray(body.data)) {
                fields = {
                    type: 'array',
                    sampleSize: body.data.length,
                    itemFields: body.data[0] ? Object.keys(body.data[0]) : [],
                };
            } else if (body.data && typeof body.data === 'object') {
                fields = {
                    type: 'object',
                    keys: Object.keys(body.data),
                };
            } else {
                fields = { type: typeof body, keys: Object.keys(body || {}) };
            }
        }
        return { url: r.url, status: r.status, size: r.size, fields };
    });
    fs.writeFileSync(path.join(OUT, 'api-fields.json'), JSON.stringify(apiFields, null, 2));
    log('api-fields.json 写入');

    // ========================================
    // 7. 错误汇总
    // ========================================
    fs.writeFileSync(path.join(OUT, 'errors.json'), JSON.stringify(errors, null, 2));
    log(`错误数: ${errors.length}`);

    await browser.close();
    log('=== 探测完成 ===');

    // ========================================
    // 8. 输出 interaction-state-machine.md
    // ========================================
    const stateMachine = [
        '# 原大屏交互状态机',
        '',
        '> 基于 v2 深度探测自动生成',
        '',
        '## 默认状态',
        '- 顶栏：标题"恶劣天气道路路面状态感知与预测系统" + 返回按钮 + S 图标',
        '- 左侧：折叠态浮层（实时车队 / 路网状态 / 实时气象）',
        '- 中心：高德地图（无锡，zoom 12）',
        '- 底部：时间轴（过去 23h ─── 未来 1h）',
        '',
        '## 状态机',
        '',
        '```',
        '[默认]',
        '  │ 鼠标悬停"实时车队数据"',
        '  ↓',
        '[车队数据展开]',
        '  │ 点击"实时数据"按钮',
        '  ↓',
        '[内容面板弹出（覆盖地图下半部分）]',
        '  │   ├─ 选择气象站（下拉）',
        '  │   ├─ 切换 radio（干湿/附着/温度）',
        '  │   ├─ 拖动时间轴',
        '  │   └─ 点击"导出"',
        '  │ 点击关闭按钮',
        '  ↓',
        '[回到内容面板弹出]',
        '  │ 点击关闭',
        '  ↓',
        '[默认]',
        '',
        '[默认]',
        '  │ 点击地图车辆标记',
        '  ↓',
        '[车辆信息弹窗]',
        '  │   ├─ 查看车牌/速度',
        '  │   └─ 关闭',
        '  ↓',
        '[默认]',
        '',
        '[默认]',
        '  │ 点击地图气象站标记',
        '  ↓',
        '[气象站信息弹窗]',
        '```',
        '',
    ].join('\n');
    fs.writeFileSync(path.join(OUT, 'interaction-state-machine.md'), stateMachine);

    log('✅ v2 探测完成，产物在 screenshots-v2/ + docs/');
})();
