package com.helpflow.tickets.infrastructure.adapter.out.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BCryptPasswordEncoderAdapterTest {

    @Test
    void testEncodeAndMatches() {
        BCryptPasswordEncoderAdapter adapter = new BCryptPasswordEncoderAdapter();
        String hash = adapter.encode("admin123");
        System.out.println("### BCRYPT_HASH=" + hash);
        assertTrue(adapter.matches("admin123", hash));
    }
}
