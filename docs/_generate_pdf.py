#!/usr/bin/env python3
"""将 hardware-data-protocol.md 转为可编辑 PDF（A4 横向 + 嵌入中文字体 + 防溢出）"""
import sys
from pathlib import Path
import markdown
from xhtml2pdf import pisa
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont

SRC = Path("/Users/yelinshan/work/天安/博世探路者系统/vaas-reproduction/docs/hardware-data-protocol.md")
DST = Path("/Users/yelinshan/work/天安/博世探路者系统/vaas-reproduction/docs/hardware-data-protocol.pdf")

# 注册中文字体 - 用 Arial Unicode.ttf（完整 Unicode TTF，包含所有汉字）
# 关键：xhtml2pdf 有自己的 DEFAULT_FONT 字典，必须把字体名加进去才能被识别
ZH_FONT = "ArialUnicode"
ZH_FONT_BOLD = "ArialUnicode-Bold"

# 1. reportlab 注册字体（用于实际渲染）
try:
    pdfmetrics.registerFont(TTFont(ZH_FONT, "/Library/Fonts/Arial Unicode.ttf"))
    pdfmetrics.registerFont(TTFont(ZH_FONT_BOLD, "/Library/Fonts/Arial Unicode.ttf"))
    print(f"✅ ReportLab 字体注册成功: {ZH_FONT}")
except Exception as e:
    print(f"⚠️  ReportLab 字体注册失败: {e}", file=sys.stderr)
    ZH_FONT = "Helvetica"
    ZH_FONT_BOLD = "Helvetica-Bold"

# 2. 把字体名加到 xhtml2pdf 的 DEFAULT_FONT 字典（让它认识这个字体名）
try:
    import xhtml2pdf.default
    xhtml2pdf.default.DEFAULT_FONT['arialunicode'] = ZH_FONT
    xhtml2pdf.default.DEFAULT_FONT['arialunicodems'] = ZH_FONT
    print(f"✅ xhtml2pdf DEFAULT_FONT 已加入: arialunicode -> {ZH_FONT}")
except Exception as e:
    print(f"⚠️  xhtml2pdf DEFAULT_FONT patch 失败: {e}", file=sys.stderr)

# 字体回退族
FONT_FAMILY = f'"{ZH_FONT}", "STSong-Light", "SimSun", "Helvetica", sans-serif'

