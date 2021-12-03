package com.company.deck;

import java.util.Comparator;

public class SortbyColorAndValue implements Comparator<Card>
{
    // Used for sorting in descending order
    public int compare(Card a, Card b) {
        if(a.getColor() == b.getColor())
            return b.getPoints() - a.getPoints();
        else
            return b.getMarriagePoints() - a.getMarriagePoints();
    }
}
