package dev.jamesswafford.chess4j.io;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(name="TestLogAppender", category="Core", elementType="appender", printObject=true)
public final class TestLogAppender extends AbstractAppender {

    private static final Logger LOGGER = LogManager.getLogger(TestLogAppender.class);

    private final List<String> logs = new ArrayList<>();

    protected TestLogAppender(String name) {
        super(name, null, PatternLayout.createDefaultLayout(), true, Property.EMPTY_ARRAY);
    }

    @Override
    public void append(LogEvent event) {
        logs.add(event.getMessage().getFormattedMessage());
    }

    public void clearMessages () {
        logs.clear();
    }

    public List<String> getMessages() {
        return new ArrayList<>(logs);
    }

    public List<String> getNonDebugMessages() {
        return logs.stream()
                .filter(msg -> !msg.startsWith("# "))
                .collect(Collectors.toList());
    }

    public void printMessages() {
        logs.forEach(LOGGER::info);
    }

    @PluginFactory
    public static TestLogAppender createAppender(@PluginAttribute("name") String name) {

        if (name == null) {
            LOGGER.error("No name provided for TestLog4j2Appender");
            return null;
        }

        return new TestLogAppender(name);
    }
}