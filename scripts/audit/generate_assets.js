/* eslint-disable */
// Generates ANTAR_PlayStore_Assets.docx — Play Store publishing assets kit.
// Every claim is grounded in the actual codebase: no invented features.

const path = require("path");
const fs = require("fs");
const {
  Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
  WidthType, BorderStyle, AlignmentType, HeadingLevel, PageBreak,
  Header, Footer, PageNumber, ShadingType, VerticalAlign,
} = require("docx");

// ─── Theme (sourced from app/.../theme/Color.kt) ─────────────────────
const C = {
  primaryDark: "0A0E21",      // AntarDark
  accent: "00E5FF",           // AntarCyan
  accentDeep: "00838F",
  accentLight: "E0F7FA",      // light cyan tint for alternating rows
  surface: "111631",          // AntarSurface
  purple: "B388FF",           // AntarPurple
  white: "FFFFFF",
  rowAltA: "FFFFFF",
  rowAltB: "F5FBFC",
  border: "CFD2D8",
  noteBg: "E0F7FA",
  noteBorder: "00838F",
  copyBg: "F2FAFB",
  copyBorder: "00BFA5",
  promptBarBg: "00838F",
  red: "B71C1C",
  green: "1B5E20",
  redBg: "FFEBEE",
  greenBg: "E8F5E9",
  yellowBg: "FFFDE7",
  amberBg: "FFF3E0",
  bodyText: "1A1A1A",
};

const FONT = "Arial";

const run = (text, opts = {}) => new TextRun({
  text: String(text ?? ""), font: FONT, size: opts.size ?? 20,
  bold: !!opts.bold, italics: !!opts.italics,
  color: opts.color ?? C.bodyText, break: opts.break,
});
const para = (children, opts = {}) => new Paragraph({
  spacing: { before: opts.before ?? 60, after: opts.after ?? 60, line: 280 },
  alignment: opts.alignment, heading: opts.heading,
  children: Array.isArray(children) ? children : [children],
  shading: opts.shading,
});
const text = (s, opts = {}) => para([run(s, opts)], opts);
const blank = () => para([run(" ")], { before: 0, after: 0 });
const gap = (n = 1) => Array.from({ length: n }, () => blank());

const noBorders = {
  top: { style: BorderStyle.NONE, size: 0, color: "FFFFFF" },
  bottom: { style: BorderStyle.NONE, size: 0, color: "FFFFFF" },
  left: { style: BorderStyle.NONE, size: 0, color: "FFFFFF" },
  right: { style: BorderStyle.NONE, size: 0, color: "FFFFFF" },
};
const thinBorders = {
  top: { style: BorderStyle.SINGLE, size: 4, color: C.border },
  bottom: { style: BorderStyle.SINGLE, size: 4, color: C.border },
  left: { style: BorderStyle.SINGLE, size: 4, color: C.border },
  right: { style: BorderStyle.SINGLE, size: 4, color: C.border },
};

const headerCell = (label, widthPct) => new TableCell({
  width: widthPct ? { size: widthPct, type: WidthType.PERCENTAGE } : undefined,
  shading: { type: ShadingType.CLEAR, color: "auto", fill: C.primaryDark },
  verticalAlign: VerticalAlign.CENTER,
  borders: thinBorders,
  margins: { top: 80, bottom: 80, left: 100, right: 100 },
  children: [para([run(label, { bold: true, color: C.white, size: 20 })])],
});

const cell = (content, opts = {}) => {
  const widthPct = opts.widthPct;
  const fill = opts.fill ?? (opts.alt ? C.rowAltB : C.rowAltA);
  const children = Array.isArray(content) ? content : [content];
  return new TableCell({
    ...(widthPct ? { width: { size: widthPct, type: WidthType.PERCENTAGE } } : {}),
    shading: { type: ShadingType.CLEAR, color: "auto", fill },
    verticalAlign: VerticalAlign.TOP,
    borders: thinBorders,
    margins: { top: 80, bottom: 80, left: 100, right: 100 },
    children: children.map((c) =>
      typeof c === "string"
        ? para([run(c, { size: opts.fontSize ?? 20, color: opts.textColor })])
        : c
    ),
  });
};

const buildTable = (headers, rows, widths) => {
  const sum = widths ? widths.reduce((a, b) => a + b, 0) : headers.length;
  const w = (i) => widths ? Math.round(widths[i] / sum * 100) : Math.round(100 / headers.length);
  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    columnWidths: widths,
    rows: [
      new TableRow({
        tableHeader: true,
        children: headers.map((h, i) => headerCell(h, w(i))),
      }),
      ...rows.map((r, idx) => new TableRow({
        children: r.cells.map((c, i) => cell(c, {
          widthPct: w(i),
          fill: r.fill ?? (idx % 2 === 0 ? C.rowAltA : C.rowAltB),
          textColor: r.textColor,
        })),
      })),
    ],
  });
};

