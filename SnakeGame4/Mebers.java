package com.company;

import java.util.LinkedList;
import java.util.Vector;

/**
 * Created by USER on 2017/6/10.
 */
class SneakBody
{
    int x;
    int y;
    int direct=0;
    int speed = Main.blockSize;

    public SneakBody(int x,int y)
    {
        this.x=x;
        this.y=y;
    }
}

class Food
{
    int x;
    int y;
    boolean alive = true;

    public Food(int x,int y)
    {
        this.x=x;
        this.y=y;
    }
    public void genFood(boolean array[][])
    {
        if(!this.alive)
        {
            do {
                this.x = ((int) (Math.random() * Main.blockSize) * Main.blockSize) + (Main.blockSize>>1);
                this.y = ((int) (Math.random() * Main.blockSize) * Main.blockSize) + (Main.blockSize>>1);
            }while(array[this.x/Main.blockSize][this.y/Main.blockSize]==true);
            this.alive = true;
        }
    }

}

class AStar
{
    Vector<Points> closes = new Vector<Points>();
    Vector<Points> opens = new Vector<Points>();
    LinkedList<SneakBody> fakeSneak = new LinkedList<SneakBody>();
    Vector<Points> finalDecision = new Vector<Points>();

     class Points
    {
        int x = 0;
        int y = 0;
        int G = 0;
        int H = 0;
        int F = 0;
        Points fatherPoint;

        public Points (int x,int y)
        {
            this.x=x;
            this.y=y;
        }

    }

    //檢查新加入的點是否在CLOSE和OPEN向量中
    public boolean checkVectorItems(Vector<Points> closes,Vector<Points> opens,Points p)
    {
        boolean TorF=true;

        for(int i=0;i<closes.size();i++)
        {
            if(p.x == closes.get(i).x && p.y == closes.get(i).y)
            {/*
			 if(closes.lastElement().fatherPoint != null)
				if(closes.lastElement().fatherPoint.F < closes.get(i).F)
				{
					closes.lastElement().fatherPoint = closes.get(i);
				}*/
                TorF = false;
            }

        }
        for(int i=0;i<opens.size();i++)
        {
            if(p.x == opens.get(i).x && p.y == opens.get(i).y)
            {/*
			 if(closes.lastElement().fatherPoint != null)
               if(closes.lastElement().fatherPoint.F < opens.get(i).F)
				{
					closes.lastElement().fatherPoint = opens.get(i);
				}*/
                TorF = false;
            }
        }

        //True代表新點不在CLOSE和OPEN向量中
        return TorF;
    }


    public void calHandF2(Points p,Food foodPostion)
    {
        int num1 = Math.abs(p.x-foodPostion.x);
        int num2 = Math.abs(p.y-foodPostion.y);
        p.G =10 +closes.lastElement().G;
        p.H = num1+num2;
        p.F = p.G+p.H;
    }
    public boolean canMove(Points p,boolean a[][])
    {

            if (a[p.x / Main.blockSize][p.y / Main.blockSize] == false) {
                return true;
            }else
            {
                return false;
            }

    }

    public void TwoDimensionalArrayCopy(boolean a1[][],boolean a2[][])
    {
        for(int i=0;i<a2.length;i++)
        {
            a1[i] = a2[i].clone();
        }
    }

