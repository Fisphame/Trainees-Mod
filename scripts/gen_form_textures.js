#!/usr/bin/env node
/**
 * 生成物质形态占位底图（§19.15 步骤⑤）。
 *
 * 输出：src/main/resources/assets/trainees/textures/item/substance/<form>.png
 * 规格：16×16 RGBA PNG，**灰阶 + alpha 形状**（颜色由 ItemColor 以 tint 方式乘上去，
 *       所以底图必须接近白色/灰色，tint 才能得到正确色相）。
 *
 * 这些是程序化占位图（确定性、可复现）。美术替换时保持同样的尺寸/灰阶约定即可，
 * 代码与模型 JSON 无需改动。
 *
 * 用法：node scripts/gen_form_textures.js
 */
const fs = require('fs');
const path = require('path');
const zlib = require('zlib');

const SIZE = 16;
const OUT_DIR = path.join(__dirname, '..', 'src', 'main', 'resources', 'assets', 'trainees', 'textures', 'item', 'substance');

// ---------- PNG 编码（无依赖） ----------
const CRC_TABLE = (() => {
    const table = new Int32Array(256);
    for (let n = 0; n < 256; n++) {
        let c = n;
        for (let k = 0; k < 8; k++) c = c & 1 ? 0xEDB88320 ^ (c >>> 1) : c >>> 1;
        table[n] = c;
    }
    return table;
})();

function crc32(buf) {
    let c = 0xFFFFFFFF;
    for (let i = 0; i < buf.length; i++) c = CRC_TABLE[(c ^ buf[i]) & 0xFF] ^ (c >>> 8);
    return (c ^ 0xFFFFFFFF) >>> 0;
}

function chunk(type, data) {
    const len = Buffer.alloc(4);
    len.writeUInt32BE(data.length, 0);
    const typeBuf = Buffer.from(type, 'ascii');
    const crcBuf = Buffer.alloc(4);
    crcBuf.writeUInt32BE(crc32(Buffer.concat([typeBuf, data])), 0);
    return Buffer.concat([len, typeBuf, data, crcBuf]);
}

function encodePng(rgba) {
    return encodePngRaw(rgba, SIZE, SIZE);
}

/** 通用编码：RGBA 原始像素（无 filter 字节）→ PNG */
function encodePngRaw(rgba, width, height) {
    const stride = width * 4;
    const raw = Buffer.alloc((stride + 1) * height);
    for (let y = 0; y < height; y++) {
        raw[y * (stride + 1)] = 0; // filter: none
        rgba.copy(raw, y * (stride + 1) + 1, y * stride, (y + 1) * stride);
    }
    const ihdr = Buffer.alloc(13);
    ihdr.writeUInt32BE(width, 0);
    ihdr.writeUInt32BE(height, 4);
    ihdr[8] = 8;  // bit depth
    ihdr[9] = 6;  // color type RGBA
    const sig = Buffer.from([0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A]);
    return Buffer.concat([
        sig,
        chunk('IHDR', ihdr),
        chunk('IDAT', zlib.deflateSync(raw, { level: 9 })),
        chunk('IEND', Buffer.alloc(0)),
    ]);
}

// ---------- 画布工具 ----------
class Canvas {
    constructor() {
        this.px = Buffer.alloc(SIZE * SIZE * 4); // 全透明
    }
    set(x, y, lum, alpha = 255) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) return;
        const i = (y * SIZE + x) * 4;
        this.px[i] = lum;
        this.px[i + 1] = lum;
        this.px[i + 2] = lum;
        this.px[i + 3] = Math.max(0, Math.min(255, Math.round(alpha)));
    }
    /** 源覆盖：把 (x,y) 的 alpha 设为 a（用于形状遮罩） */
    maskAlpha(x, y, a) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) return;
        const i = (y * SIZE + x) * 4;
        this.px[i + 3] = Math.max(0, Math.min(255, Math.round(a)));
    }
    get(x, y) {
        const i = (y * SIZE + x) * 4;
        return { lum: this.px[i], a: this.px[i + 3] };
    }
}

function rng(seed) {
    let s = seed >>> 0;
    return () => {
        s = (s * 1664525 + 1013904223) >>> 0;
        return s / 0xFFFFFFFF;
    };
}

