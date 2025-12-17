package com.vehicleman.domain.use_case.record_ai

/**
 * Compatibility layer ώστε το ViewModel να μπορεί να χρησιμοποιεί parsed.cleanedText
 * χωρίς να αλλάζουμε το βασικό parsing model.
 */
val ParsedSmartTitle.cleanedText: String
    get() = normalized.trim()