    //用來探路用的假蛇,先把假蛇移動到食物點
    public boolean fakeMove(LinkedList<SneakBody> sneakBodyLinkedList,boolean fakea[][])
    {
        boolean findTailorNot;
        if(!finalDecision.isEmpty())
        {
            for (int i=0;i<finalDecision.size();i++)
            {
                fakea[sneakBodyLinkedList.getLast().x / Main.blockSize][sneakBodyLinkedList.getLast().y / Main.blockSize] = false;
                sneakBodyLinkedList.getLast().x = finalDecision.get(i).x;
                sneakBodyLinkedList.getLast().y = finalDecision.get(i).y;
                sneakBodyLinkedList.addFirst(sneakBodyLinkedList.getLast());
                fakea[sneakBodyLinkedList.getFirst().x / Main.blockSize][sneakBodyLinkedList.getFirst().y / Main.blockSize] = true;
                sneakBodyLinkedList.removeLast();

            }
        }
	if(sneakBodyLinkedList.size()>10)
	if(Math.abs(sneakBodyLinkedList.getFirst().x-sneakBodyLinkedList.getLast().x)<=20 && Math.abs(sneakBodyLinkedList.getFirst().y-sneakBodyLinkedList.getLast().y)<=20)
	{
		return false;
	}
	
        //計算吃到食物之後是否有路徑可以到達蛇尾
        findTailorNot = findTail(sneakBodyLinkedList,fakea);

        return findTailorNot;


    }

    public void sneakMove(LinkedList<SneakBody> sneakBodyVector, Food foodPosition, boolean array[][])
    {
        Points p = new Points(sneakBodyVector.getFirst().x,sneakBodyVector.getFirst().y);
        opens.add(p);
        Points smallest;
        int temp = 0;

        boolean fakeArray[][] = new boolean[array.length][array[0].length];
        TwoDimensionalArrayCopy(fakeArray,array);

        while (!opens.isEmpty()) {
            smallest = opens.get(0);
            temp = 0;
            for(int i = 1;i>opens.size();i++)
            {
                if(smallest.F<opens.get(i).F)
                {
                    smallest = opens.get(i);
                    temp = i;
                }
            }

            closes.add(smallest);
            opens.removeElementAt(temp);

            //如果到達目的
            if(smallest.x == foodPosition.x && smallest.y == foodPosition.y)
            {
                Points fatherP = closes.lastElement();
                while(true)
                {
                    if(fatherP.x !=sneakBodyVector.getFirst().x || fatherP.y != sneakBodyVector.getFirst().y) {
                        finalDecision.insertElementAt(fatherP,0);
                        fatherP = fatherP.fatherPoint;
                    }else
                    {   //複製一隻假蛇
                        for(int i=0;i<sneakBodyVector.size();i++) {
                            SneakBody sb = new SneakBody(sneakBodyVector.get(i).x,sneakBodyVector.get(i).y);
                            fakeSneak.add(sb);
                        }
                        if(!fakeMove(fakeSneak,fakeArray))//假移動
                        {
                            finalDecision.removeAllElements();
                        }
                        fakeSneak.clear();
                        return;
                    }
                }

            }

            //計算上格子
            if (closes.lastElement().y - sneakBodyVector.getFirst().speed > 0)
            {
                Points tempP = new Points(closes.lastElement().x,closes.lastElement().y-sneakBodyVector.getFirst().speed);
                if(canMove(tempP,array))
                {
                    if (checkVectorItems(closes, opens, tempP)) {
                        //計算H和F值
                        calHandF2(tempP, foodPosition);
                        opens.add(tempP);
                        tempP.fatherPoint = closes.lastElement();
                    }
                }
            }
            //計算右格子
            if (closes.lastElement().x + sneakBodyVector.getFirst().speed < Main.width)
            {
                Points tempP = new Points(closes.lastElement().x+sneakBodyVector.getFirst().speed,closes.lastElement().y);
                if(canMove(tempP,array))
                {
                    if (checkVectorItems(closes, opens, tempP)) {
                        //計算H和F值
                        calHandF2(tempP, foodPosition);
                        opens.add(tempP);
                        tempP.fatherPoint = closes.lastElement();
                    }
                }
            }


            //計算下格子
            if (closes.lastElement().y + sneakBodyVector.getFirst().speed < Main.high) {
                Points tempP = new Points(closes.lastElement().x,closes.lastElement().y+sneakBodyVector.getFirst().speed);
                if(canMove(tempP,array))
                {
                    if (checkVectorItems(closes, opens, tempP)) {
                        //計算H和F值
                        calHandF2(tempP, foodPosition);
                        opens.add(tempP);
                        tempP.fatherPoint = closes.lastElement();
                    }
                }
            }
            //計算左格子
            if (closes.lastElement().x - sneakBodyVector.getFirst().speed > 0)
            {
                Points tempP = new Points(closes.lastElement().x-sneakBodyVector.getFirst().speed,closes.lastElement().y);
                if(canMove(tempP,array))
                {
                    if (checkVectorItems(closes, opens, tempP)) {
                        //計算H和F值
                        calHandF2(tempP, foodPosition);
                        opens.add(tempP);
                        tempP.fatherPoint = closes.lastElement();
                    }
                }
            }
        }
    }

