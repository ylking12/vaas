// 用 Playwright 访问原大屏，固化基线
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

const TARGET_URL = 'https://vaas.wx-iov.com:444/#/dashboard';
const OUT_DIR = '/Users/yelinshan/work/天安/博世探路者系统/vaas-reproduction/docs/dashboard-baseline';
const SHOTS_DIR = path.join(OUT_DIR, 'screenshots');

const REPORT = [];
let stepIdx = 0;

function log(msg) {
    console.log(`[${++stepIdx}] ${msg}`);
    REPORT.push(`[${stepIdx}] ${msg}`);
}

async function shot(page, name) {
    const p = path.join(SHOTS_DIR, name);
    await page.screenshot({ path: p, fullPage: false });
    log(`📸 ${name}`);
    return p;
}

(async () => {
    const browser = await chromium.launch({ headless: true });
    const context = await browser.newContext({
        viewport: { width: 1920, height: 1080 },
        ignoreHTTPSErrors: true,  // 忽略 HTTPS 证书错误
    });
    const page = await context.newPage();

    // 收集 console 和网络错误
    const errors = [];
    page.on('pageerror', err => errors.push(`PAGE_ERROR: ${err.message}`));
    page.on('console', msg => {
        if (msg.type() === 'error') errors.push(`CONSOLE_ERROR: ${msg.text()}`);
    });
    page.on('requestfailed', req => errors.push(`REQ_FAILED: ${req.url()} - ${req.failure()?.errorText}`));

    try {
        log(`访问 ${TARGET_URL}`);
        const resp = await page.goto(TARGET_URL, {
            waitUntil: 'domcontentloaded',
            timeout: 30000,
        }).catch(e => { return { err: e.message }; });
        if (resp?.err) {
            log(`❌ 访问失败: ${resp.err}`);
        } else {
            log(`HTTP 状态: ${resp.status()}`);
        }
    } catch (e) {
        log(`❌ 访问异常: ${e.message}`);
    }

    // 等待 3 秒（地图、SSE 等资源加载）
    await page.waitForTimeout(3000);

    // 1. 默认状态截图
    await shot(page, '01-default-state.png');
    await page.screenshot({ path: path.join(SHOTS_DIR, '01-default-fullpage.png'), fullPage: true });
    log('📸 01-default-fullpage.png (fullPage)');

    // 2. 探测所有可交互元素
    const interactives = await page.evaluate(() => {
        const results = [];
        const els = document.querySelectorAll('button, a, input, [role="button"], [role="tab"], .el-tab, .el-tabs__item, .el-button, [class*="tab"], [class*="btn"], [class*="switch"]');
        els.forEach((el, idx) => {
            const rect = el.getBoundingClientRect();
            if (rect.width > 0 && rect.height > 0) {
                results.push({
                    idx,
                    tag: el.tagName,
                    class: el.className?.toString().slice(0, 80) || '',
                    text: (el.textContent || '').trim().slice(0, 50),
                    title: el.title || '',
                    placeholder: el.placeholder || '',
                    type: el.type || '',
                    href: el.href || '',
                    x: Math.round(rect.x),
                    y: Math.round(rect.y),
                    w: Math.round(rect.width),
                    h: Math.round(rect.height),
                    visible: rect.width > 0 && rect.height > 0,
                });
            }
        });
        return results;
    });
    log(`发现 ${interactives.length} 个可见交互元素`);
    fs.writeFileSync(
        path.join(OUT_DIR, 'interactives.json'),
        JSON.stringify(interactives, null, 2)
    );

    // 3. 探测页面所有文本内容（标题、面板名等）
    const texts = await page.evaluate(() => {
        const results = [];
        const headings = document.querySelectorAll('h1, h2, h3, h4, h5, h6, .title, .panel-title, [class*="title"]');
        headings.forEach(el => {
            const t = (el.textContent || '').trim();
            if (t) results.push({ tag: el.tagName, class: el.className?.toString().slice(0, 50), text: t });
        });
        return results;
    });
    log(`发现 ${texts.length} 个标题/面板名`);
    fs.writeFileSync(
        path.join(OUT_DIR, 'headings.json'),
        JSON.stringify(texts, null, 2)
    );

    // 4. 尝试点击左侧"实时数据"按钮（如果存在）
    try {
        const liveDataBtn = page.locator('text=实时数据').first();
        if (await liveDataBtn.count() > 0) {
            await liveDataBtn.click({ timeout: 3000 });
            await page.waitForTimeout(1500);
            await shot(page, '02-after-click-实时数据.png');
            log('点击"实时数据"后截图');
        } else {
            log('未找到"实时数据"按钮');
        }
    } catch (e) {
        log(`点击"实时数据"异常: ${e.message.split('\n')[0]}`);
    }

    // 5. 关闭可能打开的弹窗
    try {
        const closeBtn = page.locator('.el-dialog__close, .close-btn, [class*="close"]').first();
        if (await closeBtn.count() > 0 && await closeBtn.isVisible()) {
            await closeBtn.click({ timeout: 2000 });
            await page.waitForTimeout(800);
        }
    } catch (e) {}

    // 6. 依次点击可见的主要按钮/tab
    const buttonLocators = await page.locator('button:visible, [role="button"]:visible, .el-tabs__item:visible').all();
    let btnCount = 0;
    for (const btn of buttonLocators.slice(0, 15)) {  // 限制前 15 个
        try {
            const text = (await btn.textContent() || '').trim().slice(0, 30);
            if (!text || text.length < 2) continue;
            btnCount++;
            const isDisabled = await btn.isDisabled();
            if (isDisabled) {
                log(`  跳过禁用按钮: ${text}`);
                continue;
            }
            await btn.click({ timeout: 2000 }).catch(() => {});
            await page.waitForTimeout(800);
            await shot(page, `03-button-${btnCount}-${text.replace(/[\/\\:*?"<>|]/g, '_')}.png`);
        } catch (e) {
            log(`  点击按钮失败: ${e.message.split('\n')[0]}`);
        }
    }
    log(`共点击 ${btnCount} 个按钮`);

    // 7. 探测地图区域（如果存在）
    try {
        const mapEl = page.locator('.amap-container, [class*="map"], #map, canvas').first();
        if (await mapEl.count() > 0) {
            await mapEl.screenshot({ path: path.join(SHOTS_DIR, '04-map-only.png') });
            log('📸 04-map-only.png (地图单独截图)');
        }
    } catch (e) {
        log(`地图截图失败: ${e.message.split('\n')[0]}`);
    }

    // 8. 错误汇总
    if (errors.length > 0) {
        log(`⚠️  共 ${errors.length} 个错误：`);
        errors.slice(0, 30).forEach(e => log(`  - ${e}`));
    } else {
        log('✅ 无 JS 错误');
    }

    // 9. 探测网络请求
    const networkLog = await page.evaluate(() => {
        return performance.getEntriesByType('resource')
            .filter(r => r.name.includes('/spring/v1/') || r.name.includes('/admin/'))
            .map(r => ({ name: r.name, status: r.responseStatus, size: r.transferSize }));
    });
    fs.writeFileSync(
        path.join(OUT_DIR, 'api-calls.json'),
        JSON.stringify(networkLog, null, 2)
    );
    log(`记录到 ${networkLog.length} 个后端 API 调用`);

    await browser.close();

    // 写 interaction-report.md
    const report = [
        '# 原大屏交互探测报告',
        '',
        '> 探测时间: ' + new Date().toISOString(),
        '> 目标 URL: ' + TARGET_URL,
        '',
        '## 1. 访问结果',
        '',
        REPORT.join('\n'),
        '',
        '## 2. 错误汇总',
        '',
        errors.length === 0 ? '✅ 无错误' : errors.map(e => '- ' + e).join('\n'),
        '',
        '## 3. 后端 API 调用',
        '',
        networkLog.length === 0 ? '未捕获到 /spring/v1/ 或 /admin/ 的 API 调用' : '```json\n' + JSON.stringify(networkLog, null, 2) + '\n```',
        '',
        '## 4. 产物清单',
        '',
        '- screenshots/ — 所有截图',
        '- interactives.json — 交互元素清单',
        '- headings.json — 标题/面板名清单',
        '- api-calls.json — 后端 API 调用清单',
        '',
    ].join('\n');

    fs.writeFileSync(path.join(OUT_DIR, 'interaction-report.md'), report);
    log('✅ 报告已写入: ' + path.join(OUT_DIR, 'interaction-report.md'));
})();
