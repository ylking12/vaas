# 原大屏视觉规范（v2 提取）

## 1. CSS 变量（:root）

- `--fa-style-family-brands`: `'Font Awesome 6 Brands'`
- `--fa-style-family-classic`: `'Font Awesome 6 Free'`
- `--fa-font-solid`: `normal 900 1em/1 'Font Awesome 6 Free'`
- `--fa-font-regular`: `normal 400 1em/1 'Font Awesome 6 Free'`
- `--fa-font-brands`: `normal 400 1em/1 'Font Awesome 6 Brands'`

## 2. Body 默认样式

```json
{
  "font": "12px / 13.8px \"Noto Sans SC\"",
  "fontSize": "12px",
  "fontFamily": "\"Noto Sans SC\"",
  "fontWeight": "400",
  "color": "rgb(0, 0, 0)",
  "background": "rgba(0, 0, 0, 0)",
  "lineHeight": "13.8px",
  "letterSpacing": "normal"
}
```

## 3. 标题样式（h1/h2/h3）

```json
{
  "font": "500 19.2px / 22.08px \"Noto Sans SC\"",
  "fontSize": "19.2px",
  "fontFamily": "\"Noto Sans SC\"",
  "fontWeight": "500",
  "color": "rgb(255, 255, 255)",
  "background": "rgba(0, 0, 0, 0)",
  "lineHeight": "22.08px",
  "letterSpacing": "normal"
}
```

## 4. 按钮样式

```json
{
  "font": "19.2px / 22.08px \"Noto Sans SC\"",
  "fontSize": "19.2px",
  "fontFamily": "\"Noto Sans SC\"",
  "fontWeight": "400",
  "color": "rgb(255, 255, 255)",
  "background": "rgb(85, 85, 85)",
  "lineHeight": "22.08px",
  "letterSpacing": "normal"
}
```

## 5. 面板样式（前 5 个）

### panel-0
```json
{
  "class": "left-panel",
  "background": "rgba(0, 0, 0, 0)",
  "border": "0px none rgb(0, 0, 0)",
  "borderRadius": "0px",
  "padding": "0px"
}
```

### panel-1
```json
{
  "class": "panel-btn dis ani",
  "background": "rgb(0, 0, 0)",
  "border": "0px none rgb(255, 246, 218)",
  "borderRadius": "0px 15.001px 0px 0px",
  "padding": "20.0006px 13.3325px"
}
```

### panel-2
```json
{
  "class": "section panel-in-ani",
  "background": "rgba(0, 0, 0, 0.6)",
  "border": "0px none rgb(0, 0, 0)",
  "borderRadius": "0px",
  "padding": "0px"
}
```
