package com.juancavallotti.tools.caas.service;

/**
 * Run the solution as an app that wraps a service.
 * This is useful for setting the working directory and other good stuff.
 */
public class ServiceWrapper {
    public static void main(String[] args) throws Exception {

        //identify where this class is located.
        //this would normally be ina Lib Directory.
        //starting from there, we want to run whatever with the same JVM where this app is being run.

        String distHome = ServiceWrapper.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String javaHome = System.getProperty("java.home");

        System.out.println(distHome);
        System.out.println(javaHome);


        //JAVA PATH :


    }
}