# 横向 A4 + 防溢出 CSS
CSS = f"""
@page {{
    size: A4 landscape;
    margin: 1.5cm 1.2cm 1.5cm 1.2cm;
    @frame footer {{
        -pdf-frame-content: footerContent;
        bottom: 0.4cm;
        margin-left: 1.2cm;
        margin-right: 1.2cm;
        height: 0.7cm;
    }}
}}

#footerContent {{
    font-size: 7pt;
    color: #888;
    text-align: center;
    width: 100%;
    font-family: {FONT_FAMILY};
}}

* {{
    word-wrap: break-word;
    word-break: break-word;
    font-family: {FONT_FAMILY};
}}

body {{
    font-family: {FONT_FAMILY};
    font-size: 9pt;
    line-height: 1.4;
    color: #1a1a1a;
}}

/* 强制所有元素都用同一个字体（xhtml2pdf 对继承支持差）*/
p, div, span, h1, h2, h3, h4, h5, h6,
table, tr, td, th, thead, tbody, tfoot,
ul, ol, li, blockquote, code, pre, a, b, i, strong, em,
col, colgroup {{
    font-family: {FONT_FAMILY};
}}

h1 {{
    font-size: 18pt;
    color: #1f4e79;
    border-bottom: 2pt solid #1f4e79;
    padding-bottom: 4pt;
    margin-top: 0;
    margin-bottom: 8pt;
    page-break-before: avoid;
    font-family: {FONT_FAMILY};
}}

h2 {{
    font-size: 13pt;
    color: #1f4e79;
    background-color: #e7f0f7;
    padding: 3pt 6pt;
    margin-top: 10pt;
    margin-bottom: 6pt;
    page-break-after: avoid;
    font-family: {FONT_FAMILY};
}}

h3 {{
    font-size: 11pt;
    color: #2e5c8a;
    border-left: 3pt solid #2e5c8a;
    padding-left: 6pt;
    margin-top: 8pt;
    margin-bottom: 4pt;
    page-break-after: avoid;
    font-family: {FONT_FAMILY};
}}

h4 {{
    font-size: 10pt;
    color: #2e5c8a;
    margin-top: 6pt;
    margin-bottom: 3pt;
    page-break-after: avoid;
    font-family: {FONT_FAMILY};
}}

p {{
    margin: 3pt 0;
    text-align: justify;
}}

blockquote {{
    background-color: #f8f8f2;
    border-left: 3pt solid #aaa;
    padding: 3pt 6pt;
    margin: 4pt 0;
    color: #444;
    font-size: 8.5pt;
    font-family: {FONT_FAMILY};
}}

ul, ol {{
    margin: 3pt 0;
    padding-left: 18pt;
}}

li {{
    margin: 1pt 0;
}}

code {{
    font-family: "Courier", "Courier New", monospace;
    font-size: 8pt;
    background-color: #f4f4f4;
    padding: 0 2pt;
    word-break: break-all;
}}

pre {{
    font-family: "Courier", "Courier New", monospace;
    font-size: 7.5pt;
    background-color: #f4f4f4;
    border: 0.5pt solid #ddd;
    padding: 5pt;
    margin: 4pt 0;
    white-space: pre-wrap;
    word-wrap: break-word;
    word-break: break-all;
    line-height: 1.25;
    page-break-inside: avoid;
    overflow: hidden;
}}

/* 表格：固定列宽 + 自动换行 + 表头不换行 */
table {{
    width: 100%;
    border-collapse: collapse;
    margin: 4pt 0;
    font-size: 7.5pt;
    table-layout: fixed;
    page-break-inside: avoid;
}}

/* 字段名单元格：等宽字体略小，避免长字段名换行（用中文字体避免方块）*/
td:first-child {{
    font-family: "Courier", "Courier New", {ZH_FONT}, monospace;
    font-size: 7pt;
}}

th {{
    background-color: #1f4e79;
    color: #ffffff;
    font-weight: bold;
    padding: 3pt 4pt;
    border: 0.5pt solid #1f4e79;
    text-align: center;
    vertical-align: middle;
    white-space: nowrap;
    font-family: {FONT_FAMILY};
}}

td {{
    padding: 2.5pt 4pt;
    border: 0.5pt solid #c0c0c0;
    vertical-align: top;
    word-wrap: break-word;
    word-break: break-word;
    font-family: {FONT_FAMILY};
    line-height: 1.3;
}}

tr:nth-child(even) td {{
    background-color: #f7fafd;
}}

strong {{
    color: #1f4e79;
    font-weight: bold;
}}

em {{
    color: #555;
}}

hr {{
    border: 0;
    border-top: 0.5pt dashed #aaa;
    margin: 8pt 0;
}}
"""

