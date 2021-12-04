package com.company.player;

import com.company.deck.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.company.Main.isANumber;

public class User extends Player{

    public User(String username){
        super(username);
    }

    @Override
    public Card makeMove(char marriage, List<Card> cardTrash, Card cardOnTable) {
        while(true) {
            String which, message;
            int cardToPull;
            boolean correctNum;
            Scanner scanner = new Scanner(System.in);
            do {
                System.out.println("\nChoose card (write num 1-" + this.hand.size() + "):");
                which = scanner.next();
                message = "Write only numbers and without spaces!";
                correctNum = isANumber(which, message, 1, this.hand.size());
            } while (!correctNum);
            cardToPull = Integer.parseInt(which);
            cardToPull--;
            Card card = this.hand.get(cardToPull);
            if(cardOnTable == null || this.inColor(cardOnTable) == -1 || card.getColor() == cardOnTable.getColor()){
                this.hand.remove(cardToPull);
                return card;
            }else
                System.out.println("Choose card in color to first player's card!");
        }
    }

    @Override
    public void askForBid(int actualBid) {
        String bid, message;
        Scanner scanner = new Scanner(System.in);
        boolean correctNum;
        for(Card card: this.hand)
            System.out.print(card.getValue()+card.getColor()+" ");
        do {
            System.out.println("\nWrite bid (It has to be higher than actual and multiple of 10) or 0 if you want to pass");
            bid = scanner.next();
            message = "Write only numbers and without spaces!";
            correctNum = isBidGood(bid, message, actualBid);
        } while (!correctNum);
    }

    public boolean isBidGood(String input, String message, int min){
        try{
            int x = Integer.parseInt(input);
            if(x==0){
                this.isInBidding = false;
                return true;
            }
            if(x%10==0 && x>=min+10 && x<=this.sumCards()) {
                this.bid = x;
                return true;
            }
        }catch(NumberFormatException ex){
            System.out.print(message);
            return false;
        }
        return false;
    }

    @Override
    public int sumCards(){
        int sum = 120;
        char[] colors = {'♥', '♦', '♣', '♠'};
        for(int i=0 ; i<4 ; i++){
            if(this.ifMarriageInColor(colors[i], this.hand))
                sum = sum + (5-i)*20;
        }
        return sum;
    }

    @Override
    public List<Card> give2Cards(){
        Scanner scanner = new Scanner(System.in);
        List<Card> toReturn = new ArrayList<>();
        String which;
        boolean correctNumber;
        for(int i=0 ; i<2 ; i++){
            System.out.println("Choose two cards to give to other players (1-"+this.hand.size()+"):");
            do {
                for(Card card: this.hand)
                    System.out.print(card.getValue()+card.getColor()+" ");
                System.out.print("\n"+(i+1) + ". card: ");
                which = scanner.next();
                correctNumber = isANumber(which, "Write number (1-" + this.hand.size() + "):", 1, this.hand.size());
            }while(!correctNumber);
            toReturn.add(this.hand.get(Integer.parseInt(which)-1));
            this.hand.remove(Integer.parseInt(which)-1);
        }

        return toReturn;
    }

    @Override
    public int changeBid(){
        String bid, message;
        Scanner scanner = new Scanner(System.in);
        boolean correctNum;
        do {
            System.out.println();
            this.showHand();
            System.out.println("Write bid (It has to be equal or higher than actual and multiple of 10) or 0 if you want to pass:");
            System.out.println("If 0 passes left and write 0, then u go actual bid");
            System.out.println("Passes left: "+(1-this.surrenders));
            bid = scanner.next();
            message = "Write only numbers and without spaces!";
            correctNum = isGood(bid, message);
        } while (!correctNum);
        return Integer.parseInt(bid);
    }

    public boolean isGood(String input, String message){
        try{
            int x = Integer.parseInt(input);
            if(x==0) {
                return true;
            }
            if(x%10==0 && x>=this.bid && x<=this.sumCards()) {
                this.bid = x;
                return true;
            }
        }catch(NumberFormatException ex){
            System.out.print(message);
            return false;
        }
        return false;
    }
}