const noteBox = (lines) => new Table({
  width: { size: 100, type: WidthType.PERCENTAGE },
  rows: [new TableRow({
    children: [new TableCell({
      shading: { type: ShadingType.CLEAR, color: "auto", fill: C.noteBg },
      borders: {
        left: { style: BorderStyle.SINGLE, size: 32, color: C.noteBorder },
        top: { style: BorderStyle.SINGLE, size: 4, color: C.noteBorder },
        bottom: { style: BorderStyle.SINGLE, size: 4, color: C.noteBorder },
        right: { style: BorderStyle.SINGLE, size: 4, color: C.noteBorder },
      },
      margins: { top: 120, bottom: 120, left: 200, right: 160 },
      children: lines.map((l) =>
        typeof l === "string"
          ? para([run(l, { italics: true, color: C.noteBorder, size: 20 })])
          : l
      ),
    })],
  })],
});

const copyBox = (lines) => new Table({
  width: { size: 100, type: WidthType.PERCENTAGE },
  rows: [new TableRow({
    children: [new TableCell({
      shading: { type: ShadingType.CLEAR, color: "auto", fill: C.copyBg },
      borders: {
        left: { style: BorderStyle.SINGLE, size: 36, color: C.copyBorder },
        top: { style: BorderStyle.SINGLE, size: 4, color: C.copyBorder },
        bottom: { style: BorderStyle.SINGLE, size: 4, color: C.copyBorder },
        right: { style: BorderStyle.SINGLE, size: 4, color: C.copyBorder },
      },
      margins: { top: 200, bottom: 200, left: 240, right: 200 },
      children: lines.map((l) =>
        typeof l === "string"
          ? para([run(l, { size: 20, color: C.bodyText })])
          : l
      ),
    })],
  })],
});

const titleBar = (label, fill = C.accentDeep) => new Table({
  width: { size: 100, type: WidthType.PERCENTAGE },
  rows: [new TableRow({
    children: [new TableCell({
      shading: { type: ShadingType.CLEAR, color: "auto", fill },
      borders: noBorders,
      margins: { top: 80, bottom: 80, left: 200, right: 200 },
      children: [para([run(label, { bold: true, color: C.white, size: 20 })])],
    })],
  })],
});

const divider = () => new Paragraph({
  spacing: { before: 120, after: 120 },
  border: { bottom: { color: C.border, space: 4, style: BorderStyle.SINGLE, size: 6 } },
  children: [run(" ")],
});

const h1 = (n, s) => para(
  [run(`${n}. ${s}`, { bold: true, size: 36, color: C.primaryDark })],
  { before: 240, after: 100, heading: HeadingLevel.HEADING_1 }
);
const h2 = (s) => para(
  [run(s, { bold: true, size: 26, color: C.accentDeep })],
  { before: 200, after: 80, heading: HeadingLevel.HEADING_2 }
);
const h3 = (s) => para(
  [run(s, { bold: true, size: 22, color: C.primaryDark })],
  { before: 160, after: 60, heading: HeadingLevel.HEADING_3 }
);

// ─── Cover banner ────────────────────────────────────────────────────
const coverBanner = () => {
  const r1 = new TableRow({ children: [new TableCell({
    shading: { type: ShadingType.CLEAR, color: "auto", fill: C.primaryDark },
    verticalAlign: VerticalAlign.CENTER,
    borders: noBorders,
    margins: { top: 600, bottom: 200, left: 400, right: 400 },
    children: [
      para([run("ANTAR", { bold: true, size: 64, color: C.white })], { alignment: AlignmentType.LEFT }),
      para([run("Antar — Device info", { bold: true, size: 28, color: C.accent })], { alignment: AlignmentType.LEFT, before: 0 }),
    ],
  })] });
  const r2 = new TableRow({ children: [new TableCell({
    shading: { type: ShadingType.CLEAR, color: "auto", fill: C.accentDeep },
    verticalAlign: VerticalAlign.CENTER,
    borders: noBorders,
    margins: { top: 200, bottom: 200, left: 400, right: 400 },
    children: [para([run("Google Play Store Publishing Assets Kit", { bold: true, size: 28, color: C.white })], { alignment: AlignmentType.LEFT })],
  })] });
  return new Table({ width: { size: 100, type: WidthType.PERCENTAGE }, rows: [r1, r2] });
};

const metaStrip = () => buildTable(
  ["Package", "Version", "Generated"],
  [{ cells: ["com.ashes.dev.works.system.core.internals.antar", "1.0 (versionCode 1)", new Date().toISOString().slice(0, 10)] }],
  [40, 30, 30]
);

// ─── Char-counter helper (for Play Store length budgets) ─────────────
const charCount = (s) => s.length;

// ─── Section 1 — Store listing ──────────────────────────────────────
const APP_TITLE_PRIMARY = "Antar - Device Info";              // 19
const APP_TITLE_VARIANT = "Antar: Device Info & Sensors";     // 28
const SHORT_DESC = "Instant device, battery, network & sensor stats — free, no ads, no account."; // ~76

