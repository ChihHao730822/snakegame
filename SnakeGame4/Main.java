package com.company;

//import com.sun.xml.internal.ws.client.sei.ResponseBuilder;

import javax.swing.*;
import javax.xml.soap.Text;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Vector;
//import com.company.Mmebers;

public class Main extends JFrame implements Runnable{

    MyPanel mp = null;
    Label lb = null;
    Label lb2 = null;
    static int score = 0;
    static int blockSize = 20;
    static int times = 1;
    static int high = 400*times;
    static int width = 400*times;
    static int gameSpeed = 1000;
    static int speedBase = 20;

    static boolean stop = false;

    public static void main(String[] args) {
        Main m = new Main();
        Thread tt = new Thread(m);
        tt.start();
        // write your code here
    }

    public Main()
    {
        mp = new MyPanel();
        lb = new Label();
        lb2 = new Label();

        Thread MypaneThread = new Thread(mp);
        MypaneThread.start();

        this.addMouseListener(mp);
        this.addKeyListener(mp);
        this.add(mp);
        this.add(lb,BorderLayout.BEFORE_FIRST_LINE);
        this.add(lb2,BorderLayout.WEST);
        this.setSize(500,500);
        this.setLocation(1400,300);
        this.setVisible(true);
        this.setTitle("Sneak2");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lb.setText("Score : " + score);
        }
    }
}



class MyPanel extends JPanel implements Runnable,MouseListener,KeyListener
{


    AStar aStar = new AStar();


    LinkedList<SneakBody> sneakBodies = new LinkedList<SneakBody>();


    boolean arry[][]= new boolean[Main.width/Main.blockSize][Main.high/Main.blockSize];

    Food food = new Food(150,150);

    public void paint(Graphics g)
    {
        super.paint(g);

        //畫方格線
//        for(int i=0;i<Main.high;i+=Main.blockSize)
//        {
//            for(int j=0;j<Main.width;j+=Main.blockSize)
//            {
//                g.setColor(Color.black);
//                g.drawRect(i, j, Main.blockSize, Main.blockSize);
//            }
//        }

        //畫外框
        g.setColor(Color.RED);
        g.drawRect(0,0,Main.width,Main.high);


        //蛇頭
        g.setColor(Color.red);
        g.fillRect(sneakBodies.getFirst().x-(Main.blockSize>>1), sneakBodies.getFirst().y-(Main.blockSize>>1), Main.blockSize,Main.blockSize);
        //蛇身
        g.setColor(Color.orange);
        for(int i=1;i<sneakBodies.size();i++)
        {
            g.fill3DRect(sneakBodies.get(i).x-(Main.blockSize>>1), sneakBodies.get(i).y-(Main.blockSize>>1), Main.blockSize, Main.blockSize,true);
        }
        //食物
        g.setColor(Color.MAGENTA);
        g.fillOval(food.x-(Main.blockSize>>1), food.y-(Main.blockSize>>1), Main.blockSize, Main.blockSize);
    }


    public void sneakEatFood(SneakBody sb,Food food)
    {
        if(food.x==sb.x &&  food.y == sb.y)
        {
            Main.score += 1;
            SneakBody body = new SneakBody(sneakBodies.getLast().x,sneakBodies.getLast().y);
            sneakBodies.add(body);
            food.alive = false;
	    arry[sneakBodies.getLast().x / Main.blockSize][sneakBodies.getLast().y / Main.blockSize] = true;
        }

        food.genFood(arry);
    }

    public void sneakMoveMotion(LinkedList<SneakBody> fakesneak,boolean fakeArray[][],AStar aster)
    {
        fakeArray[fakesneak.getLast().x / Main.blockSize][fakesneak.getLast().y / Main.blockSize] = false;
        fakesneak.getLast().x = aStar.finalDecision.get(0).x;
        fakesneak.getLast().y = aStar.finalDecision.get(0).y;
        fakesneak.addFirst(fakesneak.getLast());
        fakeArray[fakesneak.getFirst().x / Main.blockSize][fakesneak.getFirst().y / Main.blockSize] = true;
        fakesneak.removeLast();



        if(!aster.finalDecision.isEmpty())
        {
            clearVector(aster);
            aster.sneakMoveTail(fakesneak,fakeArray);

        }
    }

