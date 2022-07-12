package com.learn.ej.chapter7.item45;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

public class Item45 {

    private static List<Card> newDeckUsingForLoop() {
        List<Card> result = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                result.add(new Card(suit, rank));
            }
        }
        return result;
    }


    private static List<Card> newDeckUsingStream() {
        return Stream.of(Suit.values())
            .flatMap(suit ->
                Stream.of(Rank.values())
                    .map(rank -> new Card(suit, rank)))
            .collect(Collectors.toUnmodifiableList());
    }


    @RequiredArgsConstructor
    private static class Card {

        private final Suit suit;
        private final Rank rank;
    }

    private enum Suit {

    }

    private enum Rank {

    }

}