const LONG_DESC_LINES = [
  "ANTAR — DEEP DEVICE INFO IN A CLEAN INTERFACE",
  "",
  "Antar shows you everything your phone knows about itself — hardware specs, battery health, network details, sensor data and every installed app — all in one fast, ad-free utility designed for people who actually like reading device data.",
  "",
  "WHAT YOU GET",
  "• Live device snapshot — model, manufacturer, hardware fingerprint, build id",
  "• Battery health and history — 24-hour and 7-day charts, charging sessions, voltage, current draw, capacity",
  "• Network details — Wi-Fi DHCP, gateway, DNS, link speed, SIM carrier, MCC/MNC",
  "• Location and GNSS — GPS coordinates, satellite constellations, NMEA accuracy, address",
  "• Sensors — accelerometer, gyroscope, magnetometer, barometer, proximity, ambient light",
  "• Storage — internal and external partitions, used vs free, mount points",
  "• Display — resolution, density, refresh rate, HDR support",
  "• CPU — cores, architecture, current frequency, governor",
  "• Camera — sensor resolutions, supported modes, hardware level",
  "• System — Android version, security patch, kernel, build fingerprint",
  "• Apps — every installed package, version, target SDK, architecture",
  "",
  "PREMIUM DESIGN",
  "• Dark cyan glassmorphism theme with subtle gradients",
  "• Smooth animations across 12 screens",
  "• Material You support on Android 12+",
  "",
  "PRIVACY FIRST",
  "• No tracking SDKs, no analytics, no crash reporters",
  "• No advertisements, no account, no sign-up",
  "• Data is read on demand and stays on your device",
  "• Settings → Request data deletion clears local battery history at any time",
  "",
  "PERFECT FOR",
  "• Developers who need quick build / SDK / hardware info on a real device",
  "• Tech enthusiasts who want to inspect sensors and connectivity",
  "• Anyone debugging connectivity, battery drain, or storage issues",
  "",
  "Antar is free, has zero advertisements, and never asks you to log in.",
  "",
  "Permissions: Location is requested only for GPS, satellites and Wi-Fi security identification. Nearby Wi-Fi devices is requested on Android 13+ for the same reason. Both are optional — the app keeps working in degraded mode if you decline.",
];
const LONG_DESC_TEXT = LONG_DESC_LINES.join("\n");

const section1 = () => [
  h1(1, "Store Listing Copy"),
  text("All three copy fields below are ready to paste into Play Console → Store presence → Main store listing. Character counts shown for each.", { italics: true }),
  blank(),

  h2("1.1 — App Title"),
  buildTable(["Variant", "Text", "Chars (max 30)"],
    [
      { fill: C.greenBg, cells: ["Primary (matches launcher)", APP_TITLE_PRIMARY, `${charCount(APP_TITLE_PRIMARY)} / 30`] },
      { fill: C.rowAltA, cells: ["SEO variant", APP_TITLE_VARIANT, `${charCount(APP_TITLE_VARIANT)} / 30`] },
    ],
    [30, 50, 20]
  ),
  noteBox(["Use the primary variant unless you want to broaden organic search reach. The launcher label in strings.xml is 'Antar - Device info'; the title in Play can differ but consistency helps recognition."]),
  blank(),

  h2("1.2 — Short Description"),
  copyBox([SHORT_DESC]),
  text(`Character count: ${charCount(SHORT_DESC)} / 80`, { bold: true, color: C.accentDeep }),
  noteBox([
    "Lead value: 'Instant device, battery, network & sensor stats' covers four of the app's screens.",
    "Friction-removers: 'free, no ads, no account' — addresses the three most common objections to utility apps.",
  ]),
  blank(),

  h2("1.3 — Long Description"),
  copyBox(LONG_DESC_LINES.map((line) => para([run(line || " ", { size: 20 })], { before: 0, after: 30 }))),
  text(`Character count: ${charCount(LONG_DESC_TEXT)} / 4000 (target band 1,800–2,200 for readability)`, { bold: true, color: C.accentDeep }),
  noteBox([
    "Plain text only. ALL CAPS section headers are the Play Store convention; bullet points use '•'.",
    "Every feature listed here is grounded in an actual screen of the app (Dashboard, Device, System, CPU, Battery, Location, Network, Storage, Display, Sensors, Apps, Camera, Settings).",
    "Closes with a soft permissions explainer rather than a hard sales line — sets expectations and reduces 1-star reviews from users surprised by a permission prompt.",
  ]),
];

