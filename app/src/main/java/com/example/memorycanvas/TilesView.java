package com.example.memorycanvas;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Random;


public class TilesView extends View {



    class Card {
        Paint p = new Paint();

        public Card(int row,int column,int color) {
            this.color = color;
            this.row = row;
            this.column = column;

        }

        int color, backColor = Color.DKGRAY;
        boolean isOpen = false; // цвет карты
        //float x, y, width, height;
        int row,column;

        public void draw(Canvas c) {
            // нарисовать карту в виде цветного прямоугольника
            float left,top,right,bottom;
            float [] coord = getCoordinations(row,column);
            left = coord[0];
            top = coord[1];
            right = coord[2];
            bottom = coord[3];
            if (isOpen) {
                p.setColor(color);
            } else p.setColor(backColor);
            c.drawRect(left,top,right,bottom,p);
        }

        public float[] getCoordinations(int row,int column){
            float [] coords  = {column * (width / 4) + (width / 4) / 10, row * (height / 4) + (height / 4) / 10, column * (width / 4) + width / 4 - (width / 4) / 10, row * (height / 4) + height / 4 - (height / 4) / 10};
            return coords;
        }

        public boolean flip (float touch_x, float touch_y) {

            float [] coordinates = getCoordinations(row,column);
            if (touch_x >= coordinates[0] && touch_x <= coordinates[2] && touch_y >= coordinates[1] && touch_y <= coordinates[3]) {
                //isOpen = ! isOpen;
                return true;
            } else return false;
        }

    }


    // пауза для запоминания карт
    final int PAUSE_LENGTH = 2; // в секундах
    final int PAUSE_LENGTH_SHORT = 1; // в секундах
    boolean isOnPauseNow = false;
    int state = 0;
    Context context;
    Card c1 = null;
    Card c2 = null;

    // число открытых карт
    int openedCard = 0;

    ArrayList<Card> cards = new ArrayList<>();
    int cards_amount = 0;

    int width, height; // ширина и высота канвы

    public TilesView(Context context) {
        super(context);
    }

    public TilesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 1) заполнить массив tiles случайными цветами
        // сгенерировать поле 2*n карт, при этом
        // должно быть ровно n пар карт разных цветов
        newGame();


        cards_amount = cards.size();
        state = 1;

//        cards.add(new Card(0,0, 200, 150, Color.YELLOW));
//        cards.add(new Card(200+50, 0, 200 + 200 + 50, 150, Color.YELLOW));
//
//        cards.add(new Card(0,200, 200, 150 + 200, Color.RED));
//        cards.add(new Card(200+50, 200, 200 + 200 + 50, 150+200, Color.RED));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = canvas.getWidth();
        height = canvas.getHeight();
        // 2) отрисовка плиток
        // задать цвет можно, используя кисть
        Paint p = new Paint();
        for (Card c: cards) {
            c.draw(canvas);
        }
    }

    public boolean checkOpenCardsEqual(Card card1, Card card2){
        if (card1.color==card2.color){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 3) получить координаты касания
        int x = (int) event.getX();
        int y = (int) event.getY();
        // 4) определить тип события
        if (state == 1 || state == 3) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // палец коснулся экрана

                for (Card c : cards) {
                    if (c.flip(x, y)) {
                        if (state == 1) {
                            if (c1 == c) {
                                return true;
                            }
                            c1 = c;
                            c1.isOpen = true;
                            state = 3;
                            invalidate();
                        } else if (state == 3) {
                            if (c1==c){
                                return true;
                            }
                            c2 = c;
                            c2.isOpen = true;
                            if (checkOpenCardsEqual(c1, c2)) {
                                state = 4;

                            }
                            else {
                                state = 5;
                            }
                            invalidate();
                            PauseTask task = new PauseTask();
                            task.execute(PAUSE_LENGTH);
                            //isOnPauseNow = true;


                        }
                        Log.d("mytag", "card flipped: " + openedCard);
                        break;
                    }
                }

            }

        }

        return true;
    }

    public void changeState(){
        if (state == 0){
            //newGame();



            state = 1;
        }
        else if (state==4){
            cards.remove(c1);
            cards.remove(c2);
            if (cards.size()==0){
                Toast toast = Toast.makeText(context, "Вы выиграли!", Toast.LENGTH_SHORT);
                toast.show();
                state = 0;

            }
            else {
                state = 1;
            }
        }
        else if (state == 5){
            state = 1;
            c1.isOpen = false;
            c2.isOpen = false;
            c1 = null;
            c2 = null;
        }
        invalidate();
    }

    public void newGame() {
        Random r = new Random();
        state = 1;
        ArrayList<Card> cards2 = new ArrayList<>();
        int current_color = Color.rgb(r.nextInt(255),r.nextInt(255),r.nextInt(255));
        int k=0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (k==2){
                    current_color = Color.rgb(r.nextInt(255),r.nextInt(255),r.nextInt(255));
                    k=0;
                }
                cards2.add(new Card(i,j,current_color));
                k++;
            }
        }
        for (int i = 0; i < 8; i++) {
            current_color = Color.rgb(r.nextInt(255),r.nextInt(255),r.nextInt(255));
            int index = r.nextInt(cards2.size()-1);
            cards2.get(index).color = current_color;

            cards.add(cards2.get(index));
            cards2.remove(index);

            if (cards2.size() == 1){
                index = 0;
            }
            else {
                index = r.nextInt(cards2.size()-1);
            }


            cards2.get(index).color = current_color;

            cards.add(cards2.get(index));
            cards2.remove(index);
        }
        invalidate();



    }

    class PauseTask extends AsyncTask<Integer, Void, Void> {
        @Override

        protected Void doInBackground(Integer... integers) {

                Log.d("mytag", "Pause started");
                try {
                    Thread.sleep(integers[0] * 500); // передаём число секунд ожидания
                } catch (InterruptedException e) {
                }
                Log.d("mytag", "Pause finished");



            return null;
        }

        // после паузы, перевернуть все карты обратно


        @Override
        protected void onPostExecute(Void aVoid) {

            openedCard = 0;
            isOnPauseNow = false;
            invalidate();
            changeState();
        }
    }
}