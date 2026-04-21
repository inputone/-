# Tailwind CSS Customization

Config file structure, custom utilities, plugins, and theme extensions.

## @theme Directive

Modern approach to customize Tailwind using CSS:

```css
@import "tailwindcss";

@theme {
  /* Custom colors */
  --color-brand-50: oklch(0.97 0.02 264);
  --color-brand-500: oklch(0.55 0.22 264);
  --color-brand-900: oklch(0.25 0.15 264);

  /* Custom fonts */
  --font-display: "Satoshi", "Inter", sans-serif;
  --font-body: "Inter", system-ui, sans-serif;

  /* Custom spacing */
  --spacing-18: calc(var(--spacing) * 18);
  --spacing-navbar: 4.5rem;

  /* Custom breakpoints */
  --breakpoint-3xl: 120rem;
  --breakpoint-tablet: 48rem;

  /* Custom shadows */
  --shadow-glow: 0 0 20px rgba(139, 92, 246, 0.3);

  /* Custom radius */
  --radius-large: 1.5rem;
}
```

**Usage:**
```html
<div class="bg-brand-500 font-display shadow-glow rounded-large">
  Custom themed element
</div>
```

## Color Customization

### Custom Color Palette

```css
@theme {
  /* Full color scale */
  --color-primary-50: oklch(0.98 0.02 250);
  --color-primary-100: oklch(0.95 0.05 250);
  --color-primary-200: oklch(0.90 0.10 250);
  --color-primary-300: oklch(0.85 0.15 250);
  --color-primary-400: oklch(0.75 0.18 250);
  --color-primary-500: oklch(0.65 0.22 250);
  --color-primary-600: oklch(0.55 0.22 250);
  --color-primary-700: oklch(0.45 0.20 250);
  --color-primary-800: oklch(0.35 0.18 250);
  --color-primary-900: oklch(0.25 0.15 250);
  --color-primary-950: oklch(0.15 0.10 250);
}
```

### Semantic Colors

```css
--color-success: oklch(0.65 0.18 145);
--color-warning: oklch(0.75 0.15 85);
--color-error: oklch(0.60 0.22 25);
--color-info: oklch(0.65 0.18 240);
```

## Typography Customization

### Custom Fonts

```css
@theme {
  --font-sans: "Inter", system-ui, sans-serif;
  --font-serif: "Merriweather", Georgia, serif;
  --font-mono: "JetBrains Mono", Consolas, monospace;
  --font-display: "Playfair Display", serif;
}
```

### Font Size Scale

```ts
theme: {
  extend: {
    fontSize: {
      'xs': ['0.75rem', { lineHeight: '1rem' }],
      'sm': ['0.875rem', { lineHeight: '1.25rem' }],
      'base': ['1rem', { lineHeight: '1.5rem' }],
    }
  }
}
```

## Spacing Customization

```css
@theme {
  --spacing-18: 4.5rem;      /* 72px */
  --spacing-88: 22rem;       /* 352px */
  --spacing-128: 32rem;      /* 512px */
}
```

## Breakpoint Customization

```css
@theme {
  --breakpoint-sm: 640px;
  --breakpoint-md: 768px;
  --breakpoint-lg: 1024px;
  --breakpoint-xl: 1280px;
  --breakpoint-2xl: 1536px;

  /* Custom breakpoints */
  --breakpoint-3xl: 1920px;
  --breakpoint-tablet: 768px;
}
```

## Shadow Customization

```css
@theme {
  --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  --shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
  --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  --shadow-xl: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);
  --shadow-glow: 0 0 20px rgba(139, 92, 246, 0.3);
  --shadow-inner: inset 0 2px 4px 0 rgb(0 0 0 / 0.05);
}
```

## Border Radius Customization

```css
@theme {
  --radius-sm: 0.25rem;
  --radius-md: 0.5rem;
  --radius-lg: 0.75rem;
  --radius-xl: 1rem;
  --radius-2xl: 1.5rem;
  --radius-full: 9999px;
}
```

## Animation Customization

```css
@theme {
  --animate-spin: spin 1s linear infinite;
  --animate-ping: ping 1s cubic-bezier(0, 0, 0.2, 1) infinite;
  --animate-pulse: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
  --animate-bounce: bounce 1s infinite;

  @keyframes spin {
    to { transform: rotate(360deg); }
  }
}
```

## Layer Organization

```css
@layer base {
  /* Base styles */
  body {
    @apply bg-background text-foreground;
  }
}

@layer components {
  /* Reusable component classes */
  .btn {
    @apply px-4 py-2 rounded-lg font-medium;
  }
}

@layer utilities {
  /* Utility classes */
  .text-balance {
    text-wrap: balance;
  }
}
```

## Using @apply

Extract repeated styles into component classes:

```css
@layer components {
  .card {
    @apply bg-white rounded-xl shadow-lg p-6 border border-gray-100;
  }

  .btn-primary {
    @apply bg-blue-500 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-600 transition-colors;
  }
}
```

## Custom Variants

```css
@layer utilities {
  .hover-scale {
    @apply transition-transform duration-200;
  }

  .hover-scale:hover {
    transform: scale(1.05);
  }

  .first-line-bold::first-line {
    @apply font-bold;
  }
}
```

## Custom Utilities

```css
@layer utilities {
  .center-absolute {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  .text-shadow {
    text-shadow: 0 2px 4px rgb(0 0 0 / 0.1);
  }
}
```