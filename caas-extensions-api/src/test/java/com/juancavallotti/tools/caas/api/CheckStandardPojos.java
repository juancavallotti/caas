package com.juancavallotti.tools.caas.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CheckStandardPojos {


    @Test
    @DisplayName("Check equals is only based on getters/setters.")
    public void checkEquals() {

        //since we're comparing coordinates, this should work.
        DefaultConfigurationElement element = new DefaultConfigurationElement();
        element.setApplication("a");
        element.setVersion("v");
        element.setEnvironment("e");

        DefaultConfigCoordinate coordinate = new DefaultConfigCoordinate();
        coordinate.setEnvironment("e");
        coordinate.setApplication("a");
        coordinate.setVersion("v");

        assertTrue( coordinate.equals(element) ,"Equals should work as expected");

    }

}
