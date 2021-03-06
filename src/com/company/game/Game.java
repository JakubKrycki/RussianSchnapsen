package com.company.game;

import com.company.deck.Card;
import com.company.deck.Deck;
import com.company.deck.SortbyColorAndValue;
import com.company.player.Bot;
import com.company.player.Player;
import com.company.player.User;

import java.util.ArrayList;
import java.util.List;

public class Game {

    List<Card> stock = new ArrayList<>();
    List<Card> cardTrash = new ArrayList<>();
    List<Player> players = new ArrayList<>();
    char marriage = ' ';
    Deck deck = new Deck();
    int gameBid = 100;


/*
    public Game(Player player){
        this.player = player;
    }
*/

    public Game(Player user){
        this.players.add(new Bot("firstbot"));
        this.players.add(new Bot("secondBot"));
        //this.players.add(new Bot("thirdBot"));
        this.players.add(user);
    }

    public void dealTheCards(){
        for(int i=0 ; i<3 ; i++)
            this.deck.hit(this.stock);
        for(int i=0 ; i<7 ; i++){
            for(int j=0 ; j<3 ; j++)
                this.deck.hit(this.players.get(j).getHand());
            //this.deck.hit(user.getHand());
        }
    }

    public void rotatePlayers(){
        Player first = this.players.get(0);
        this.players.remove(0);
        this.players.add(first);
    }

    public int bidding(){
        int surrenders=0;
        int bestPlayer = 0;
        System.out.println(this.players.get(0).getUsername()+": 100");
        this.players.get(0).setBid(100);
        for(int i=1 ; surrenders<2 ; i = (i+1)%3){
            Player player = this.players.get(i);
            if(player.isInBidding()) {
                player.askForBid(this.gameBid);
                System.out.print(player.getUsername()+": ");
                if(player.isInBidding()) {
                    this.gameBid = player.getBid();
                    System.out.print(this.gameBid+"\n");
                    bestPlayer = i;
                }
                else {
                    surrenders++;
                    System.out.print("pass\n");
                }
            }
        }
        return bestPlayer;
    }

    public void showStockAndPlayers(){
        if(this.gameBid>100) {
            System.out.print("Stock: ");
            for (Card card : this.stock)
                System.out.print(card.getValue() + card.getColor() + " ");
            System.out.print("\n");
        }
        this.showHand();
        System.out.print("\n");
    }

    public boolean handleStock(int startingPlayer){
        this.showStockAndPlayers();
        //give three cards to first player
        for(int i=0 ; i<3 ; i++) {
            this.players.get(startingPlayer).getHand().add(this.stock.get(0));
            this.stock.remove(0);
        }
        this.players.get(startingPlayer).getHand().sort(new SortbyColorAndValue());
        //player choose one card for each player
        List<Card> toGive = this.players.get(startingPlayer).give2Cards();

        this.players.get((startingPlayer+1)%3).getHand().add(toGive.get(0));
        this.players.get((startingPlayer+2)%3).getHand().add(toGive.get(1));

        this.sortHands();
        //player sum his points once again
        int newBid = this.players.get(startingPlayer).changeBid();
        if(newBid == 0 && this.players.get(startingPlayer).getSurrenders()==0)
            return false;
        if(newBid != 0)
            this.gameBid = newBid;
        return true;
    }

    public void bomb(int startingPlayer){
        System.out.println(this.players.get(startingPlayer).getUsername()+" resignates");
        this.players.get(startingPlayer).setSurrenders(1);
        this.players.get(startingPlayer).setInBidding(false);
        this.players.get((startingPlayer+1)%3).setWonPoints(60);
        this.players.get((startingPlayer+2)%3).setWonPoints(60);
    }

    public void showTable(Card card1, Card card2, Card card3){
        System.out.print("Table: ");
        if(card1!=null)
            System.out.print(card1.getValue()+card1.getColor()+" ");
        if(card2!=null)
            System.out.print(card2.getValue()+card2.getColor()+" ");
        if(card3!=null)
            System.out.print(card3.getValue()+card3.getColor()+"\n");
        System.out.println();
    }

