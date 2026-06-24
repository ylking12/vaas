#!/usr/bin/env node
/**
 * 从原版前端 Webpack bundle 提取 base64 inline 图标资源 + file-loader 引用资源
 *
 * SOURCE: /Users/yelinshan/work/天安/博世探路者系统/前端代码/www/
 *   - js/*.js — 模块定义（base64 inline 或 file-loader 路径）
 *   - img/   — file-loader 的真实 png/gif 文件
 *
 * TARGET: vaas-reproduction/frontend/dashboard/src/assets/img/{,event,roadside,layer}/
 *
 * Webpack 5 模块两种格式:
 *   1. base64 inline:
 *      eval("module.exports = \"data:image/png;base64,XXX\";\n//# sourceURL=...")
 *   2. file-loader 引用:
 *      eval("module.exports = __webpack_require__.p + \"img/xxx.hash.png\";\n//# sourceURL=...")
 *
 * 策略：先按 module key 切段，再每段内独立匹配，避免 regex 跨段错位
 *
 * 用法: node scripts/extract-original-icons.js
 */

const fs = require('fs')
const path = require('path')

const ORIGINAL_WWW = '/Users/yelinshan/work/天安/博世探路者系统/前端代码/www'
const TARGET_ROOT = path.join(__dirname, '..', 'frontend/dashboard/src/assets/img')

// 扫描 www/js 下所有包含 "./src/assets/img/..." 模块的 .js
function findIconChunks() {
  const jsDir = path.join(ORIGINAL_WWW, 'js')
  const files = fs.readdirSync(jsDir).filter(f => f.endsWith('.js') && !f.endsWith('.gz'))
  const matched = []
  for (const f of files) {
    const txt = fs.readFileSync(path.join(jsDir, f), 'utf8')
    if (/"\.\/src\/assets\/img\/[^"]+":/.test(txt)) {
      matched.push(path.join(jsDir, f))
    }
  }
  return matched
}

// 按 module key 切段：返回 [{ modulePath, body }]
//   切段方式：相邻两个 key 之间的字符就是上一段的 body
function splitModules(txt) {
  const keyRe = /"(\.\/src\/assets\/img\/[^"]+)":/g
  const matches = [...txt.matchAll(keyRe)]
  const result = []
  for (let i = 0; i < matches.length; i++) {
    const m = matches[i]
    const start = m.index
    const end = i + 1 < matches.length ? matches[i + 1].index : start + 50000
    result.push({
      modulePath: m[1],
      body: txt.slice(start, end)
    })
  }
  return result
}

// 从段内提取 eval 字符串内容
//   "use strict"; eval("module.exports = ...")
// 返回 eval 字符串解码后的 JS 代码（已去掉两端引号 + 反转义）
function extractEvalContent(body) {
  // eval("..."); 但内容可能含 \" 等转义
  // 简单做法：从 'eval("' 开始扫到匹配的 '"'，处理转义
  const startMark = 'eval("'
  const i = body.indexOf(startMark)
  if (i < 0) return null
  let j = i + startMark.length
  let out = ''
  while (j < body.length) {
    const c = body[j]
    if (c === '\\') {
      const next = body[j + 1]
      // 反转义
      if (next === 'n') out += '\n'
      else if (next === 't') out += '\t'
      else if (next === 'r') out += '\r'
      else if (next === '"') out += '"'
      else if (next === '\\') out += '\\'
      else if (next === "'") out += "'"
      else out += next
      j += 2
    } else if (c === '"') {
      // eval 字符串结束
      return out
    } else {
      out += c
      j++
    }
  }
  return null
}

// 解析 eval 内容，得到资源数据
//   1. base64: 'module.exports = "data:image/png;base64,XXX";...'
//   2. file: 'module.exports = __webpack_require__.p + "img/xxx.hash.png";...'
function parseModuleExport(evalContent, modulePath) {
  if (!evalContent) return null
  // base64 inline
  const m1 = evalContent.match(/module\.exports\s*=\s*"(data:image\/[a-z+]+);base64,([A-Za-z0-9+/=]+)"/)
  if (m1) {
    return { kind: 'base64', mime: m1[1], b64: m1[2] }
  }
  // file-loader
  const m2 = evalContent.match(/module\.exports\s*=\s*__webpack_require__\.p\s*\+\s*"([^"]+)"/)
  if (m2) {
    return { kind: 'file', publicPath: m2[1] }
  }
  return null
}

function toRelTarget(modulePath) {
  return modulePath.replace(/^\.\/src\/assets\/img\//, '')
}

function ensureDir(filePath) {
  fs.mkdirSync(path.dirname(filePath), { recursive: true })
}

function main() {
  console.log('=== 原版图标提取 ===')
  console.log('源:', ORIGINAL_WWW)
  console.log('目标:', TARGET_ROOT)
  console.log()

  const chunks = findIconChunks()
  console.log(`包含图片模块的 chunk: ${chunks.length} 个`)
  chunks.forEach(c => console.log('  ', path.basename(c)))
  console.log()

  const seen = new Map() // modulePath -> { kind, ... }
  for (const chunk of chunks) {
    const txt = fs.readFileSync(chunk, 'utf8')
    const mods = splitModules(txt)
    console.log(`[${path.basename(chunk)}] 切出 ${mods.length} 个模块段`)
    for (const { modulePath, body } of mods) {
      const evalStr = extractEvalContent(body)
      const data = parseModuleExport(evalStr, modulePath)
      if (!data) {
        console.warn('  ! 无法解析:', modulePath)
        continue
      }
      const prev = seen.get(modulePath)
      if (prev) {
        // 多 chunk 重复：跳过（应当一致）
        continue
      }
      seen.set(modulePath, data)
    }
  }
  console.log()
  console.log(`去重后 ${seen.size} 个模块`)
  console.log()

  // 写入目标
  let writtenB = 0, writtenF = 0, missing = 0
  for (const [modulePath, data] of seen) {
    const rel = toRelTarget(modulePath)
    const out = path.join(TARGET_ROOT, rel)
    ensureDir(out)
    if (data.kind === 'base64') {
      const buf = Buffer.from(data.b64, 'base64')
      fs.writeFileSync(out, buf)
      console.log(`  [base64] ${rel}  (${data.mime}, ${(buf.length / 1024).toFixed(1)} KB)`)
      writtenB++
    } else if (data.kind === 'file') {
      // 从 www/{publicPath} 拷贝实际文件
      const srcFile = path.join(ORIGINAL_WWW, data.publicPath)
      if (fs.existsSync(srcFile)) {
        fs.copyFileSync(srcFile, out)
        const size = fs.statSync(out).size
        console.log(`  [file]   ${rel}  (←${data.publicPath}, ${(size / 1024).toFixed(1)} KB)`)
        writtenF++
      } else {
        console.warn(`  ! 源文件不存在: ${srcFile} (for ${rel})`)
        missing++
      }
    }
  }
  console.log()
  console.log(`✓ base64 写入 ${writtenB} 个, file-loader 写入 ${writtenF} 个, 缺失 ${missing} 个`)
}

main()
