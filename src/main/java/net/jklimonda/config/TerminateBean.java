package net.jklimonda.config;

import javax.annotation.PreDestroy;

public class TerminateBean {

    @PreDestroy
    public void oDestroy() throws Exception {
        System.out.println("Closing aplication context...");
    }
}