    public void play(int startingPlayer){
         if(!this.handleStock(startingPlayer)) {
             this.bomb(startingPlayer);
             return;
         }
         char trump;
         System.out.println("----------------");
         System.out.println("Bid: "+this.gameBid);
         while(this.cardTrash.size()<24 && this.players.get(startingPlayer).getHand().size()>0){
            System.out.println("----------------");
            System.out.println("startingPlayer: "+this.players.get(startingPlayer).getUsername());
            this.showHand();
            trump = this.marriage;
            if(trump != ' ')
                System.out.println("Trump: "+trump);
            if(this.players.get(startingPlayer) instanceof User)
                this.showTable(null, null, null);
            Card card1 = this.players.get(startingPlayer).makeMove(this.marriage, this.cardTrash, null);
            if(this.players.get(startingPlayer).checkIfMarriage(card1)) {
                this.marriage = card1.getColor();
                System.out.println("Trump: "+this.marriage);
            }
            if(this.players.get((startingPlayer+1)%3) instanceof User)
                this.showTable(card1, null, null);
            Card card2 = this.players.get((startingPlayer+1)%3).makeMove(this.marriage, this.cardTrash, card1);
            if(this.players.get((startingPlayer+2)%3) instanceof User)
                this.showTable(card1, card2, null);
            Card card3 = this.players.get((startingPlayer+2)%3).makeMove(this.marriage, this.cardTrash, card1);
            this.showTable(card1, card2, card3);
            int startingPlayerTemp = (winningCard(card1, card2))? startingPlayer : (startingPlayer+1)%3;
            if(startingPlayerTemp == startingPlayer)
                startingPlayer = (winningCard(card1, card3))? startingPlayer : (startingPlayer+2)%3;
            else
                startingPlayer = (winningCard(card2, card3))? (startingPlayer+1)%3 : (startingPlayer+2)%3;
            this.players.get(startingPlayer).won(card1.getPoints(), card2.getPoints(), card3.getPoints());
            this.showPoints();
            System.out.println();
            this.cardTrash.add(card1);
            this.cardTrash.add(card2);
            this.cardTrash.add(card3);
        }

    }

    public boolean result(){
        for(Player player: this.players) {
            player.result();
            if(player.getGamePoints()>=1000) {
                return false;
            }
        }
        System.out.println();
        return true;
    }

    public void endGame(){
        System.out.println("Game Over");
    }

    public boolean winningCard(Card card1, Card card2){
        if(card1.getColor() != card2.getColor()){
            return card2.getColor() != this.marriage;
        }else
            return card1.getPoints() > card2.getPoints();
    }

    public boolean dealAgain(){
        for(Player player: this.players) {
            if(player.fourNines())
                return true;
        }
        return false;
    }

    public void round(){
        do {
            do {
                this.deck.newRoundDeck();
                this.dealTheCards();
            } while (this.dealAgain());
            this.gameBid = 100;
            this.sortHands();
            this.play(this.bidding());
            this.result();
            this.showResult();
            this.endRound();
        }while(this.result());
        this.endGame();
    }

    public void endRound(){
        for(Player player: this.players){
            player.setBid(0);
            player.getHand().clear();
            player.setInBidding(true);
            player.setWonPoints(0);
        }
        this.rotatePlayers();
        this.cardTrash.clear();
        this.marriage = ' ';
        System.out.println("-----------------------------");
    }

    public void sortHands(){
        for(Player player: this.players)
            player.sortingCards();
    }

    public void showHands(){
        for(Player player: this.players){
            player.showHand();
        }
    }

    public void showHand(){
        for(Player player: this.players){
            if(player instanceof User)
                player.showHand();
        }
    }

    public void showResult(){
        System.out.println("Result: ");
        for(Player player: this.players){
            System.out.println(player.getUsername() + ": "+player.getGamePoints());
        }
    }

    public void showPoints(){
        for(Player player: this.players){
            System.out.println(player.getUsername() + ": "+player.getWonPoints());
        }
    }

    public List<Card> getCardTrash() {
        return cardTrash;
    }

    public void setCardTrash(List<Card> cardTrash) {
        this.cardTrash = cardTrash;
    }

    public char getMarriage() {
        return marriage;
    }

    public void setMarriage(char marriage) {
        this.marriage = marriage;
    }
}
