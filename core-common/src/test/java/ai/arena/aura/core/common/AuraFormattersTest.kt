package ai.arena.aura.core.common

import org.junit.Assert.assertEquals
import org.junit.Test

class AuraFormattersTest {
    @Test fun durationFormatsMinutesAndHours() {
        assertEquals("3:05", AuraFormatters.duration(185_000))
        assertEquals("1:01:01", AuraFormatters.duration(3_661_000))
    }
}
