package org.tairobea;


import javax.swing.*;

public class App {
    public static void main(String[] args) {

        int tileSize = 32;
        int columns = 16;
        int rows = 16;
        int boardHeight = tileSize * rows;
        int boardWidth = tileSize * columns;


        JFrame jFrame = new JFrame("invasores del espacio");

        jFrame.setSize(boardWidth, boardHeight);
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SpaceInvaders spaceInvaders = new SpaceInvaders();
        jFrame.add(spaceInvaders);
        jFrame.pack();
        spaceInvaders.requestFocus();
        jFrame.setVisible(true);
    }
}