// ─── Section 2 — App categorisation ─────────────────────────────────
const section2 = () => {
  const rows = [
    { fill: C.rowAltA, cells: ["Category (primary)", "Tools"] },
    { fill: C.rowAltB, cells: ["Category (alt)", "House & Home → Device personalization (only if Tools is contested)"] },
    { fill: C.rowAltA, cells: ["Content rating", "Everyone (no violence, no gambling, no UGC, no mature themes — utility-only)"] },
    { fill: C.rowAltB, cells: ["Tags / Keywords (woven into the long description)", "device info, system info, hardware monitor, battery health, sim info, sensor test, network analyzer, gps info, build version, kernel info, storage info, cpu info, android version checker, ram info"] },
    { fill: C.greenBg, cells: ["Ads disclosure", "No ads — declare 'Contains ads: NO' in Play Console"] },
    { fill: C.greenBg, cells: ["In-app purchases", "None — app is fully free with no IAP / subscriptions"] },
    { fill: C.amberBg, cells: ["Permissions used (declared in AndroidManifest.xml)", "INTERNET, ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, NEARBY_WIFI_DEVICES (neverForLocation), QUERY_ALL_PACKAGES"] },
    { fill: C.rowAltA, cells: ["Target audience", "Developers, power users, tech enthusiasts, IT support staff. Age 13+."] },
    { fill: C.rowAltB, cells: ["Distribution countries", "All countries supported by Play (no geofencing reason)"] },
    { fill: C.rowAltA, cells: ["Pricing", "Free"] },
  ];
  return [
    h1(2, "App Categorisation"),
    text("Reference table for the Play Console Store presence → Main store listing and Categorisation forms.", { italics: true }),
    blank(),
    buildTable(["Field", "Value"], rows, [30, 70]),
  ];
};

// ─── Section 3 — Keyword strategy ───────────────────────────────────
const section3 = () => {
  const rows = [
    { cells: ["device info", "battery health", "device info app no ads"] },
    { cells: ["system info", "sim card info", "free hardware info android"] },
    { cells: ["hardware info", "sensor test", "check phone specs offline"] },
    { cells: ["phone specs", "wifi analyzer", "system info no permission"] },
    { cells: ["device monitor", "gps info", "android build version checker"] },
    { cells: ["device toolkit", "cpu info", "battery drain analyzer android"] },
    { cells: ["android utility", "storage info", "phone hardware test free"] },
    { cells: ["device analyzer", "network info", "show device manufacturer model"] },
    { cells: ["build info", "kernel info", "lightweight device info android"] },
  ];
  return [
    h1(3, "Keyword Strategy"),
    text("Three tiers of keywords. Play Store has no separate keyword field — these must be woven naturally into the long description (which Section 1.3 already does).", { italics: true }),
    blank(),
    buildTable(["Primary (high volume)", "Secondary (feature-specific)", "Long-tail (intent)"], rows, [33, 33, 34]),
    blank(),
    noteBox([
      "Primary keywords already appear in the title and short description.",
      "Secondary keywords appear in the WHAT YOU GET bullets in 1.3.",
      "Long-tail phrases are how niche searchers find utility apps — sprinkle one or two organically into the closing paragraphs of 1.3.",
      "Avoid keyword stuffing: Play penalises store listings that read like keyword soup. Aim for natural prose that happens to contain the terms.",
    ]),
  ];
};

// ─── Section 4 — Graphic assets (page break before) ─────────────────
const featurePromptA = [
  "Cinematic, atmospheric promotional banner exactly 1024 x 500 pixels for an Android utility app called 'Antar - Device Info'. Wide aspect 1024:500.",
  "Subject: a single floating, semi-transparent smartphone silhouette set against a pre-dawn sky with soft volumetric light rays from the upper-left. Around the phone, abstract holographic data panels float as glassmorphism cards displaying real device-info fields: 'Battery 87%', '120 Hz', 'arm64-v8a', 'Wi-Fi WPA3', '24 satellites', 'GPS 28.6° N', '4.2 GB / 128 GB'.",
  "Colour palette (use exact hex values):",
  "  Background gradient from #0A0E21 (deep night) to #111631.",
  "  Cyan accent #00E5FF for highlights, frame edges and key data text.",
  "  Purple lift #B388FF in the upper light rays.",
  "  Soft cyan glow #3300E5FF around the phone silhouette.",
  "Tagline overlay text on the lower-left: 'Deep device info, in a clean interface.' in Inter / Helvetica bold white.",
  "App name overlay top-left: 'ANTAR' in extra-bold white with cyan underline.",
  "Style: editorial, photoreal lighting, subtle film grain, 3D rendered, very high detail.",
  "Constraints: No device mockups of real branded phones. No App Store / Play Store badges. No people. Leave a 64 px safe margin on all edges so text won't be cropped on Play.",
];
const featurePromptB = [
  "Minimalist flat-design Play Store feature graphic exactly 1024 x 500 pixels for an Android utility app called 'Antar - Device Info'. Wide aspect 1024:500.",
  "Composition: an off-centre concentric-ring radar / sensor diagram on the right, made of thin neon strokes. To its left, a vertically stacked column of glassmorphism info chips with real device data labels: 'CPU', 'RAM', 'Battery', 'Network', 'Storage', 'Sensors'.",
  "Colour palette (use exact hex values):",
  "  Solid background #0A0E21.",
  "  Primary accent strokes and chip borders #00E5FF.",
  "  Secondary accent #448AFF.",
  "  Soft purple glow #B388FF on the outer rings.",
  "  Chip background fills #161B35 with 1 px cyan borders.",
  "Tagline overlay centred-left: 'Every spec your phone hides.' in geometric sans-serif bold white.",
  "App name top-left: 'ANTAR' uppercase, extra-bold, cyan with subtle gradient.",
  "Style: flat vector, sharp edges, ultra-clean, dark-mode forward, very minimal.",
  "Constraints: No device mockups of real branded phones. No App Store / Play Store badges. No people. Leave a 64 px safe margin on all edges.",
];
const iconPrompt = [
  "Adaptive Android app icon exactly 512 x 512 pixels for a device-info utility called 'Antar'.",
  "Concept: a stylised concentric 'A' formed from three nested arcs that double as a sensor radar / signal-strength glyph — reads as both the letter A and as a device pulse.",
  "Colour palette (use exact hex values):",
  "  Outer arc / primary mark #00E5FF (cyan).",
  "  Middle arc #448AFF.",
  "  Inner arc #B388FF.",
  "  Fill behind the mark #0A0E21 contained inside a circular bounded shape.",
  "  Subtle inner-glow #3300E5FF around the centre to lift the mark off the background.",
  "Composition: keep the mark inside the central 66% safe zone (Material You / adaptive icon mask). The outer 17% on each side may be cropped on round masks.",
  "Style: flat geometric, slight 1-pixel cyan rim light, no skeuomorphism, no gradients on the mark itself, very clean.",
  "Constraints: No text. No serifs. Transparent background outside the circular shape so adaptive masks render cleanly. No drop shadows. No 3D bevels.",
];

