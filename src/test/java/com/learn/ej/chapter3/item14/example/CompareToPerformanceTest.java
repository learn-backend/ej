package com.learn.ej.chapter3.item14.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

class CompareToPerformanceTest {

    private List<PhoneNumber> phoneNumbers = new ArrayList<>();

    @BeforeEach
    void init() {
        Random rnd = new Random();
        for (int i = 0; i < 1_000_000; i++) {
            phoneNumbers.add(PhoneNumber.builder()
                .areaCode(rnd.nextInt(100))
                .prefix(rnd.nextInt(100))
                .lineNum(rnd.nextInt(9_999_999) + 10_000_000)
                .build());
        }
    }

    @Test
    void compareToPerformanceTest() {
        ArrayList<PhoneNumber> dumpPhoneNumbers = new ArrayList<>(phoneNumbers);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("compareTo #1");
        Collections.sort(phoneNumbers);
        stopWatch.stop();

        stopWatch.start("Comparator #2");
        dumpPhoneNumbers.sort(
            Comparator.comparingInt(PhoneNumber::getAreaCode)
                .thenComparingInt(PhoneNumber::getPrefix)
                .thenComparingInt(PhoneNumber::getLineNum));
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());

    }
}

