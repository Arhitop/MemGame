package com.example.memorycanvas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point; //можно убрать этот import
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log; //можно убрать этот import
import android.view.Display; //можно убрать этот import
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;  //можно убрать этот import
import android.widget.Toast;

import androidx.annotation.ColorRes; //можно убрать этот import
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

class Card {
    Paint p = new Paint();
    private float width;
    private float height;
    private boolean visible = true;
    public int getColor() {
        return this.color;
    }
    public Card(int color) {
        this.color = color;
    }
    public void flWidth(float width) {
        this.width = width;
    }
    public void flHeight(float height) {

        this.height = height;
    }
    public boolean visible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public void fl_x(float x) {
        this.x = x;
    }
    public void fl_y(float y) {
        this.y = y;
    }
    int color, backColor = R.color.backcard; //заменить переменную
    boolean isOpen = false;
    float x, y;
    @SuppressLint("ResourceAsColor")
    public void draw(Canvas c) {
        if (isOpen) {
            p.setColor(color);
        } else p.setColor(backColor);
        c.drawRect(x,y, x+width, y+height, p);
    }
    public boolean flip (float touch_x, float touch_y) {
        if (touch_x >= x && touch_x <= x + width && touch_y >= y && touch_y <= y + height) {
            isOpen = ! isOpen;
            return true;
        } else return false;
    }
}

public class TilesView extends View { //Надо заменить белый цвет карточки, так как при нажатии он сливается с уже открытыми карточками
    final int PAUSE_LENGTH = 2;
    boolean isOnPauseNow = false;
    int[] tiles = new int[]{Color.CYAN, Color.RED, Color.BLACK, Color.MAGENTA, Color.BLUE, Color.GREEN, Color.YELLOW, Color.TRANSPARENT, Color.DKGRAY, Color.WHITE, Color.LTGRAY, Color.GRAY}; 
    int openCard = 0;
    ArrayList<Card> cards;
    int width, height;
    public TilesView(Context context) {
        super(context);
    }

    public TilesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        newGame();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = canvas.getWidth();
        height = canvas.getHeight();

        float cardWidth = (width * 0.87f) / 4;
        float cardHeight = (height * 0.88f) / 6;
        float indentWidth = width * 0.1f / 4;
        float indentHeight = height * 0.1f / 6;
        int counter = 0;
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 6; j++) {
                Card CdHold = cards.get(counter);
                if (!CdHold.visible()){
                    CdHold.flWidth(0);
                    CdHold.flHeight(0);
                    counter++;
                    continue;
                }
                CdHold.flHeight(cardHeight);
                CdHold.flWidth(cardWidth);
                CdHold.fl_x(cardWidth * i + indentWidth * (i + 1));
                CdHold.fl_y(cardHeight * j + indentHeight * (j + 1));
                counter++;
            }
        }
        for (Card c: cards) {
            c.draw(canvas);
        }
    }

    private boolean Winner(){
        for (Card c : cards){
            if (c.visible())
                return true;
        }
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN && !isOnPauseNow)
        {
            for (Card c: cards) {

                if (openCard == 0) {
                    if (c.flip(x, y)) {
                        openCard ++;
                        invalidate();
                        return true;
                    }
                }

                if (openCard == 1) {
                    if (c.flip(x, y)) {
                        openCard ++;
                        invalidate();
                        PauseTask task = new PauseTask();
                        task.execute(PAUSE_LENGTH);
                        isOnPauseNow = true;
                        return true;
                    }
                }

            }
        }
        return true;
    }

    public void newGame() {
        cards = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            cards.add(new Card(tiles[i]));
            cards.add(new Card(tiles[i]));
        }
        Collections.shuffle(cards);
        invalidate();
    }

    class PauseTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            try {
                Thread.sleep(integers[0] * 1000);
            } catch (InterruptedException e) {}
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayList<Card> CdHold = new ArrayList<>();
            for (Card c: cards) {
                if (c.isOpen) {
                    CdHold.add(c);
                    c.isOpen = false;
                }
            }
            if (CdHold.get(0).getColor() == CdHold.get(1).getColor()){
                CdHold.get(0).setVisible(false);
                CdHold.get(1).setVisible(false);
            }
            if (!Winner()){
                Toast.makeText(getContext(), "Победа", Toast.LENGTH_LONG).show();
            }
            openCard = 0;
            isOnPauseNow = false;
            invalidate();
        }
    }
}