    public boolean findTail(LinkedList<SneakBody> sneakBody,boolean array[][])
    {
        Points p = new Points(sneakBody.getFirst().x,sneakBody.getFirst().y);
        Vector<Points> tempVC = new Vector<Points>();
        Vector<Points> tempVO = new Vector<Points>();
        tempVO.add(p);
        Food fakefood = new Food(sneakBody.getLast().x,sneakBody.getLast().y);
        array[fakefood.x/Main.blockSize][fakefood.y/Main.blockSize] = false;

        Points smallest;

//        if(sneakBody.size()<=3)
//        {
//            return true;
//        }
        while(!tempVO.isEmpty()) {

            smallest = tempVO.get(0);
            int temp = 0;
            for (int i = 1; i < tempVO.size(); i++) {
                if (smallest.F > tempVO.get(i).F) {
                    smallest = tempVO.get(i);
                    temp = i;
                }
            }

            tempVC.add(smallest);
            tempVO.removeElementAt(temp);


            if (tempVC.lastElement().x == sneakBody.getLast().x && tempVC.lastElement().y == sneakBody.getLast().y) {
                return true;//true
            }

            //上
            if (tempVC.lastElement().y - sneakBody.getFirst().speed > 0) {
                Points tempP = new Points(tempVC.lastElement().x, tempVC.lastElement().y - sneakBody.getFirst().speed);
                if (canMove(tempP, array)) {
                    if (checkVectorItems(tempVC, tempVO, tempP)) {
                        //計算H和F值
                        calHandF2(tempP, fakefood);
                        tempVO.add(tempP);
                    }
                }
            }
            //右
            if (tempVC.lastElement().x + sneakBody.getFirst().speed < Main.width) {
                Points tempP = new Points(tempVC.lastElement().x+sneakBody.getFirst().speed, tempVC.lastElement().y);
                if (canMove(tempP, array)) {
                    if (checkVectorItems(tempVC, tempVO, tempP)) {
                        //計算H和F值
                        calHandF2(tempP, fakefood);
                        tempVO.add(tempP);
                    }
                }
            }
            //下
            if (tempVC.lastElement().y + sneakBody.getFirst().speed < Main.high) {
                Points tempP = new Points(tempVC.lastElement().x, tempVC.lastElement().y + sneakBody.getFirst().speed);
                if (canMove(tempP, array)) {
                    if (checkVectorItems(tempVC, tempVO, tempP)) {
                        //計算H和F值
                        calHandF2(tempP, fakefood);
                        tempVO.add(tempP);
                    }
                }
            }
            //左
            if (tempVC.lastElement().x - sneakBody.getFirst().speed > 0) {
                Points tempP = new Points(tempVC.lastElement().x- sneakBody.getFirst().speed, tempVC.lastElement().y );
                if (canMove(tempP, array)) {
                    if (checkVectorItems(tempVC, tempVO, tempP)) {
                        //計算H和F值
                        calHandF2(tempP, fakefood);
                        tempVO.add(tempP);
                    }
                }
            }



        }
        return false;//false
    }

