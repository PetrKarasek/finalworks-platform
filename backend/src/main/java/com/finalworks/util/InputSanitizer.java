package com.finalworks.util;

import org.springframework.stereotype.Component;

/**
 * Utility class for sanitizing user input to prevent XSS attacks
 */
@Component
public class InputSanitizer {

    /**
     * Sanitizes a string by removing potentially dangerous characters
     * This is a basic implementation - for production, consider using OWASP Java HTML Sanitizer
     */
    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove HTML tags and encode special characters
        return input
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;")
                .trim();
    }

    /**
     * Sanitizes input but allows basic formatting (for descriptions, comments)
     */
    public String sanitizeWithBasicFormatting(String input) {
        if (input == null) {
            return null;
        }
        
        // Allow newlines and basic formatting, but remove script tags
        return input
                .replace("<script", "")
                .replace("</script>", "")
                .replace("<iframe", "")
                .replace("</iframe>", "")
                .replace("javascript:", "")
                .replace("onerror=", "")
                .replace("onclick=", "")
                .trim();
    }
}