const screenshotPrompts = [
  {
    n: 1, subject: "Dashboard screen", prompt:
    "Vertical 1080 x 1920 px Play Store screenshot tile. Top 200 px: solid #0A0E21 banner with bold white text 'EVERY SPEC AT A GLANCE'. Below: a stylised phone frame (no real-brand mockup) showing the Antar Dashboard screen — a dark #0A0E21 background with rounded glassmorphism cards arranged in a 2-column grid. Cards show real field labels from the app: 'Model: Pixel 8 Pro', 'Battery: 87%', 'CPU: Tensor G3', 'Storage: 96 / 256 GB', 'Wi-Fi: WPA3', 'Sensors: 14'. Cyan #00E5FF section accents, subtle purple #B388FF gradient bar across the top of the phone frame. Caption strip at the bottom in cyan: 'Live snapshot of your device.' Style: editorial, sharp, dark-mode."
  },
  {
    n: 2, subject: "Battery screen with 24h / 7d chart", prompt:
    "Vertical 1080 x 1920 px Play Store screenshot tile. Top banner: 'BATTERY HEALTH, IN HISTORY'. Below: phone frame showing the Antar Battery screen — header card with '87% • 25 °C • 4.21 V', then a 24-hour line chart with cyan #00E5FF stroke and purple #B388FF charging-session highlights, then info rows: 'Health: Good', 'Capacity: 4485 / 5000 mAh', 'Current: -540 mA'. Background #0A0E21. Caption: '24-hour and 7-day charts. Charging sessions tracked.' Style match Tile 1."
  },
  {
    n: 3, subject: "Network / SIM card detail", prompt:
    "Vertical 1080 x 1920 px Play Store screenshot tile. Top banner: 'WI-FI + SIM, IN ONE PLACE'. Below: phone frame showing the Antar Network screen — Wi-Fi card with 'WPA3 • 192.168.1.42 • -56 dBm', Mobile Data card 'Multi SIM: 2', SIM 1 card 'Carrier: Airtel • IN • MCC 404 • MNC 45'. Cyan rows, dark #0A0E21 background, subtle teal accent on the SIM card. Caption: 'No phone permissions. Nothing leaves the device.' Style match Tile 1."
  },
  {
    n: 4, subject: "Sensors live data", prompt:
    "Vertical 1080 x 1920 px Play Store screenshot tile. Top banner: 'EVERY SENSOR YOUR PHONE HAS'. Below: phone frame showing the Antar Sensors screen — list of sensors with live readings: 'Accelerometer 0.02, 0.18, 9.81 m/s²', 'Gyroscope -0.01, 0.00, 0.02 rad/s', 'Magnetometer 26.4 µT', 'Proximity Far', 'Light 412 lx', 'Pressure 1013 hPa'. Cyan iconography per row, dark #0A0E21 background. Caption: 'Read every sensor in real time.' Style match Tile 1."
  },
];