    //以最遠路徑追著蛇尾跑
    public void sneakMoveTail(LinkedList<SneakBody> sneakBodyVector,boolean array[][])
    {
        Points p = new Points(sneakBodyVector.getFirst().x,sneakBodyVector.getFirst().y);
        opens.add(p);
        Points smallest;
        int temp = 0;
        Food fakefood = new Food(sneakBodyVector.getLast().x,sneakBodyVector.getLast().y);

	if(Math.abs(sneakBodyVector.getFirst().x-fakefood.x)<=20 && Math.abs(sneakBodyVector.getFirst().y-fakefood.y)<=20)
	{
		return;
	}

        array[fakefood.x/Main.blockSize][fakefood.y/Main.blockSize] = false;
        while (!opens.isEmpty()) {
            smallest = opens.get(0);
            temp = 0;
            for(int i = 1;i<opens.size();i++)
            {
                if(smallest.F<opens.get(i).F)
                {
                    smallest = opens.get(i);
                    temp = i;
                }
            }

            closes.add(smallest);
            opens.removeElementAt(temp);



            //如果到達目的
            if(smallest.x == fakefood.x && smallest.y == fakefood.y)
            {

                Points fatherP = closes.lastElement();
		
                while(true)
                {
                    if(fatherP.x !=sneakBodyVector.getFirst().x || fatherP.y != sneakBodyVector.getFirst().y) {
                        finalDecision.insertElementAt(fatherP,0);
                        fatherP = fatherP.fatherPoint;
                    }else
                    {
                        return;
                    }
                }

            }

            //計算上格子
            if (closes.lastElement().y - sneakBodyVector.getFirst().speed > 0)
            {
                Points tempP = new Points(closes.lastElement().x,closes.lastElement().y-sneakBodyVector.getFirst().speed);
                if(canMove(tempP,array))
                {
                    if (checkVectorItems(closes, opens, tempP)) {
                        //計算H和F值
                        calHandF2(tempP, fakefood);
                        opens.add(tempP);
                        tempP.fatherPoint = closes.lastElement();
                    }
                }
            }
            //計算右格子
            if (closes.lastElement().x + sneakBodyVector.getFirst().speed < 400)
            {
                Points tempP = new Points(closes.lastElement().x+sneakBodyVector.getFirst().speed,closes.lastElement().y);
                if(canMove(tempP,array))
                {
                    if (checkVectorItems(closes, opens, tempP)) {
                        //計算H和F值
                        calHandF2(tempP, fakefood);
                        opens.add(tempP);
                        tempP.fatherPoint = closes.lastElement();
                    }
                }
            }


            //計算下格子
            if (closes.lastElement().y + sneakBodyVector.getFirst().speed < 400) {
                Points tempP = new Points(closes.lastElement().x,closes.lastElement().y+sneakBodyVector.getFirst().speed);
                if(canMove(tempP,array))
                {
                    if (checkVectorItems(closes, opens, tempP)) {
                        //計算H和F值
                        calHandF2(tempP, fakefood);
                        opens.add(tempP);
                        tempP.fatherPoint = closes.lastElement();
                    }
                }
            }
            //計算左格子
            if (closes.lastElement().x - sneakBodyVector.getFirst().speed > 0)
            {
                Points tempP = new Points(closes.lastElement().x-sneakBodyVector.getFirst().speed,closes.lastElement().y);
                if(canMove(tempP,array))
                {
                    if (checkVectorItems(closes, opens, tempP)) {
                        //計算H和F值
                        calHandF2(tempP, fakefood);
                        opens.add(tempP);
                        tempP.fatherPoint = closes.lastElement();
                    }
                }
            }
        }

    }