# 关键 HTML 预处理：给 markdown 表格加列宽控制 + 给 td 文字强制包 font 标签
def fix_table_widths(html: str) -> str:
    """根据表头字段调整列宽，避免内容溢出；同时给 td 文字包 <font face> 强制字体"""
    import re

    # 字段清单表列宽（7 列）：字段名 26%，数据类型 9%，含义 13%，单位 7%，范围 9%，必填 6%，用途 30%
    FIELD_WIDTHS = ["26%", "9%", "13%", "7%", "9%", "6%", "30%"]
    META_WIDTHS = ["28%", "16%", "12%", "44%"]
    SERVICE_WIDTHS = ["5%", "18%", "12%", "17%", "14%", "34%"]

    def wrap_td_content(td_html: str) -> str:
        """给 td 内的纯文本包 <font face="ArialUnicode">"""
        # 取出 td 标签内部内容
        m = re.match(r'(<td[^>]*>)(.*?)(</td>)$', td_html, re.DOTALL)
        if not m:
            return td_html
        open_tag, inner, close_tag = m.group(1), m.group(2), m.group(3)
        # 如果内部已有 <font> 或 <code>，不包
        if '<font' in inner or '<code' in inner:
            return td_html
        # 包 font 标签
        return f'{open_tag}<font face="ArialUnicode">{inner}</font>{close_tag}'

    def process_table(match):
        table_html = match.group(0)
        thead_match = re.search(r'<thead>(.*?)</thead>', table_html, re.DOTALL)
        if not thead_match:
            return table_html
        ths = re.findall(r'<th[^>]*>(.*?)</th>', thead_match.group(1), re.DOTALL)
        ths_clean = [re.sub(r'<[^>]+>', '', t).strip() for t in ths]
        n = len(ths_clean)
        first_col = ths_clean[0] if ths_clean else ""
        all_cols = " ".join(ths_clean)

        if n == 7 and "字段名" in first_col and "数据类型" in all_cols and "含义" in all_cols:
            widths = FIELD_WIDTHS
        elif n == 6 and "字段名" in first_col and "含义" in all_cols:
            # 兼容旧版本 6 列表
            widths = ["30%", "14%", "7%", "9%", "7%", "33%"]
        elif n == 4 and "字段" in first_col and "类型" in all_cols:
            widths = META_WIDTHS
        elif n >= 5 and ("服务" in all_cols or "入口协议" in all_cols):
            widths = SERVICE_WIDTHS[:n]
        else:
            widths = None

        if widths:
            # 直接在 th 上加 style="width:NN%; text-align: center;"（xhtml2pdf 不支持 colgroup.width）
            new_thead = thead_match.group(1)
            for i, w in enumerate(widths[:n]):
                th_pattern = r'<th>'
                th_replacement = f'<th style="width: {w}; text-align: center;">'
                new_thead = new_thead.replace('<th>', th_replacement, 1)
            table_html = table_html.replace(thead_match.group(1), new_thead, 1)

        # 给所有 td 内的纯文字包 <font face="ArialUnicode">（强制字体选择）
        # 注意：th 已经在上面用 style 强制了，不动
        table_html = re.sub(
            r'<td[^>]*>.*?</td>',
            lambda m: wrap_td_content(m.group(0)),
            table_html,
            flags=re.DOTALL,
        )
        return table_html

    return re.sub(r'<table>.*?</table>', process_table, html, flags=re.DOTALL)

def md_to_html(md_text: str) -> str:
    html = markdown.markdown(
        md_text,
        extensions=[
            "tables",
            "fenced_code",
            "codehilite",
            "sane_lists",
        ],
        extension_configs={
            "codehilite": {"css_class": "codehilite", "guess_lang": False},
        },
    )
    html = fix_table_widths(html)
    full_html = f"""<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>{CSS}</style>
</head>
<body>
{html}
<div id="footerContent">车端硬件数据协议 VaaS · 城市级道路状态感知和预警系统 · 第 <pdf:pagenumber/> 页 / 共 <pdf:pagecount/> 页</div>
</body>
</html>"""
    return full_html

def main():
    md_text = SRC.read_text(encoding="utf-8")
    html = md_to_html(md_text)
    with open(DST, "wb") as f:
        result = pisa.CreatePDF(html, dest=f, encoding="utf-8")
    if result.err:
        print(f"❌ PDF 生成失败: {result.err}", file=sys.stderr)
        sys.exit(1)
    size_kb = DST.stat().st_size / 1024
    print(f"✅ PDF 已生成: {DST}  ({size_kb:.1f} KB)  字体: {ZH_FONT} | 横向A4 | 可编辑")

if __name__ == "__main__":
    main()
