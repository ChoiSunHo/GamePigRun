package com.example.chickenncola.gamepigrun;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    /*
        UI 변수 선언 영역
     */

    ImageView pig = null;
    ImageView cloud1 = null;
    ImageView cloud2 = null;
    ImageView obstacle = null;
    Button startbutton = null;
    TextView scoreboard = null;

    int test = 0;

    /*
        쓰레드 관련 변수 선언 영역
     */

    private int pignum = 0;
    private int score = 0; // 점수
    private boolean pigcontrol = true; // 점프할때 움직임을 멈추기 위해 선언한 변수
    private float originalpigY = 0; // 돼지의 Y축 위치를 저장할 변수
    private float purposeY = 0; // 돼지가 점프할 위치를 저장할 변수
    private int jumpcontrol1 = 0; // 올라갈 때와 내려갈 때를 구분하기 위한 변수
    private boolean jumpcontrol2 = true; // 스레드가 연속 동작하지 않ß기 위해 선언한 변수

    Thread pigTh = null;
    Thread backTh = null; // 구름, 장애물, 점프, 스코어 장애물끝나는지




    Handler pighandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updatepigThread();
        }
    };

    Handler backhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updatebackThread();
        }
    };

    /*
        OnCreate 영역
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);


        obstacle = (ImageView)findViewById(R.id.obstacle);
        obstacle.setX(obstacle.getX() - 100);
    }






    /*
        onStart 영역
     */

    @Override
    protected void onStart() {
        super.onStart();

        pig = (ImageView)findViewById(R.id.pig);
        cloud1 = (ImageView)findViewById(R.id.cloud1);
        cloud2 = (ImageView)findViewById(R.id.cloud2);
        obstacle = (ImageView)findViewById(R.id.obstacle);
        startbutton = (Button)findViewById(R.id.startbutton);
        scoreboard = (TextView)findViewById(R.id.scoreboard);
        purposeY = pig.getY() - 40;

        pigTh = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    while (pigcontrol) {
                        try {
                            pighandler.sendMessage(pighandler.obtainMessage());
                            Thread.sleep(100);
                        } catch (Throwable t) {
                        }
                    }
                }
            }
        });



        backTh = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        backhandler.sendMessage(backhandler.obtainMessage());
                        Thread.sleep(1);
                    } catch (Throwable t) {
                    }


                }
            }
        });
    }






    /*
        updateThread 영역
     */

    private void updatepigThread() {

        if ( startbutton.getText().equals("stop") ) {
            int mod = pignum % 2;

            switch (mod) {
                case 0:
                    pignum++;
                    pig.setImageResource(R.drawable.second);
                    break;
                case 1:
                    pignum++;
                    pig.setImageResource(R.drawable.first);
                    break;
            }
        }
    }





    /*
       backThread 함수 선언영역
    */

    private void updatebackThread() {

        // 구름 애니메이션
        if ( startbutton.getText().equals("stop") ) {
            cloud1.getX();
            cloud1.setX(cloud1.getX() - 2);

            cloud2.getX();
            cloud2.setX(cloud2.getX() - 2);

            if (cloud1.getX() < -600) {
                cloud1.setX(1700);
            }

            if (cloud2.getX() < -600) {
                cloud2.setX(1700);
            }
        }


        // 점수 업데이트
        if ( startbutton.getText().equals("stop") ) {
            score += 1;
            scoreboard.setText("score " + score);
        }



        // 점프 애니메이션
        if ( startbutton.getText().equals("stop") ) {
            if(jumpcontrol1 == 1) {
                pig.setY(pig.getY() - 2);
                if(pig.getY() <= purposeY){
                    jumpcontrol1 = 2;
                }
            } else if (jumpcontrol1 == 2) {
                pig.setY(pig.getY() + 2);
                if (pig.getY() >= originalpigY) {
                    jumpcontrol1 = 0;
                    jumpcontrol2 = true;
                    pigcontrol = true;
                }
            }
        }



        // 장애물 이동 애니메이션
        if ( startbutton.getText().equals("stop") ) {
            obstacle.setX(obstacle.getX() - 2);

            if (obstacle.getX() < -600){
                obstacle.setX(1700);
            }
        }

        if ( obstacle.getX() == 870) {
            if (pig.getY() == originalpigY) {
                startbutton.setText("restart");
            }
        }
    }






    /*
        start 버튼 클릭 시
     */

    public void startButton(View V){
        if(startbutton.getText().equals("start")) {
            originalpigY = pig.getY();
            pigTh.start();
            backTh.start();
            startbutton.setText("stop");
        } else if(startbutton.getText().equals("stop")){
            startbutton.setText("restart");
        } else{
            score = 0;
            startbutton.setText("stop");
        }
    }







    /*
        터치할 경우
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(jumpcontrol2) {
                    pigcontrol = false;
                    jumpcontrol1 = 1;
                    jumpcontrol2 = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

}
