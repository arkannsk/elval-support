package com.arkannsk.elval

object ElvalConstants {

    val EVL_DIRECTIVES = listOf("validate", "decor")

    val VALIDATE_PARAMS = mapOf(
        // Required & Optional
        "required" to "Field is required",
        "optional" to "Field is optional (skips empty)",
        "not-zero" to "Value must not be zero/empty",
        "enum:" to "Comma-separated allowed values",

        // String & Slice Lengths / Numeric Ranges
        "min:" to "Minimum length or value",
        "max:" to "Maximum length or value",
        "len:" to "Exact length",

        // Comparisons
        "gt:" to "Greater than",
        "lt:" to "Less than",
        "gte:" to "Greater than or equal",
        "lte:" to "Less than or equal",
        "eq:" to "Equal to",
        "neq:" to "Not equal to",

        // Patterns & Formats
        "pattern:" to "Regex pattern or predefined (email, phone)",
        "email" to "Must be valid email",
        "url" to "Must be valid URL",
        "uuid" to "Must be valid UUID",
        "phone" to "Must be valid phone number", // Добавил, так как есть в примере pattern:phone

        // Enums
        "enum:" to "List of allowed values (comma-separated)",

        // Dates & Durations
        "after:" to "Date after specified",
        "before:" to "Date before specified"
    )

    // --- DECOR PARAMS ---
    val DECOR_PARAMS = mapOf(
        "ctx-get:" to "Get value from context key",
        "httpctx-get:" to "Get value from HTTP header",
        "env-get:" to "Get value from environment variable",
        "time-now" to "Set current timestamp",
        "uuid-gen" to "Generate UUID v4"
    )

    // --- REWRITE PARAMS (если понадобятся) ---
    val REWRITE_PARAMS = mapOf(
        "type:" to "Rewrite type for OpenAPI docs (string, integer, etc.)"
    )

    val OA_KEYS = mapOf(
        "in" to "Parameter location (path, query, header, cookie)",
        "title" to "Schema title",
        "description" to "Schema description",
        "example" to "Example value",
        "format" to "OpenAPI format (email, uuid, date-time, etc.)",
        "default" to "Default value",
        "enum" to "Allowed values",
        "minimum" to "Min value (numeric)",
        "maximum" to "Max value (numeric)",
        "minLength" to "Min length (string)",
        "maxLength" to "Max length (string)",
        "pattern" to "Regex pattern",
        "discriminator" to "Discriminator field name",
        "rewrite.type" to "Override type for OpenAPI docs"
    )

    // Возможные значения для @oa:in
    val OA_IN_VALUES = listOf("path", "query", "header", "cookie")
}
