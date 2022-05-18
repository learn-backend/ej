package com.learn.ej.chapter3.item14.example;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class PhoneNumber implements Comparable<PhoneNumber> {

    private final int areaCode;
    private final int prefix;
    private final int lineNum;

    @Override
    public int compareTo(PhoneNumber pn) {
        int result = Integer.compare(areaCode, pn.areaCode);
        if (result == 0) {
            result = Integer.compare(prefix, pn.prefix);
            if (result == 0) {
                result = Integer.compare(lineNum, pn.lineNum);
            }
        }

        return result;
    }
}
