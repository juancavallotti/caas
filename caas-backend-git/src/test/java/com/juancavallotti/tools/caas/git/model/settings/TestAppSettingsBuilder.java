package com.juancavallotti.tools.caas.git.model.settings;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;

public class TestAppSettingsBuilder {

    @Test
    @DisplayName("Check that application settings can be correctly read.")
    public void testParseApplicationSettings() {

        InputStream is = getClass().getResourceAsStream("/app-settings.yml");
        AppSettings settings = AppSettings.builder().fromInputStream(is).build();

        assertNotNull(settings, "Settings should have been built correctly.");
        assertEquals("documents_", settings.getDocsPrefix(), "Check one setting");
        assertEquals(1, settings.getEnvironments().size(), "We should have one dev forEnvironment");
        assertEquals(1, settings.getImports().size(), "We should have one global import");
    }
}
