package com.company.player;

import com.company.deck.Card;
import com.company.deck.SortbyColorAndValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Player {

    protected String username;
    protected List<Card> hand;
    protected boolean isInBidding = true;
    protected int bid = 0;
    protected int wonPoints = 0;
    protected int gamePoints = 0;
    protected int surrenders = 0;

    public abstract Card makeMove(char marriage, List<Card> cardTrash, Card cardOnTable);
    public abstract void askForBid(int actualBid);
    public abstract int sumCards();
    public abstract List<Card> give2Cards();
    public abstract int changeBid();

    public Player(){
        this.username = "";
        this.hand = new ArrayList<>();
    }

    public Player(String username){
        this.username = username;
        this.hand = new ArrayList<>();
    }

    public boolean fourNines(){
        int nines = 0;
        for(Card card: this.hand){
            if(card.getValue().equals("9"))
                nines++;
        }
        return nines == 4;
    }

    public void won(int card1, int card2, int card3){
        this.wonPoints = this.wonPoints + card1 + card2 + card3;
    }

    public void result(){
        if(this.isInBidding){
            if(this.bid<=this.wonPoints)
                this.gamePoints+=this.bid;
            else
                this.gamePoints-=this.bid;
        }else{
            int points = this.wonPoints, rest;
            rest = points%10;
            points /= 10;
            if(rest>=5)
                points+=1;
            points*=10;
            if(this.gamePoints+points<900)
                this.gamePoints += points;
            else
                this.gamePoints = 900;
        }
    }

    public int inColor(Card cardOnTable){
        int index=-1;
        for(int i=0 ; i<this.hand.size() ; i++){
            Card card = this.hand.get(i);
            if(card.getColor() == cardOnTable.getColor()){
                if(index == -1)
                    index = i;
                else if(card.getPoints()>cardOnTable.getPoints()){
                    if(card.getPoints()>this.hand.get(index).getPoints())
                        index = i;
                }else if(this.hand.get(index).getPoints()<cardOnTable.getPoints() && card.getPoints()<this.hand.get(index).getPoints()){
                    index = i;
                }
            }
        }
        return index;
    }

    public void showHand(){
        System.out.print(this.username + ": ");
        for(Card card: this.hand){
            System.out.print(card.getValue()+card.getColor()+" ");
        }
        System.out.print("\n");
    }

    public boolean checkIfMarriage(Card card){
        if(card.getValue().equals("K")){
            for(Card actual: this.hand) {
                if (actual.getValue().equals("Q") && actual.getColor() == card.getColor()) {
                    this.wonPoints+=card.getMarriagePoints();
                    return true;
                }
            }
        }else if(card.getValue().equals("Q")){
            for(Card actual: this.hand){
                if(actual.getValue().equals("K") && actual.getColor()==card.getColor()){
                    this.wonPoints+=card.getMarriagePoints();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean ifMarriageInColor(char color, List<Card> hand){
        for(int i=0 ; i<hand.size()-1 ; i++){
            Card card1 = hand.get(i);
            Card card2 = hand.get(i+1);
            if(card1.getColor() == color && card1.getColor()==card2.getColor() && card1.getValue().equals("K") && card2.getValue().equals("Q"))
                return true;
        }
        return false;
    }

    public int findNewMarriage(){//cards are sorted, that means Q is after K, so we have to check if actual card is K and next is Q and if they're in the same color
        for(int i=0 ; i<this.hand.size()-1 ; i++){
            Card card1 = this.hand.get(i);
            Card card2 = this.hand.get(i+1);
            if(card1.getColor()==card2.getColor() && card1.getValue().equals("K") && card2.getValue().equals("Q"))
                return card1.getMarriagePoints();
        }
        return 0;
    }

    public List<Card> getHand(){
        return this.hand;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public int getWonPoints() {
        return wonPoints;
    }

    public void setWonPoints(int wonPoints) {
        this.wonPoints = wonPoints;
    }

    public int getGamePoints() {
        return gamePoints;
    }

    public void setGamePoints(int gamePoints) {
        this.gamePoints = gamePoints;
    }

    public boolean isInBidding() {
        return isInBidding;
    }

    public void setInBidding(boolean inBidding) {
        isInBidding = inBidding;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getSurrenders() {
        return surrenders;
    }

    public void setSurrenders(int surrenders) {
        this.surrenders = surrenders;
    }

    public void sortingCards(){
        this.hand.sort(new SortbyColorAndValue());
    }
}
