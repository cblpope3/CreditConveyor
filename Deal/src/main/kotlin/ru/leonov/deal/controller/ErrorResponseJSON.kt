package ru.leonov.deal.controller

/**
 * Container that used to create error explanation json.
 */
data class ErrorResponseJSON(
    val problemFieldName: String? = null,
    val rejectedValue: String? = null,
    val problemMessage: String?
)