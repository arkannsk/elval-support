# ElVal Helper for GoLand

**ElVal Helper** is an IntelliJ Platform plugin designed to enhance the development experience when using the [ElVal](https://github.com/arkannsk/elval) validation and documentation library in Go.

It provides intelligent **code completion** and rich **syntax highlighting** for `@evl` (validation/decoration) and `@oa` (OpenAPI) annotations directly within Go source code comments.

## ✨ Features

### 🚀 Intelligent Code Completion
Never guess parameter names again. The plugin understands the context of your annotations and suggests valid parameters with helpful descriptions.

*   **Directives:** Supports `@evl:validate`, `@evl:decor`, `@evl:rewrite`, and `@oa:*`.
*   **Smart Filtering:** Suggests only relevant parameters. Once a parameter is used in a line, it won't clutter the suggestion list unless explicitly typed.
*   **Context-Aware:**
    *   For `@oa:in`, it suggests locations (`path`, `query`, `header`, `cookie`).
    *   For `@evl:decor`, it suggests generators (`uuid-gen`, `time-now`).
    *   For validation rules, it suggests types like `min:`, `max:`, `pattern:`, `enum:`, etc.


### 🎨 Syntax Highlighting
Make your annotations stand out from regular comments. The plugin parses comment text and applies semantic coloring:

*   **Keywords:** `@evl:validate`, `@oa:title` are highlighted as keywords/metadata.
*   **Parameters:** Parameter names like `required`, `min:`, `header` are highlighted distinctly.
*   **Values:** Values such as `"User Name"`, `email`, or `10` are highlighted as strings or numbers.

<video src="screenshots/demo_hl.webm" autoplay loop muted playsinline width="400"></video>

## 📦 Installation

### Manual Installation
1.  Download the latest `.zip` release from the [Releases page](https://github.com/arkannsk/elval-support/releases).
2.  In GoLand, go to **Settings/Preferences** → **Plugins**.
3.  Click the ⚙️ icon → **Install Plugin from Disk...**.
4.  Select the downloaded ZIP file and restart the IDE.

## 🛠 Usage

Simply start typing annotations in comments above your struct fields. The plugin will automatically activate.

### ElVal Validation & Decoration

```go
type User struct {
    // @evl:validate required min:3 max:50
    // @evl:decor uuid-gen
    ID string `json:"id"`

    // @evl:validate pattern:email
    Email string `json:"email"`

    // @evl:validate gte:18
    Age int `json:"age"`
    
    // @evl:decor ctx-get:user_id
    OwnerID int64 `json:"owner_id"`
}
```

### OpenAPI Documentation

```go
type CreateUserRequest struct {
    // @oa:in header X-Request-ID
    // @oa:description Unique ID for tracing requests
    RequestID string `json:"request_id"`

    // @oa:in query limit
    // @oa:minimum 1
    // @oa:maximum 100
    Limit int `json:"limit"`
    
    // @oa:rewrite.type string
    Metadata map[string]string `json:"metadata"`
}
```

## ⚙️ Customization

You can customize the colors used for highlighting to match your theme:

1.  Go to **Settings/Preferences** → **Editor** → **Color Scheme**.
2.  Navigate to **ElVal Annotations** (or search for "ElVal").
3.  Adjust the colors for:
    *   **ElVal Keyword** (e.g., `@evl:validate`)
    *   **ElVal Parameter** (e.g., `required`, `min:`)
    *   **ElVal Value** (e.g., `email`, `10`)
    *   **OpenAPI Annotation** (e.g., `@oa:title`)


## 🔗 Links

*   [ElVal Library Repository](https://github.com/arkannsk/elval)
*   [ElVal Documentation](https://github.com/arkannsk/elval/blob/main/README.md)
*   [Report an Issue](https://github.com/arkannsk/elval-helper/issues)