// ---------- 各形态 ----------
function powder() {
    const c = new Canvas();
    const rand = rng(1001);
    for (let y = 0; y < SIZE; y++) {
        for (let x = 0; x < SIZE; x++) {
            // 小丘形状：中间高、边缘低
            const dx = (x - 7.5) / 8, dy = (y - 10.5) / 6.5;
            const inside = dx * dx + dy * dy < 1.0 && y > 3;
            if (!inside) continue;
            const n = rand();
            const lum = 150 + Math.floor(n * 105);        // 150~255 颗粒噪点
            const a = 235 + Math.floor(rand() * 20);
            c.set(x, y, lum, a);
        }
    }
    return c;
}

function crystal() {
    const c = new Canvas();
    // 宝石式分面（避免按角度分面在中心退化出黑心）：
    // 顶部菱形高光 → 中部左右棱面 → 底部收尖，边缘一圈暗描边
    const inBody = (x, y) => {
        if (y < 2 || y > 13) return false;
        if (y <= 5) {                       // 顶部菱形
            const half = 1 + (y - 2) * 1.2;
            return x >= 7.5 - half && x <= 7.5 + half;
        }
        const t = (y - 5) / 8;              // 0 → 1 向下收窄
        const half = 4.6 - t * 3.4;
        return x >= 7.5 - half && x <= 7.5 + half;
    };
    for (let y = 0; y < SIZE; y++) {
        for (let x = 0; x < SIZE; x++) {
            if (!inBody(x, y)) continue;
            let lum;
            if (y <= 5) {
                lum = 248 - Math.abs(x - 7.5) * 6;             // 顶面：中心最亮
            } else {
                const left = x < 7.5;
                const t = (y - 6) / 7;
                lum = (left ? 205 : 180) - t * 25 + (y % 2 === 0 ? 4 : 0);
            }
            c.set(x, y, Math.max(120, Math.min(255, Math.round(lum))), 255);
        }
    }
    // 暗描边（紧邻透明处）
    const outline = [];
    for (let y = 0; y < SIZE; y++) {
        for (let x = 0; x < SIZE; x++) {
            if (!inBody(x, y)) continue;
            const edge = !inBody(x - 1, y) || !inBody(x + 1, y) || !inBody(x, y - 1) || !inBody(x, y + 1);
            if (edge) outline.push([x, y]);
        }
    }
    for (const [x, y] of outline) c.set(x, y, 118, 255);
    return c;
}

function granule() {
    const c = new Canvas();
    const rand = rng(3003);
    const pellets = [];
    for (let i = 0; i < 9; i++) {
        pellets.push({ x: 2 + rand() * 12, y: 2 + rand() * 12, r: 1.5 + rand() * 1.1 });
    }
    for (let y = 0; y < SIZE; y++) {
        for (let x = 0; x < SIZE; x++) {
            let best = null;
            for (const p of pellets) {
                const d = Math.hypot(x - p.x, y - p.y);
                if (d <= p.r && (!best || d < best.d)) best = { d, r: p.r };
            }
            if (!best) continue;
            // 球面明暗：中心亮、边缘暗
            const t = 1 - best.d / best.r;
            c.set(x, y, 140 + Math.floor(t * 110), 250);
        }
    }
    return c;
}

function bulk() {
    const c = new Canvas();
    for (let y = 0; y < SIZE; y++) {
        for (let x = 0; x < SIZE; x++) {
            // 锭状梯形轮廓
            const top = 5, bottom = 13;
            if (y < top || y > bottom) continue;
            const inset = Math.round((y - top) * 0.45);
            if (x < 2 + inset || x > SIZE - 3 - inset) continue;
            const lum = y <= top + 1 ? 250 : (y >= bottom - 1 ? 165 : 205);
            c.set(x, y, lum, 255);
        }
    }
    return c;
}