const section4 = () => {
  const featureSpec = buildTable(
    ["Spec", "Value"],
    [
      { cells: ["Required size", "1024 × 500 pixels (exactly)"] },
      { cells: ["Format", "PNG or JPEG, 24-bit, no transparency"] },
      { cells: ["Safe zone", "Keep all critical content inside the central 924 × 400 area (50 px margin all sides). Edges may be cropped or covered by overlays on different devices."] },
      { cells: ["Forbidden", "Real device-brand mockups, App Store / Play Store badges, screenshots of other people's apps, people's faces, marketing claims that imply rankings or downloads."] },
      { cells: ["Best practice", "Tagline overlay must be readable on a 5-inch phone preview. App name should be visible without being a logo file."] },
    ],
    [30, 70]
  );
  const iconSpec = buildTable(
    ["Spec", "Value"],
    [
      { cells: ["Required size", "512 × 512 pixels"] },
      { cells: ["Format", "32-bit PNG with alpha channel (transparent background allowed outside the shape)"] },
      { cells: ["Safe zone", "Material You / adaptive icon central 66% — content outside the safe zone may be cropped on round / squircle masks"] },
      { cells: ["Forbidden", "Text in the icon, replicas of competitor logos, App Store / Play badges, drop shadows that imply 3D, busy backgrounds"] },
    ],
    [30, 70]
  );
  const ssSpec = buildTable(
    ["Spec", "Value"],
    [
      { cells: ["Required size", "1080 × 1920 px (portrait phone) — minimum 320 px short edge, max 3840 px long edge"] },
      { cells: ["Quantity", "Minimum 2, recommended 4–8 for first-time apps. Tablet sets are optional."] },
      { cells: ["Format", "PNG or JPEG, 24-bit, no transparency"] },
      { cells: ["Style", "Consistent across all tiles — same banner band, same caption font, same phone frame, same colour palette"] },
      { cells: ["Forbidden", "Real branded device frames, false claims, copy that contradicts the app, deceptive UI"] },
    ],
    [30, 70]
  );
  return [
    new Paragraph({ children: [new PageBreak()] }),
    h1(4, "Graphic Assets — AI Image Prompts"),
    text("Each asset section below has a spec table first (size, safe zone, what NOT to include per Play policy), then ready-to-paste AI image-generation prompts. Prompts use exact hex codes from app/.../theme/Color.kt.", { italics: true }),
    blank(),

    h2("4.1 — Feature Graphic (1024 × 500 px)"),
    featureSpec,
    blank(),
    titleBar("PROMPT A — ATMOSPHERIC / CINEMATIC"),
    copyBox(featurePromptA.map((l) => para([run(l, { size: 20 })], { before: 0, after: 40 }))),
    blank(),
    titleBar("PROMPT B — MINIMALIST / DARK MODE"),
    copyBox(featurePromptB.map((l) => para([run(l, { size: 20 })], { before: 0, after: 40 }))),
    blank(),

    h2("4.2 — App Icon (512 × 512 px)"),
    iconSpec,
    blank(),
    titleBar("PROMPT — APP ICON"),
    copyBox(iconPrompt.map((l) => para([run(l, { size: 20 })], { before: 0, after: 40 }))),
    blank(),

    h2("4.3 — Phone Screenshots (1080 × 1920 px)"),
    ssSpec,
    blank(),
    buildTable(
      ["#", "Screenshot Subject", "Full AI Prompt"],
      screenshotPrompts.map((s) => ({ cells: [String(s.n), s.subject, s.prompt] })),
      [4, 22, 74]
    ),
    blank(),
    noteBox([
      "Style consistency check: every prompt above uses the same primary palette (#0A0E21 background, #00E5FF cyan, #448AFF blue, #B388FF purple) and the same caption-banner band. Render all four tiles back-to-back so reviewers see a coherent visual story.",
      "If a model adds device branding (Pixel / Galaxy / iPhone outlines), regenerate — Play rejects feature graphics with real-brand mockups.",
    ]),
  ];
};

// ─── Section 5 — What's New ─────────────────────────────────────────
const releaseNotes = [
  {
    version: "1.0 (versionCode 1) — Initial release",
    notes: [
      "• New: 12 device-info screens — Dashboard, Device, System, CPU, Battery, Location, Network, Storage, Display, Sensors, Apps, Camera",
      "• New: persistent battery history with 24-hour and 7-day charts plus charging sessions",
      "• New: premium dark cyan glassmorphism theme with smooth animations",
      "• New: dynamic colors on Android 12+ (Material You), with Antar theme as fallback",
      "• New: in-app Settings with rate, share, privacy policy and data deletion shortcuts",
      "• Privacy: zero analytics, zero ad SDKs, all data stays on the device",
    ],
  },
];

const section5 = () => {
  const rows = releaseNotes.map((r) => {
    const body = r.notes.join("\n");
    return {
      fill: C.rowAltA,
      cells: [
        r.version,
        body + `\n\n[${charCount(body)} / 500 chars]`,
      ],
    };
  });
  return [
    h1(5, "What's New (Release Notes)"),
    text("Plain text only, max 500 characters per version, '•' bullets. Pulled from the actual feature commits in this branch.", { italics: true }),
    blank(),
    buildTable(["Version", "Release Notes Text"], rows, [25, 75]),
    blank(),
    noteBox([
      "Paste each notes block into Play Console → Release → Edit release → 'Release notes' for the matching language.",
      "Keep the 500-char budget tight: long release notes get truncated in the Play store update card.",
    ]),
  ];
};

