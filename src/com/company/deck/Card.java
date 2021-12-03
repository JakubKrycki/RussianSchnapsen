package com.company.deck;

import java.util.Comparator;

public class Card {

    private int points;
    private String value;
    private char color;
    private int marriagePoints;
    private int numOfHigherCards;


    public Card(int points, String value, char color, int numOfHigherCards){
        this.setColor(color);
        this.setPoints(points);
        this.setValue(value);
        this.setNumOfHigherCards(numOfHigherCards);
        switch (color) {
            case '♥' -> this.setMarriagePoints(100);
            case '♦' -> this.setMarriagePoints(80);
            case '♣' -> this.setMarriagePoints(60);
            case '♠' -> this.setMarriagePoints(40);
        }
    }

    public int getMarriagePoints() {
        return marriagePoints;
    }

    public void setMarriagePoints(int marriagePoints) {
        this.marriagePoints = marriagePoints;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public char getColor() {
        return color;
    }

    public int getNumOfHigherCards(){
        return this.numOfHigherCards;
    }

    public void setNumOfHigherCards(int numOfHigherCards){
        this.numOfHigherCards = numOfHigherCards;
    }

    public void setColor(char color) {
        this.color = color;
    }

}