function liquidBase(withSoluteSpecks) {
    const c = new Canvas();
    const rand = rng(withSoluteSpecks ? 5005 : 4004);
    // 液滴/液面：水滴形，顶部高光
    for (let y = 0; y < SIZE; y++) {
        for (let x = 0; x < SIZE; x++) {
            const dx = (x - 7.5) / 6.6;
            const dy = (y - 9.0) / 6.2;
            const d = Math.sqrt(dx * dx + dy * dy);
            if (d > 1.0 || y < 3) continue;
            let lum = 190 + Math.floor((1 - d) * 40);
            // 高光
            if (x >= 4 && x <= 6 && y >= 5 && y <= 8) lum = 252;
            if (withSoluteSpecks && rand() > 0.88) lum = 150; // 溶质颗粒
            c.set(x, y, Math.min(255, lum), 235);
        }
    }
    // 液面弧线（略亮）
    for (let x = 3; x <= 12; x++) {
        const y = 5 + Math.round(Math.abs(x - 7.5) * 0.25);
        const cur = c.get(x, y);
        if (cur.a > 0) c.set(x, y, 250, cur.a);
    }
    return c;
}

function gas() {
    const c = new Canvas();
    const rand = rng(6006);
    const cx = 7.5, cy = 8.0;
    for (let y = 0; y < SIZE; y++) {
        for (let x = 0; x < SIZE; x++) {
            const d = Math.hypot((x - cx) / 7.2, (y - cy) / 6.4);
            if (d > 1.0) continue;
            const wisp = 0.5 + 0.5 * Math.sin((x + y) * 1.1) * Math.sin((x - y) * 0.7);
            const alpha = (1 - d) * (110 + 90 * wisp) * (0.7 + 0.3 * rand());
            c.set(x, y, 225 + Math.floor(rand() * 30), alpha);
        }
    }
    return c;
}

const FORMS = {
    powder,
    crystal,
    granule,
    bulk,
    liquid: () => liquidBase(false),
    solution: () => liquidBase(true),
    gas,
};

/** 7 合 1 对照图（仅开发用，放大 6 倍，写进 scripts/.out/） */
function montage(canvases, scale = 6) {
    const order = Object.keys(FORMS);
    const w = SIZE * scale * order.length;
    const h = SIZE * scale;
    const out = Buffer.alloc(w * h * 4);
    order.forEach((name, idx) => {
        const src = canvases[name];
        for (let y = 0; y < SIZE; y++) {
            for (let x = 0; x < SIZE; x++) {
                const sp = (y * SIZE + x) * 4;
                const a = src[sp + 3] / 255;
                const lum = src[sp];
                // 深色棋盘背景，便于看清透明区域与灰阶
                const checker = ((x >> 1) + (y >> 1)) % 2 === 0 ? 60 : 90;
                const r = Math.round(lum * a + checker * (1 - a));
                for (let sy = 0; sy < scale; sy++) {
                    for (let sx = 0; sx < scale; sx++) {
                        const dx = (idx * SIZE + x) * scale + sx;
                        const dy = y * scale + sy;
                        const dp = (dy * w + dx) * 4;
                        out[dp] = r;
                        out[dp + 1] = r;
                        out[dp + 2] = r;
                        out[dp + 3] = 255;
                    }
                }
            }
        }
    });
    const ihdr = Buffer.alloc(13);
    ihdr.writeUInt32BE(w, 0);
    ihdr.writeUInt32BE(h, 4);
    ihdr[8] = 8;
    ihdr[9] = 6;
    void ihdr;
    return encodePngRaw(out, w, h);
}

function main() {
    fs.mkdirSync(OUT_DIR, { recursive: true });
    const canvases = {};
    for (const [name, draw] of Object.entries(FORMS)) {
        canvases[name] = draw().px;
        const png = encodePng(canvases[name]);
        const file = path.join(OUT_DIR, `${name}.png`);
        fs.writeFileSync(file, png);
        console.log(`generated ${path.relative(process.cwd(), file)} (${png.length} bytes)`);
    }
    const outDir = path.join(__dirname, '.out');
    fs.mkdirSync(outDir, { recursive: true });
    const montageFile = path.join(outDir, 'forms_montage.png');
    fs.writeFileSync(montageFile, montage(canvases));
    console.log(`montage ${path.relative(process.cwd(), montageFile)} (order: ${Object.keys(FORMS).join(', ')})`);
}

main();
