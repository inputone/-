# shadcn/ui Accessibility Patterns

ARIA patterns, keyboard navigation, screen reader support, and accessible component usage.

## Foundation: Radix UI Primitives

shadcn/ui built on Radix UI primitives - unstyled, accessible components following WAI-ARIA design patterns.

Benefits:
- Keyboard navigation built-in
- Screen reader announcements
- Focus management
- ARIA attributes automatically applied
- Tested against accessibility standards

## Keyboard Navigation

### Focus Management

**Focus visible states:**
```tsx
<Button className="focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2">
  Accessible Button
</Button>
```

**Skip to content:**
```tsx
<a href="#main-content" className="sr-only focus:not-sr-only focus:absolute focus:top-4 focus:left-4 focus:z-50 focus:px-4 focus:py-2">
  Skip to content
</a>

<main id="main-content">
  {/* Content */}
</main>
```

### Dialog/Modal Navigation

Dialogs trap focus automatically via Radix Dialog primitive:

```tsx
import { Dialog, DialogContent, DialogTrigger } from "@/components/ui/dialog"

<Dialog>
  <DialogTrigger>Open</DialogTrigger>
  <DialogContent>
    {/* Focus trapped here */}
    <input />  {/* Auto-focused */}
    <Button>Action</Button>
    {/* Esc to close, Tab to navigate */}
  </DialogContent>
</Dialog>
```

Features:
- Focus trapped within dialog
- Esc key closes
- Tab cycles through focusable elements
- Focus returns to trigger on close

### Dropdown/Menu Navigation

```tsx
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"

<DropdownMenu>
  <DropdownMenuTrigger>Open</DropdownMenuTrigger>
  <DropdownMenuContent>
    <DropdownMenuItem>Profile</DropdownMenuItem>
    <DropdownMenuItem>Settings</DropdownMenuItem>
    <DropdownMenuItem>Logout</DropdownMenuItem>
  </DropdownMenuContent>
</DropdownMenu>
```

Keyboard shortcuts:
- `Space/Enter`: Open menu
- `Arrow Up/Down`: Navigate items
- `Esc`: Close menu
- `Tab`: Close and move focus

## Screen Reader Support

### Semantic HTML

Use proper HTML elements:

```tsx
// Good: Semantic HTML
<button>Click me</button>
<nav><a href="/">Home</a></nav>

// Avoid: Div soup
<div onClick={handler}>Click me</div>
```

### ARIA Labels

**Label interactive elements:**
```tsx
<Button aria-label="Close dialog">
  <X className="h-4 w-4" />
</Button>

<Input aria-label="Email address" type="email" />
```

**Describe elements:**
```tsx
<Button aria-describedby="delete-description">
  Delete Account
</Button>
<p id="delete-description" className="sr-only">
  This action permanently deletes your account and cannot be undone
</p>
```

### Screen Reader Only Text

Use `sr-only` class for screen reader only content:

```tsx
<Button>
  <Trash className="h-4 w-4" />
  <span className="sr-only">Delete item</span>
</Button>
```

### Live Regions

Announce dynamic content:

```tsx
<div aria-live="polite" aria-atomic="true">
  {message}
</div>

// For urgent updates
<div aria-live="assertive">
  {error}
</div>
```

## Form Accessibility

### Labels and Descriptions

**Always label inputs:**
```tsx
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"

<div>
  <Label htmlFor="email">Email</Label>
  <Input id="email" type="email" />
</div>
```

**Error messages:**
```tsx
<FormField>
  <FormItem>
    <FormLabel>Email</FormLabel>
    <FormControl>
      <Input {...field} />
    </FormControl>
    <FormMessage />  {/* Announces error to screen readers */}
  </FormItem>
</FormField>
```

### Required Fields

```tsx
<FormField>
  <FormItem>
    <FormLabel>
      Email <span className="text-destructive">*</span>
    </FormLabel>
    <FormControl>
      <Input {...field} aria-required="true" />
    </FormControl>
    <FormDescription>Required for notifications</FormDescription>
    <FormMessage />
  </FormItem>
</FormField>
```

## Color Contrast

Ensure sufficient color contrast (WCAG AA minimum):

| Text Type | Minimum Ratio |
|-----------|-------------|
| Normal text | 4.5:1 |
| Large text (18px+) | 3:1 |
| UI components | 3:1 |

```tsx
// Good: Sufficient contrast
<div className="text-gray-900 bg-white">  {/* ~21:1 */}

// Avoid: Poor contrast
<div className="text-gray-400 bg-white">  {/* ~2.5:1 */}
```

## Focus Indicators

Never remove focus indicators completely:

```tsx
// Bad: No visible focus
<button className="outline-none">Click</button>

// Good: Custom focus style
<button className="focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2">Click</button>
```

## Reduced Motion

Respect user preferences:

```tsx
<div className="transition-all duration-300 motion-reduce:transition-none">
  Animated content
</div>
```