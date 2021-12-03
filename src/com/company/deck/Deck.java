package com.company.deck;

import com.company.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    List<Card> cards;

    public Deck(){
        this.cards = createCardList();//full and shuffle deck
    }

    public List<Card> createCardList(){
        char[] colors = {'♥', '♦', '♣', '♠'};
        List<Card> deck = new ArrayList<>();
        for (char color : colors) {
            deck.add(new Card(0, "9", color, 5));//9
            deck.add(new Card(2, "J", color, 4));//J
            deck.add(new Card(3, "Q", color, 3));//Q
            deck.add(new Card(4, "K", color, 2));//K
            deck.add(new Card(10, "10", color, 1));//10
            deck.add(new Card(11, "A", color, 0));//A
        }
        Collections.shuffle(deck);
        return deck;
    }

    public void hit(List<Card> place){
        Card card = this.cards.get(0);
        place.add(card);
        this.cards.remove(0);
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void newRoundDeck(){
        this.cards = this.createCardList();
    }

}
