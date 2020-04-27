package com.jamesswafford.chess4j.io;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.rules.ExternalResource;

import java.io.CharArrayWriter;

public class LogAppenderResource extends ExternalResource {

    private static final String APPENDER_NAME = "log4jRuleAppender";

    private static final String PATTERN = "%-5level %msg";

    private Logger logger;
    private Appender appender;
    private final CharArrayWriter outContent = new CharArrayWriter();

    public LogAppenderResource(org.apache.logging.log4j.Logger logger) {
        this.logger = (org.apache.logging.log4j.core.Logger)logger;
    }

    @Override
    protected void before() {
        StringLayout layout = PatternLayout.newBuilder().withPattern(PATTERN).build();
        appender = WriterAppender.newBuilder()
                .setTarget(outContent)
                .setLayout(layout)
                .setName(APPENDER_NAME).build();
        appender.start();
        logger.addAppender(appender);
    }

    @Override
    protected void after() {
        logger.removeAppender(appender);
    }

    public String getOutput() {
        return outContent.toString();
    }

}
