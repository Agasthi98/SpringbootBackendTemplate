package com.example.backendtemplate.util;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class MaskingPatternLayout extends PatternLayout {

    private final List<String> maskPatterns = new ArrayList<>();
    private Pattern multilinePattern;

    public void addMaskPattern(String maskPattern) {
        maskPatterns.add(maskPattern);
        multilinePattern = Pattern.compile(String.join("|", maskPatterns), Pattern.MULTILINE);
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        return maskMessage(super.doLayout(event));
    }

    public String maskMessage(String message) {
        if (multilinePattern == null) {
            return message;
        }
        StringBuilder sb = new StringBuilder(message);
        Matcher matcher = multilinePattern.matcher(sb);
        while (matcher.find()) {
            IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
                if (matcher.group(group) != null) {
                    int startIndex = matcher.start(group);
                    int bound = matcher.end(group);
                    //avoid full mask if card number is present
                    boolean cardNumberPresent = matcher.group().contains("number")
                            || matcher.group().contains("Destination_card_number");
                    if (cardNumberPresent) {
                        startIndex += 6;
                        bound -= 4;
                    }
                    for (int i = startIndex; i < bound; i++) {
                        sb.setCharAt(i, '*');
                    }
                }
            });
        }
        return sb.toString();
    }
}