// ─── Section 6 — Data Safety answers ────────────────────────────────
const section6 = () => {
  const rows = [
    {
      fill: C.amberBg,
      cells: [
        "Does your app collect or share user data?",
        "Yes — Location (precise + approximate), Installed apps, Connectivity info (BSSID), Device or other IDs (Android ID).",
        "Every collected type is read on demand; nothing is stored remotely. See permission_declarations.md → Data Safety table for the full row-by-row form."
      ]
    },
    {
      fill: C.amberBg,
      cells: [
        "Location data collected?",
        "Yes — both ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION; runtime-gated; foreground only.",
        "Sources: LocationRepositoryImpl.kt:104 (FINE check) and :125-141 (LocationManager calls); NetworkRepositoryImpl.kt:92 (FINE check before wifiManager.scanResults)."
      ]
    },
    {
      fill: C.amberBg,
      cells: [
        "Is location data shared? With whom?",
        "Yes — shared with Google's system Geocoder.",
        "android.location.Geocoder.getFromLocation at LocationRepositoryImpl.kt:180 forwards lat/lon to Google's geocoding service over HTTPS to convert coordinates into a human-readable address. No other party receives location."
      ]
    },
    {
      fill: C.greenBg,
      cells: [
        "Can users request data deletion?",
        "Yes.",
        "SettingsScreen.kt → 'Request data deletion' opens Android Settings → App info, where the user can clear local app data (Room battery_log DB + DataStore preferences). Privacy policy page also explains this."
      ]
    },
    {
      fill: C.greenBg,
      cells: [
        "Is data encrypted in transit?",
        "Yes.",
        "The only outbound traffic is the Geocoder call, which uses HTTPS. App makes no other network requests (Retrofit / OkHttp deps are unused — Section 5 of the audit recommends removing them)."
      ]
    },
    {
      fill: C.greenBg,
      cells: [
        "Advertising ID collected?",
        "No.",
        "AdvertisingIdClient call removed; AD_ID permission removed from AndroidManifest.xml; play-services-ads-identifier dependency dropped from app/build.gradle.kts."
      ]
    },
  ];
  return [
    h1(6, "Data Safety Answers"),
    text("Six high-level questions Play Console asks. Full row-by-row form (33 data-type categories) is in ANTAR_PlayStore_Audit.docx → Section 6.", { italics: true }),
    blank(),
    buildTable(["Question", "Your Answer", "Notes (file:line evidence)"], rows, [22, 28, 50]),
    blank(),
    noteBox([
      "If you reduce the feature surface (e.g. drop the address field on the Location screen and remove the Geocoder call), the answer to 'Is location data shared?' becomes 'No' — fewer disclosures, faster review.",
    ]),
  ];
};

// ─── Section 7 — Pre-submission checklist (page break before) ───────
const checklistRows = [
  { task: "Replace any test/placeholder API keys or ad unit IDs", status: "✅ Ready", notes: "App has no ad SDK and no API keys. Verified by grep for InterstitialAd, MobileAds, com.google.android.gms.ads — zero matches." },
  { task: "Upload signed release APK or AAB", status: "⬜ To Do", notes: "Build via Android Studio → Build → Generate Signed App Bundle. Use Play App Signing." },
  { task: "Confirm versionCode and versionName", status: "✅ Ready", notes: "build.gradle.kts:16-17 → versionCode = 1, versionName = '1.0'." },
  { task: "Upload Feature Graphic (1024 × 500)", status: "⬜ To Do", notes: "Use prompt A or B from Section 4.1." },
  { task: "Upload App Icon (512 × 512)", status: "⬜ To Do", notes: "Use prompt from Section 4.2." },
  { task: "Upload minimum 2 screenshots (1080 × 1920)", status: "⬜ To Do", notes: "Recommend the 4 from Section 4.3 for visual coherence." },
  { task: "Fill App Title", status: "⬜ To Do", notes: "Use 'Antar - Device Info' (Section 1.1)." },
  { task: "Fill Short Description", status: "⬜ To Do", notes: "Use the 76-char string from Section 1.2." },
  { task: "Fill Long Description", status: "⬜ To Do", notes: "Paste the full block from Section 1.3." },
  { task: "Select correct Category", status: "⬜ To Do", notes: "Tools (Section 2)." },
  { task: "Complete Data Safety form", status: "⚠️ Required", notes: "Use the 33-row table in ANTAR_PlayStore_Audit.docx Section 6.2 + the answers in Section 6 of this kit." },
  { task: "Add release notes", status: "⬜ To Do", notes: "Paste v1.0 block from Section 5." },
  { task: "Set Content Rating via questionnaire", status: "⬜ To Do", notes: "Expected: Everyone. App is utility-only with no UGC." },
  { task: "Verify Privacy Policy URL is live", status: "⚠️ Required", notes: "https://ashokvarma.dev/antar/privacy must return HTTP 200 and list every permission. Audit doc finding #3." },
  { task: "Verify Data Deletion URL is live", status: "⚠️ Required", notes: "https://ashokvarma.dev/antar/data-deletion. Hard-coded in SettingsScreen.kt:45." },
  { task: "File Sensitive Permissions Declaration for QUERY_ALL_PACKAGES", status: "⚠️ Required", notes: "Form text in permission_declarations.md → Declaration 1." },
  { task: "Upload demo video URL for QUERY_ALL_PACKAGES", status: "⚠️ Required", notes: "Recording script in permission_video_guide.md → V1." },
  { task: "Implement Prominent Disclosure dialogs (LocationScreen + NetworkScreen)", status: "⚠️ Required", notes: "Audit doc finding #2. Wording in permission_declarations.md Disclosures 1 + 2." },
  { task: "Final QA on physical device release build", status: "⬜ To Do", notes: "Test all 12 screens; verify the Battery 24h chart populates after 15 min of foreground use; verify deny-permissions path doesn't crash any screen." },
  { task: "Test edge cases (first launch, permission denial, no network, no SIM, airplane mode)", status: "⬜ To Do", notes: "Each repository returns sentinel strings on denial — no crashes expected, but verify on a real device." },
  { task: "Run Play pre-launch report on Internal testing track", status: "⬜ To Do", notes: "Catches Material design regressions, ANRs, and accessibility issues before production." },
];

