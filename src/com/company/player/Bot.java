package com.company.player;

import com.company.deck.Card;

import java.util.ArrayList;
import java.util.List;

public class Bot extends Player{

    public Bot(String username){
        super(username);
    }

    @Override
    public Card makeMove(char marriage, List<Card> cardTrash, Card cardOnTable){
        int cardToPull = -1;
        if(cardOnTable == null){//then it is the first player
            if(marriage == ' ' || this.leftInMarriage(cardTrash, marriage) == 0){//there is no marriage
                cardToPull = this.findBestCard(cardTrash);//card which other players can't take
                if(cardToPull == -1)
                    cardToPull = this.findAMarriage();
                if(cardToPull == -1)
                    cardToPull = this.findWorstToLoose();//card which other player will take
            }else {//there is some marriage cards left
                cardToPull = this.findAMarriage();
                if (cardToPull == -1)
                    cardToPull = this.findBestCard(cardTrash);
                if (cardToPull == -1)
                    cardToPull = this.findBestInMarriage(marriage);
            }
            if(cardToPull == -1)
                cardToPull = this.findWorstToLoose();//card which other player will take
        }else{//then there is some cards on table
            cardToPull = inColor(cardOnTable);//czy w kolorze jest cos lepszego
            if(cardToPull == -1 && marriage != ' ')//czy moge zabrac czyms z atutu
                cardToPull = this.findBestInMarriage(marriage);
            if(cardToPull == -1)//strata, czyli jak najgorsza karta
                cardToPull = this.findWorstToLoose();
        }
        //the card is chosen
        Card card = this.hand.get(cardToPull);
        this.hand.remove(cardToPull);
        return card;
    }

    @Override
    public void askForBid(int actualBid) {

        if(this.sumCards()>=actualBid+10)
            this.bid = actualBid+10;
        else
            this.isInBidding = false;
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

    public int findBestCard(List<Card> used){
        for(int j=0 ; j<this.hand.size() ; j++){
            Card card = this.hand.get(j);
            if(this.isTheHighestInColor(used, card))
                return j;
        }
        return -1;
    }

    public int findAMarriage() {
        int marriage = this.findNewMarriage();
        if (marriage != 0) {
            return this.takeMarriage(marriage);
        }
        return -1;
    }

    public int takeMarriage(int marriage){
        for(int i=0 ; i<this.hand.size() ; i++){
            if(this.hand.get(i).getMarriagePoints()==marriage && this.hand.get(i).getValue().equals("Q")) {
                return i;
            }
        }
        return -1;
    }

    @Override
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

    public int findBestInMarriage(char marriage){
        int index=-1;
        for(int i=0 ; i<this.hand.size() ; i++){
            Card card = this.hand.get(i);
            if(card.getColor() == marriage){
                if(index == -1 || card.getPoints()>this.hand.get(index).getPoints())
                    index = i;
            }
        }
        return index;
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

    public int leftInMarriage(List<Card> used, char marriage){
        int marriageCards = 6;
        for(Card card: used){
            if(card.getColor() == marriage)
                marriageCards--;
        }
        return marriageCards;
    }

    public boolean isTheHighestInColor(List<Card> used, Card card) {
        int i = card.getNumOfHigherCards();
        for (Card actual : used) {
            if (actual.getColor() == card.getColor() && actual.getPoints() > card.getPoints()) {
                i--;
            }
        }
        return i==0;
    }

    public int findWorstToLoose(){
        int worst = 0;
        for(int i=0 ; i<this.hand.size() ; i++){
            if(this.hand.get(worst).getPoints()>this.hand.get(i).getPoints())
                worst = i;
        }
        return worst;
    }

    @Override
    public int sumCards(){
        int sum=0;
        char[] colors = {'♥', '♦', '♣', '♠'};
        for(int i=0 ; i<4 ; i++)//highest in colors
            sum+=this.sumColor(colors[i]);
        //marriages
        sum+=this.findNewMarriage();
        return sum;
    }

    public int sumColor(char color){
        int[] cards = {0, 0, 0, 0, 0, 0};
        int sum=0;
        for(Card card: this.hand){
            if(card.getColor()==color){
                cards[card.getNumOfHigherCards()]=1;
                sum++;
            }
        }
        if(sum==6 || (sum==5 && cards[0]==1) || (sum==4 && cards[0]==1 && cards[1]==1) || (sum==3 && cards[0]==1 && cards[1]==1 && cards[2]==1))//all cards in color to take
            return 30;
        if(cards[0] == 0)
            return 0;
        if(cards[1]==0)
            return 11;
        if(cards[2]==0)
            return 23;
        return 0;
    }

    @Override
    public List<Card> give2Cards(){
        char[] colors = {'♥', '♦', '♣', '♠'};
        List<Card> toReturn = new ArrayList<>();
        for(int i=0 ; i<4 && toReturn.size()<2; i++)
            if(this.sumColor(colors[i])==0 && !this.checkifColorHasMarriage(colors[i])){
                this.worstInColor(colors[i], toReturn);
            }
        if(toReturn.size()==2)
            return toReturn;
        else
            while(toReturn.size()<2){
                toReturn.add(this.hand.get(this.findWorstToLoose()));
                this.hand.remove(this.findWorstToLoose());
            }
            return toReturn;
    }

    public void worstInColor(char color, List<Card> toReturn){

        for(int i=0 ; i<this.hand.size() ; i++){
            if(this.hand.get(i).getColor()==color){
                toReturn.add(this.hand.get(i));
                this.hand.remove(i);
                i--;
            }
        }

        while(toReturn.size()>2){
            int highest = 0;
            for(int i=1 ; i<toReturn.size() ; i++){
                if(toReturn.get(highest).getPoints()<toReturn.get(i).getPoints()){
                    highest = i;
                }
            }
            this.hand.add(toReturn.get(highest));
            toReturn.remove(highest);
        }
    }

    public boolean checkifColorHasMarriage(char color){
        int cardInMarriage=0;
        for(Card card: this.hand){
            if(card.getColor() == color && (card.getValue().equals("K") || card.getValue().equals("Q"))){
                if(cardInMarriage == 1)
                    return true;
                else
                    cardInMarriage++;
            }
        }
        return false;
    }

    //TODO jesli nie ugra swojego bid'a jednak to rezygnacja jesli jeszcze nie dal, mozna max 1

    @Override
    public int changeBid(){

        if(this.sumCards()<this.bid)
            return 0;
        while(this.sumCards()>=this.bid+10){
            this.bid+=10;
        }
        return this.bid;
    }
}
