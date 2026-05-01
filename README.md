# ElVal Support Plugin

IntelliJ Platform plugin for syntax highlighting of [ElVal](https://github.com/arkannsk/elval) annotations in Go comments.

## Features

- Syntax highlighting for `@evl:validate`, `@evl:decor`, and `@evl:rewrite` directives.
- Support for OpenAPI annotations (`@oa:title`, `@oa:description`, etc.).
- Customizable color scheme via **Settings | Editor | Color Scheme | ElVal Annotations**.

## Installation

1. Build the plugin: `./gradlew buildPlugin`
2. Install from disk: `build/distributions/ElValSupport-*.zip`

## Supported Annotations

### ElVal
- `@evl:validate required`
- `@evl:validate min:10 max:100`
- `@evl:validate pattern:email`
- `@evl:decor uuid-gen`

### OpenAPI
- `@oa:title "User Name"`
- `@oa:description Some text`
- `@oa:format email`