    public void copyFakeSnake(LinkedList<SneakBody> sneakBodyVector,boolean array[][],boolean fakeArray[][])
	{
		fakeSneak.clear();
		TwoDimensionalArrayCopy(fakeArray,array);
		fakeArray[sneakBodyVector.getFirst().x / Main.blockSize][sneakBodyVector.getFirst().y / Main.blockSize] = true;
		for(int i=0;i<sneakBodyVector.size();i++) {
                    SneakBody sb = new SneakBody(sneakBodyVector.get(i).x,sneakBodyVector.get(i).y);
                    fakeSneak.add(sb);
                        }
	}
    //找不到食物也找不到尾巴,以最遠距離找著蛇尾
    public void sneakMoveFar(LinkedList<SneakBody> sneakBodyVector, Food foodPosition, boolean array[][])
    {
        Points p = new Points(sneakBodyVector.getFirst().x,sneakBodyVector.getFirst().y);
        closes.add(p);
        Points biggest;
        Food fakefood = new Food(sneakBodyVector.getLast().x,sneakBodyVector.getLast().y);
        int temp = 0;

	boolean fakeArray[][] = new boolean[array.length][array[0].length];        
	
	boolean findTailorNot;

            //計算上格子
            if (closes.lastElement().y - sneakBodyVector.getFirst().speed > 0)
            {
                Points tempP = new Points(closes.lastElement().x,closes.lastElement().y-sneakBodyVector.getFirst().speed);
                if(canMove(tempP,array))
                {
                    if (checkVectorItems(closes, opens, tempP)) {
			copyFakeSnake(sneakBodyVector, array, fakeArray);
			findTailorNot = findTail(sneakBodyVector,fakeArray);
			if(findTailorNot == true){
                        //計算H和F值
                        calHandF2(tempP, fakefood);
                        opens.add(tempP);
                        tempP.fatherPoint = closes.lastElement();
			}
                    }
                }
            }
            //計算右格子
            if (closes.lastElement().x + sneakBodyVector.getFirst().speed < 400)
            {
                Points tempP = new Points(closes.lastElement().x+sneakBodyVector.getFirst().speed,closes.lastElement().y);
                if(canMove(tempP,array))
                {
                    if (checkVectorItems(closes, opens, tempP)) {
			copyFakeSnake(sneakBodyVector, array, fakeArray);
			findTailorNot = findTail(sneakBodyVector,fakeArray);
			if(findTailorNot == true){
                        //計算H和F值
                        calHandF2(tempP, fakefood);
                        opens.add(tempP);
                        tempP.fatherPoint = closes.lastElement();
			}
                    }
                }
            }


            //計算下格子
            if (closes.lastElement().y + sneakBodyVector.getFirst().speed < 400) {
                Points tempP = new Points(closes.lastElement().x,closes.lastElement().y+sneakBodyVector.getFirst().speed);
                if(canMove(tempP,array))
                {
                    if (checkVectorItems(closes, opens, tempP)) {
			copyFakeSnake(sneakBodyVector, array, fakeArray);
			findTailorNot = findTail(sneakBodyVector,fakeArray);
			if(findTailorNot == true){
                        //計算H和F值
                        calHandF2(tempP, fakefood);
                        opens.add(tempP);
                        tempP.fatherPoint = closes.lastElement();
			}
                    }
                }
            }
            //計算左格子
            if (closes.lastElement().x - sneakBodyVector.getFirst().speed > 0)
            {
                Points tempP = new Points(closes.lastElement().x-sneakBodyVector.getFirst().speed,closes.lastElement().y);
                if(canMove(tempP,array))
                {
                    if (checkVectorItems(closes, opens, tempP)) {
			copyFakeSnake(sneakBodyVector, array, fakeArray);
			findTailorNot = findTail(sneakBodyVector,fakeArray);
			if(findTailorNot == true){
                        //計算H和F值
                        calHandF2(tempP, fakefood);
                        opens.add(tempP);
                        tempP.fatherPoint = closes.lastElement();
			}
                    }
                }
            }

        biggest = opens.get(0);
        temp = 0;
        for(int i = 1;i<opens.size();i++)
        {
            if(biggest.F<opens.get(i).F)
            {
                biggest = opens.get(i);
                temp = i;
            }
        }

        closes.add(biggest);
        //opens.removeElementAt(temp);

    }
}
