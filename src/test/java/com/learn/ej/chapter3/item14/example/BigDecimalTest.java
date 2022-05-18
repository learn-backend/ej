package com.learn.ej.chapter3.item14.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class BigDecimalTest {


    @Test
    void bigDecimalTest() {
        BigDecimal bigDecimal1 = new BigDecimal("1.0");
        BigDecimal bigDecimal2 = new BigDecimal("1.00");

        assertThat(bigDecimal1)
            .isEqualByComparingTo(bigDecimal2)
            .isNotEqualTo(bigDecimal2);
    }

    @Test
    void hashSetTest() {
        BigDecimal bigDecimal1 = new BigDecimal("1.0");
        BigDecimal bigDecimal2 = new BigDecimal("1.00");

        Set<BigDecimal> hashSet = new HashSet<>();
        hashSet.add(bigDecimal1);
        hashSet.add(bigDecimal2);

        assertThat(hashSet).hasSize(2);
    }


    @Test
    void treeSetTest() {
        BigDecimal bigDecimal1 = new BigDecimal("1.0");
        BigDecimal bigDecimal2 = new BigDecimal("1.00");

        Set<BigDecimal> hashSet = new TreeSet<>();
        hashSet.add(bigDecimal1);
        hashSet.add(bigDecimal2);

        assertThat(hashSet).hasSize(1);
    }
}