    public void clearVector(AStar aStar)
    {
        if(!aStar.opens.isEmpty())
            aStar.opens.removeAllElements();
        if(!aStar.closes.isEmpty())
            aStar.closes.removeAllElements();
        if(!aStar.finalDecision.isEmpty())
            aStar.finalDecision.removeAllElements();
    }
    @Override
    public void run() {

        //Main.stop = true;
        SneakBody sb = new SneakBody(10,10);

        sneakBodies.add(sb);
        arry[sb.x/Main.blockSize][sb.y/Main.blockSize] = true;

        while(true)
        {

            try {
                Thread.sleep(Main.gameSpeed/Main.speedBase);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(Main.stop == false) {

                aStar.sneakMove(sneakBodies, food, arry);
                if(!aStar.finalDecision.isEmpty())//如果有到達食物的路徑
                {

                    arry[sneakBodies.getLast().x / Main.blockSize][sneakBodies.getLast().y / Main.blockSize] = false;
                    sneakBodies.getLast().x = aStar.finalDecision.get(0).x;
                    sneakBodies.getLast().y = aStar.finalDecision.get(0).y;
                    sneakBodies.addFirst(sneakBodies.getLast());
                    arry[sneakBodies.getFirst().x / Main.blockSize][sneakBodies.getFirst().y / Main.blockSize] = true;
                    sneakBodies.removeLast();
                    sneakEatFood(sneakBodies.getFirst(), food);

                    clearVector(aStar);

                }else//將目標選擇為尾巴
                {
                    clearVector(aStar);

                    aStar.sneakMoveTail(sneakBodies, arry);
                    arry[sneakBodies.getLast().x/Main.blockSize][sneakBodies.getLast().y/Main.blockSize] = true;

                    if (!aStar.finalDecision.isEmpty()) {

                        arry[sneakBodies.getLast().x / Main.blockSize][sneakBodies.getLast().y / Main.blockSize] = false;
                        sneakBodies.getLast().x = aStar.finalDecision.firstElement().x;
                        sneakBodies.getLast().y = aStar.finalDecision.firstElement().y;
                        sneakBodies.addFirst(sneakBodies.getLast());
                        arry[sneakBodies.getFirst().x / Main.blockSize][sneakBodies.getFirst().y / Main.blockSize] = true;
                        sneakBodies.removeLast();
			sneakEatFood(sneakBodies.getFirst(), food);
                        clearVector(aStar);

                    } else {//如果沒有到達尾巴的路,
                        clearVector(aStar);
                        aStar.sneakMoveFar(sneakBodies, food, arry);
                        arry[sneakBodies.getLast().x / Main.blockSize][sneakBodies.getLast().y / Main.blockSize] = false;
                        sneakBodies.getLast().x = aStar.closes.lastElement().x;
                        sneakBodies.getLast().y = aStar.closes.lastElement().y;
                        sneakBodies.addFirst(sneakBodies.getLast());
                        arry[sneakBodies.getFirst().x / Main.blockSize][sneakBodies.getFirst().y / Main.blockSize] = true;
                        sneakBodies.removeLast();
			sneakEatFood(sneakBodies.getFirst(), food);
                        clearVector(aStar);
                    }
                }

                this.repaint();

            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        System.out.println("X: "+(e.getX()-20)+"  Y :"+(e.getY()-50));
        System.out.println(arry[(e.getX()-20)/Main.blockSize][(e.getY()-50)/Main.blockSize]);
    }

    @Override
    public void mousePressed(MouseEvent e) {


    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {


        if(e.getKeyCode()==KeyEvent.VK_SPACE)
        {
            if(Main.stop == true)
            {
                Main.stop = false;
            }else {
                Main.stop = true;
            }
        }

        if(e.getKeyCode()==KeyEvent.VK_1)
        {
            Main.speedBase = 1;
        }else if(e.getKeyCode()==KeyEvent.VK_2)
        {
            Main.speedBase = 2;
        }
        else if(e.getKeyCode()==KeyEvent.VK_3)
        {
            Main.speedBase = 10;
        }
        else if(e.getKeyCode()==KeyEvent.VK_4)
        {
            Main.speedBase = 20;
        }
        else if(e.getKeyCode()==KeyEvent.VK_5)
        {
            Main.speedBase = 100;
        }

        if(e.getKeyCode()==KeyEvent.VK_UP)
        {
            if(Main.speedBase < 100)
                Main.speedBase += 5;
        }else if(e.getKeyCode()==KeyEvent.VK_DOWN)
        {
            if(Main.speedBase > 5)
                Main.speedBase -= 5;
            else if(Main.speedBase > 1)
                Main.speedBase -= 1;
        }

        System.out.println("延遲"+Main.gameSpeed/Main.speedBase+"ms");
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
