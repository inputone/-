# Tailwind CSS Utility Reference

Core utility classes for layout, spacing, typography, colors, borders, and shadows.

## Layout Utilities

### Display

```html
<div class="block">Block</div>
<div class="inline-block">Inline Block</div>
<div class="inline">Inline</div>
<div class="flex">Flexbox</div>
<div class="inline-flex">Inline Flex</div>
<div class="grid">Grid</div>
<div class="inline-grid">Inline Grid</div>
<div class="hidden">Hidden</div>
```

### Flexbox

**Container:**
```html
<div class="flex flex-row">Row (default)</div>
<div class="flex flex-col">Column</div>
<div class="flex flex-row-reverse">Reverse row</div>
<div class="flex flex-col-reverse">Reverse column</div>
```

**Justify (main axis):**
```html
<div class="flex justify-start">Start</div>
<div class="flex justify-center">Center</div>
<div class="flex justify-end">End</div>
<div class="flex justify-between">Space between</div>
<div class="flex justify-around">Space around</div>
<div class="flex justify-evenly">Space evenly</div>
```

**Align (cross axis):**
```html
<div class="flex items-start">Start</div>
<div class="flex items-center">Center</div>
<div class="flex items-end">End</div>
<div class="flex items-baseline">Baseline</div>
<div class="flex items-stretch">Stretch</div>
```

**Gap:**
```html
<div class="flex gap-4">All sides</div>
<div class="flex gap-x-6 gap-y-2">X and Y</div>
```

**Wrap:**
```html
<div class="flex flex-wrap">Wrap</div>
<div class="flex flex-nowrap">No wrap</div>
```

### Grid

**Columns:**
```html
<div class="grid grid-cols-1">1 column</div>
<div class="grid grid-cols-2">2 columns</div>
<div class="grid grid-cols-3">3 columns</div>
<div class="grid grid-cols-4">4 columns</div>
<div class="grid grid-cols-12">12 columns</div>
<div class="grid grid-cols-[1fr_500px_2fr]">Custom</div>
```

**Span:**
```html
<div class="col-span-2">Span 2 columns</div>
<div class="row-span-3">Span 3 rows</div>
```

**Gap:**
```html
<div class="grid gap-4">All sides</div>
<div class="grid gap-x-8 gap-y-4">X and Y</div>
```

### Positioning

```html
<div class="static">Static (default)</div>
<div class="relative">Relative</div>
<div class="absolute">Absolute</div>
<div class="fixed">Fixed</div>
<div class="sticky">Sticky</div>

<!-- Position values -->
<div class="absolute top-0 right-0">Top right</div>
<div class="absolute inset-0">All sides 0</div>
<div class="absolute inset-x-4">Left/right 4</div>
<div class="absolute inset-y-8">Top/bottom 8</div>
```

## Spacing Utilities

### Padding

```html
<div class="p-4">All sides</div>
<div class="px-6">Left and right</div>
<div class="py-3">Top and bottom</div>
<div class="pt-8">Top</div>
<div class="pr-4">Right</div>
<div class="pb-2">Bottom</div>
<div class="pl-6">Left</div>
```

### Margin

```html
<div class="m-4">All sides</div>
<div class="mx-auto">Center horizontally</div>
<div class="my-6">Top and bottom</div>
<div class="mt-8">Top</div>
<div class="-mt-4">Negative top</div>
<div class="ml-auto">Push to right</div>
```

### Spacing Scale

| Class | Value |
|-------|-------|
| `0` | 0px |
| `px` | 1px |
| `0.5` | 0.125rem (2px) |
| `1` | 0.25rem (4px) |
| `2` | 0.5rem (8px) |
| `3` | 0.75rem (12px) |
| `4` | 1rem (16px) |
| `6` | 1.5rem (24px) |
| `8` | 2rem (32px) |
| `12` | 3rem (48px) |
| `16` | 4rem (64px) |
| `24` | 6rem (96px) |

## Typography

### Font Size

| Class | Size |
|-------|------|
| `text-xs` | 12px |
| `text-sm` | 14px |
| `text-base` | 16px |
| `text-lg` | 18px |
| `text-xl` | 20px |
| `text-2xl` | 24px |
| `text-3xl` | 30px |
| `text-4xl` | 36px |
| `text-5xl` | 48px |

### Font Weight

