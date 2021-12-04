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
        int cardToPull;
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
        else if(actualBid==100 && this.ifMarriageInColor('♥', this.hand))
                this.bid = actualBid+10;
        else
            this.isInBidding = false;
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
        int sum;
        int toLoose = 0;
        char[] colors = {'♥', '♦', '♣', '♠'};
        for(int i=0 ; i<4 ; i++)//highest in colors
            toLoose+=this.sumColor(colors[i], this.hand);

        switch(toLoose){
            case 0 -> sum = 120;
            case 1 -> sum = 99;
            case 2 -> sum = 70;
            case 3 -> sum = 60;
            case 4 -> sum = 40;
            case 5 -> sum = 30;
            case 6 -> sum = 20;
            case 7 -> sum = 10;
            default -> sum = 0;
        }
        //marriages
        sum+=this.sumMarriages();
        return sum;
    }

    public int sumColor(char color, List<Card> hand){
        List<Card> colorCard = new ArrayList<>();
        for(Card card: hand)
            if(card.getColor()==color)
                colorCard.add(card);
        int cardToSub = colorCard.size();
        if(colorCard.size()==6 || (colorCard.size()==5 && colorCard.get(0).getValue().equals("A")) || (colorCard.size()==4 && colorCard.get(0).getValue().equals("A") && colorCard.get(1).getValue().equals("10"))){//all/5 with A/4 with A,10/3 with
            return 0;
        }
        if(colorCard.size()==0)
            return 0;
        if(colorCard.get(0).getValue().equals("A")){
            if(colorCard.size()>=5 || colorCard.size()==1)
                return 0;
            else if(colorCard.size()>=2 && colorCard.get(0).getValue().equals("A") && colorCard.get(1).getValue().equals("10"))
                return 0;
            cardToSub--;
        }
        return cardToSub;
    }

    public int sumMarriages(){
        short marriagesToLoose = 0, temp;
        int sum = 0;
        char[] colors = {'♥', '♦', '♣', '♠'};
        for(int i=0 ; i<4 ; i++){
            if(this.ifMarriageInColor(colors[i], this.hand)){
                temp = this.checkColorCards(colors[i]);
                if(temp==0 || (temp==1 && marriagesToLoose==0))
                    sum = sum + (5-i)*20;
                if(temp==1)
                    marriagesToLoose=1;
            }
        }
        return sum;
    }

    public short checkColorCards(char color){
        List<Card> colorCard = new ArrayList<>();
        for(Card card: this.hand){
            if(card.getColor()==color) {
                colorCard.add(card);
            }
        }
        //now we've got list of cards in this color
        if(colorCard.size()==5 || (colorCard.get(0).getValue().equals("A") && colorCard.get(1).getValue().equals("10")))
            return 0;
        else
            return 1;
    }

    @Override
    public List<Card> give2Cards(){
        List<Card> toReturn = new ArrayList<>(this.hand);
        this.hand.clear();
        List<Card> aces = new ArrayList<>(this.addAces(toReturn));
        if(aces.size()>0) {
            this.addTensToAces(toReturn);
            this.addMarriageToAces(toReturn, aces);
            if(toReturn.size()==2)
                return toReturn;
            else if(toReturn.size()<2){
                this.findAcesAndTensWithoutMarriage(toReturn);
            }else{
                this.findRest(toReturn);
            }
        }else{//hand is clear
            //search for marriage 100 or 80
            if(toReturn.size()>2)
                this.worstInColor('♥', toReturn);
            if(toReturn.size()>2)
                this.worstInColor('♦', toReturn);
            if(toReturn.size()>2)
                this.worstInColor('♣', toReturn);
            if(toReturn.size()>2)
                this.worstInColor('♠', toReturn);
        }
        return toReturn;
    }

    public List<Card> addAces(List<Card> toReturn){
        for(int i=0 ; i<toReturn.size() ; i++){
            Card card = toReturn.get(i);
            if(card.getPoints() == 11) {
                this.hand.add(card);
                toReturn.remove(card);
                i--;
            }
        }
        this.sortingCards();
        return this.hand;
    }

    public void addTensToAces(List<Card> toReturn){
        List<Card> aces = new ArrayList<>(this.hand);
        for(int i=0 ; i<toReturn.size() ; i++){//searching tens
            Card card = toReturn.get(i);
            if(card.getPoints() == 10){//ten found
                for(Card ace: aces){//search ace in the same color if found then would be added to hand
                    if(ace.getValue().equals("A") && card.getColor() == ace.getColor()){
                        this.hand.add(card);
                        toReturn.remove(card);
                        i--;
                    }
                }
            }
        }
        this.sortingCards();
    }

    public void addMarriageToAces(List<Card> toReturn, List<Card> aces){
        for(Card ace: aces){
            if(this.ifMarriageInColor(ace.getColor(), toReturn)){
                for(int i=0 ; i<toReturn.size() ; i++){
                    Card card = toReturn.get(i);
                    if(card.getColor() == ace.getColor() && (card.getValue().equals("K") || card.getValue().equals("Q"))){
                        this.hand.add(card);
                        toReturn.remove(card);
                        i--;
                    }
                }
            }
        }
        this.sortingCards();
    }

    public void findAcesAndTensWithoutMarriage(List<Card> toReturn){
        for(int i=0 ; i<this.hand.size() && toReturn.size()<2; i++){
            Card card = this.hand.get(i);
            if(card.getValue().equals("10") && !this.ifMarriageInColor(card.getColor(), this.hand)){
                toReturn.add(card);
                this.hand.remove(i);
                if(toReturn.size()<2){
                    toReturn.add(this.hand.get(i-1));
                    this.hand.remove(i-1);//if ten was on i then ace will be on i-1, ten is always in hand only if ace is too
                    i--;
                }
                i--;
            }
        }
        this.sortingCards();
    }

    public void findRest(List<Card> toReturn){
        char[] colors = {'♥', '♦', '♣', '♠'};
        for(int i=0 ; i<4 && toReturn.size()>2; i++){//find card which are in the same colors as our marriages
            if(this.ifMarriageInColor(colors[i], this.hand)){
                this.worstInColor(colors[i], toReturn);
            }
        }
        this.sortingCards();
        for(int i=0 ; i<this.hand.size()-2 && toReturn.size()>2; i++){//we've got A and 10 in the same color on hand and we're looking for K (there won't be Q), cause it's easy win card
            if(this.hand.get(i).getValue().equals("A") && this.hand.get(i+1).getValue().equals("10") && this.hand.get(i).getColor() == this.hand.get(i+1).getColor()){
                for(int j=0 ; j<toReturn.size() ; j++){
                    Card card = toReturn.get(j);
                    if(card.getColor() == this.hand.get(i).getColor() && toReturn.size()>2){
                        this.hand.add(card);
                        toReturn.remove(card);
                        j--;
                    }
                }
            }
        }
        this.sortingCards();
        for(int i=0 ; i<this.hand.size() ; i++){//finding anything with the same color as our aces
            Card ace = this.hand.get(i);
            if(ace.getValue().equals("A") && toReturn.size()>2){
                for(int j=0 ; j<toReturn.size() ; j++){
                    Card card = toReturn.get(j);
                    if(ace.getColor() == card.getColor() && toReturn.size()>2){
                        this.hand.add(card);
                        toReturn.remove(card);
                        j--;
                    }
                }
            }
        }
        this.sortingCards();
        for(int i=0 ; i<4 && toReturn.size()>2; i++)//find worst cards just to fill the hand, and try to make these cards in the same color, to for example take someone ace with our trump
            this.worstInColor(colors[i], toReturn);

    }

    public void worstInColor(char color, List<Card> toReturn){
        for(int i=0 ; i<toReturn.size() && toReturn.size() > 2; i++){
            if(toReturn.get(i).getColor() == color){
                this.hand.add(toReturn.get(i));
                toReturn.remove(i);
                i--;
            }
        }
        this.sortingCards();
    }

    @Override
    public int changeBid(){
        int sum = this.sumCards();
        if(sum<this.bid)
            return 0;
        while(sum>=this.bid+10){
            this.bid+=10;
        }
        return this.bid;
    }
}