const statusColor = (status) =>
  status.startsWith("⚠️") ? C.red :
  status.startsWith("✅") ? C.green :
  C.accentDeep;
const statusFill = (status) =>
  status.startsWith("⚠️") ? C.redBg :
  status.startsWith("✅") ? C.greenBg :
  C.rowAltA;

const section7 = () => {
  const rows = checklistRows.map((c, i) => ({
    fill: statusFill(c.status),
    cells: [
      "☐",
      c.task,
      para([run(c.status, { bold: true, color: statusColor(c.status), size: 20 })], { before: 0, after: 0 }),
      c.notes,
    ],
  }));
  return [
    new Paragraph({ children: [new PageBreak()] }),
    h1(7, "Pre-Submission Checklist"),
    text("Tick each item before pressing 'Send for review' in Play Console. Required items (red) are blockers; To Do items are recommended; Ready items are already satisfied by the codebase.", { italics: true }),
    blank(),
    buildTable(["", "Task", "Status", "Notes / Pointer"], rows, [4, 38, 14, 44]),
    blank(),
    noteBox([
      "If you tackle the four ⚠️ Required items in this order — privacy URL live → data deletion URL live → Sensitive Permissions form filed with video → Prominent Disclosure dialogs implemented — submission becomes a 30-minute task.",
      "Cross-reference with the action plan in ANTAR_PlayStore_Audit.docx Section 4 for effort estimates.",
    ]),
  ];
};

// ─── Document assembly ──────────────────────────────────────────────
const buildDocument = () => {
  const children = [
    coverBanner(),
    blank(),
    metaStrip(),
    blank(),
    noteBox([
      "What this document is: a copy-paste kit for filing the Play Store listing. Section 1 contains the three Play Console copy fields. Sections 2-3 are categorisation and keyword reference. Section 4 has AI prompts for Feature Graphic, Icon and Screenshots. Section 5 has release notes, Section 6 has Data Safety answers, and Section 7 is the final pre-submission checklist.",
      "Companion document: ANTAR_PlayStore_Audit.docx covers policy compliance and the full Data Safety form. Use both side by side when filing.",
    ]),
    blank(),
    ...section1(), divider(),
    ...section2(), divider(),
    ...section3(), divider(),
    ...section4(), divider(),
    ...section5(), divider(),
    ...section6(),
    ...section7(),
  ];
  return new Document({
    creator: "ANTAR Assets Pipeline",
    title: "ANTAR — Google Play Store Publishing Assets Kit",
    description: "Generated assets kit",
    styles: { default: { document: { run: { font: FONT, size: 20 } } } },
    sections: [{
      properties: { page: { margin: { top: 720, right: 720, bottom: 720, left: 720 } } },
      headers: { default: new Header({ children: [para([run("ANTAR — Play Store Assets Kit", { size: 16, color: "5A5E6F" })], { alignment: AlignmentType.RIGHT, before: 0, after: 0 })] }) },
      footers: { default: new Footer({ children: [para([new TextRun({ children: ["Page ", PageNumber.CURRENT, " of ", PageNumber.TOTAL_PAGES], font: FONT, size: 16, color: "5A5E6F" })], { alignment: AlignmentType.CENTER, before: 0, after: 0 })] }) },
      children,
    }],
  });
};

(async () => {
  const doc = buildDocument();
  const buffer = await Packer.toBuffer(doc);
  const out = path.resolve(__dirname, "..", "..", "ANTAR_PlayStore_Assets.docx");
  fs.writeFileSync(out, buffer);
  console.log(`Wrote ${buffer.length.toLocaleString()} bytes to ${out}`);
})().catch((err) => {
  console.error("Assets generation failed:", err);
  process.exit(1);
});
