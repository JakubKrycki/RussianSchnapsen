package com.company;

import com.company.game.Game;
import com.company.player.User;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Scanner scanner = new Scanner(System.in);
        String username;
        System.out.println("Your username: ");
        username = scanner.next();
        User user = new User(username);
        Game game = new Game(user);

        game.round();
    }
    public static boolean isANumber(String input, String message, int min, int max){
        try{
            int x = Integer.parseInt(input);
            return (x>=min && x<=max);
        }catch(NumberFormatException ex){
            System.out.println(message);
            return false;
        }
    }
}
