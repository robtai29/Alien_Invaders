package org.tairobea;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import javax.swing.*;


public class SpaceInvaders  extends JPanel implements ActionListener, KeyListener {

    static  class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true;
        boolean used = false;

        Block(int x, int y, int width, int height, boolean alive, boolean used, Image img){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.alive = alive;
            this.used = used;
            this.img = img;
        }
    }
    int tileSize = 32;
    int columns = 16;
    int rows = 16;
    int boardHeight = tileSize * rows;
    int boardWidth = tileSize * columns;

    Image shipImg;
    Image alienWhiteImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;

    ArrayList<Image>  alienImgList;

    int shipWidth = tileSize * 2;
    int shipHeight = tileSize;
    int shipX = tileSize * columns / 2 -  tileSize;
    int shipY = boardHeight - tileSize * 2;
    int shipVelocityX = tileSize;

    Block ship;

    Timer gameLoop;
    ArrayList<Block> alienList;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienColumns = 2;
    int alienCount = 0;
    int alienVelocityX = 1;

    ArrayList<Block> bulletList;
    int bulletWidth = tileSize/8;
    int bulletHeight = tileSize /2;
    int bulletVelocity = -10;
    int score = 0;

    boolean gameOver = false;


    SpaceInvaders(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        alienImgList = new ArrayList<>();
        shipImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/ship.png"))).getImage();
        alienWhiteImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/alien-white.png"))).getImage();
        alienCyanImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/alien-cyan.png"))).getImage();
        alienMagentaImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/alien-magenta.png"))).getImage();
        alienYellowImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/alien-yellow.png"))).getImage();

        alienImgList.add(alienWhiteImg);
        alienImgList.add(alienCyanImg);
        alienImgList.add(alienMagentaImg);
        alienImgList.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth,  shipHeight, true, true, shipImg);
        alienList = new ArrayList<>();
        bulletList = new ArrayList<>();

        gameLoop = new Timer(1000/60, this);
        createAliens();
        gameLoop.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        for (int i = 0; i < alienList.size(); i++) {
            Block alien = alienList.get(i);
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        g.setColor(Color.white);
        for (int i = 0; i < bulletList.size(); i++){
            Block bullet = bulletList.get(i);
            if (!bullet.used){
                g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }

        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 34));
        if (gameOver){
            g.drawString("Game Over Mijo: " + String.valueOf((int) score), 10, 35);
        }else{
            g.drawString(String.valueOf((int) score), 10, 34);
        }

    }

    public boolean detectCollision(Block a, Block b) {
        return  a.x < b.x + b.width &&  //a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x &&  //a's top right corner passes b's top left corner
                a.y < b.y + b.height && //a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y;   //a's bottom left corner passes b's top left corner
    }


    public void move() {
        //alien
        for (int i = 0; i < alienList.size(); i++) {
            Block alien = alienList.get(i);
            if (alien.alive) {
                alien.x += alienVelocityX;

                //if alien touches the borders
                if (alien.x + alien.width >= boardWidth || alien.x <= 0) {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX*2;

                    //move all aliens up by one row
                    for (int j = 0; j < alienList.size(); j++) {
                        alienList.get(j).y += alienHeight;
                    }
                }

                if (alien.y >= ship.y) {
                    gameOver = true;
                }
            }
        }

        //bullets
        for (int i = 0; i < bulletList.size(); i++) {
            Block bullet = bulletList.get(i);
            bullet.y += bulletVelocity;

            //bullet collision with aliens
            for (int j = 0; j < alienList.size(); j++) {
                Block alien = alienList.get(j);
                if (!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score += 100;
                }
            }
        }

        //clear bullets
        while (bulletList.size() > 0 && (bulletList.get(0).used || bulletList.get(0).y < 0)) {
            bulletList.remove(0); //removes the first element of the array
        }

        //next level
        if (alienCount == 0) {
            //increase the number of aliens in columns and rows by 1
            score += alienColumns * alienRows * 100; //bonus points :)
            alienColumns = Math.min(alienColumns + 1, columns/2 -2); //cap at 16/2 -2 = 6
            alienRows = Math.min(alienRows + 1, rows-6);  //cap at 16-6 = 10
            alienList.clear();
            bulletList.clear();
            createAliens();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) { //any key to restart
            ship.x = shipX;
            bulletList.clear();
            alienList.clear();
            gameOver = false;
            score = 0;
            alienColumns = 3;
            alienRows = 2;
            alienVelocityX = 1;
            createAliens();
            gameLoop.start();
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT  && ship.x - shipVelocityX >= 0) {
            ship.x -= shipVelocityX; //move left one tile
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT  && ship.x + shipVelocityX + ship.width <= boardWidth) {
            ship.x += shipVelocityX; //move right one tile
        }
        else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            //shoot bullet
            Block bullet = new Block(ship.x + shipWidth*15/32, ship.y, bulletWidth, bulletHeight, false, false, null);
            bulletList.add(bullet);
        }
    }

    public void createAliens(){
        Random  random = new Random();
        for (int r = 0; r < alienRows; r++){
            for  (int c = 0; c < alienColumns; c++){
                int randomImgIndex = random.nextInt(alienImgList.size());
                Block alien = new Block(
                        alienX + c * alienWidth,
                        alienY + r * alienHeight,
                        alienWidth,
                        alienHeight,
                        true,
                        true,
                        alienImgList.get(randomImgIndex)
                );
                alienList.add(alien);


            }

        }
        alienCount = alienList.size();
    }


}