| Class | Weight |
|-------|--------|
| `font-thin` | 100 |
| `font-light` | 300 |
| `font-normal` | 400 |
| `font-medium` | 500 |
| `font-semibold` | 600 |
| `font-bold` | 700 |
| `font-black` | 900 |

### Text Alignment

```html
<p class="text-left">Left</p>
<p class="text-center">Center</p>
<p class="text-right">Right</p>
<p class="text-justify">Justify</p>
```

### Line Height

| Class | Value |
|-------|-------|
| `leading-none` | 1 |
| `leading-tight` | 1.25 |
| `leading-snug` | 1.375 |
| `leading-normal` | 1.5 |
| `leading-relaxed` | 1.625 |
| `leading-loose` | 2 |

### Letter Spacing

| Class | Value |
|-------|-------|
| `tracking-tighter` | -0.05em |
| `tracking-tight` | -0.025em |
| `tracking-normal` | 0 |
| `tracking-wide` | 0.025em |
| `tracking-wider` | 0.05em |
| `tracking-widest` | 0.1em |

## Colors

### Background Colors

```html
<div class="bg-white">White</div>
<div class="bg-black">Black</div>
<div class="bg-gray-100">Gray 100</div>
<div class="bg-blue-500">Blue 500</div>
<div class="bg-red-600">Red 600</div>
```

### Text Colors

```html
<p class="text-gray-900">Dark gray</p>
<p class="text-gray-500">Medium gray</p>
<p class="text-white">White</p>
<p class="text-blue-600">Blue</p>
```

### Border Colors

```html
<div class="border border-gray-200">Light border</div>
<div class="border-2 border-blue-500">Blue border</div>
<div class="border-t border-red-500">Top red border</div>
```

### Opacity

```html
<div class="bg-black/50">50% opacity black</div>
<div class="bg-black/75">75% opacity black</div>
<div class="text-white/30">30% opacity white text</div>
```

## Borders

### Border Width

```html
<div class="border">1px border</div>
<div class="border-2">2px border</div>
<div class="border-4">4px border</div>
<div class="border-8">8px border</div>
<div class="border-t-2">Top border only</div>
```

### Border Radius

| Class | Value |
|-------|-------|
| `rounded-none` | 0 |
| `rounded-sm` | 0.125rem |
| `rounded` | 0.25rem |
| `rounded-md` | 0.375rem |
| `rounded-lg` | 0.5rem |
| `rounded-xl` | 0.75rem |
| `rounded-2xl` | 1rem |
| `rounded-full` | 9999px (circle/pill) |

## Shadows

| Class | Value |
|-------|-------|
| `shadow-sm` | 0 1px 2px rgb(0 0 0 / 0.05) |
| `shadow` | 0 1px 3px rgb(0 0 0 / 0.1) |
| `shadow-md` | 0 4px 6px rgb(0 0 0 / 0.1) |
| `shadow-lg` | 0 10px 15px rgb(0 0 0 / 0.1) |
| `shadow-xl` | 0 20px 25px rgb(0 0 0 / 0.1) |
| `shadow-2xl` | 0 25px 50px rgb(0 0 0 / 0.25) |
| `shadow-none` | none |

## Effects

### Opacity

```html
<div class="opacity-0">0%</div>
<div class="opacity-50">50%</div>
<div class="opacity-75">75%</div>
<div class="opacity-100">100%</div>
```

### Blur

```html
<div class="blur-sm">Small blur</div>
<div class="blur">Normal blur</div>
<div class="blur-lg">Large blur</div>
<div class="blur-xl">Extra large blur</div>
<div class="blur-2xl">2xl blur</div>
<div class="blur-full">Full blur</div>
```

### Transitions

```html
<div class="transition-none">No transition</div>
<div class="transition">All properties</div>
<div class="transition-all">All properties</div>
<div class="transition-colors">Color only</div>
<div class="transition-opacity">Opacity only</div>
<div class="transition-transform">Transform only</div>
```

### Duration

```html
<div class="duration-75">75ms</div>
<div class="duration-100">100ms</div>
<div class="duration-150">150ms</div>
<div class="duration-200">200ms</div>
<div class="duration-300">300ms</div>
<div class="duration-500">500ms</div>
<div class="duration-700">700ms</div>
<div class="duration-1000">1000ms</div>
```