package com.arkannsk.elval

object ElvalConstants {

    val EVL_DIRECTIVES = listOf("validate", "decor", "rewrite")

    val VALIDATE_PARAMS = mapOf(
        "required" to "Required field",
        "min:" to "Min value/length",
        "max:" to "Max value/length",
        "len:" to "Exact length",
        "gt:" to "Greater than",
        "lt:" to "Less than",
        "pattern:" to "Regex pattern",
        "email" to "Valid email",
        "url" to "Valid URL",
        "uuid" to "Valid UUID"
    )

    val OA_KEYS = mapOf(
        "title" to "Field title",
        "description" to "Field description",
        "format" to "Data format",
        "example" to "Example value",
        "in" to "Location",
        "minimum" to "Min number",
        "maximum" to "Max number",
        "type" to "Data type",
        "items" to "Array items"
    )
}
