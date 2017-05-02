package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.sun.org.apache.regexp.internal.RE;

import java.lang.System.*;

public class MyGame implements ApplicationListener {
	Texture rollImage;
	Texture blockImage;
	Texture ballImage;
	Texture heartImage;
	Texture GOImage;
	Sound collSound;
	Music Music;
	SpriteBatch batch;
	OrthographicCamera camera;
	Rectangle roll;
    Circle ball;
	int[][] blocks;
    int blocksQX = 11;
    int blocksQY = 7;
	Rectangle[][] blocksReal;
    float ballSpeed = 300;
    float ballSpeedX;
    float ballSpeedY;
    float goalPointX;
	float ballStart;
    float goalPointY;
    int a;
    int b;
    float time;
	boolean onRoll;
	BitmapFont font;
	boolean GameOver;
	int life;

	public void ResetGame(){
		roll.x = 800 / 2 - 128 / 2;
		roll.y = 20;
		ball.x = 800 / 2;
		ball.y = 20+32+16;
		ballSpeedX = 0;
		ballSpeedY = 0;
		goalPointX = 0;
		goalPointY = 0;
		onRoll = true;

	}

	public void Calc(){
		a = (int) (goalPointX - ball.x);
		b = (int) (goalPointY - ball.y);
		time = (float) Math.sqrt(a * a + b * b) / ballSpeed;
		if (time !=0) {
			ballSpeedX = a / time;
			ballSpeedY = b / time;
		}

	}



	@Override
	public void create() {

		rollImage = new Texture(Gdx.files.internal("roll.png"));
		blockImage = new Texture(Gdx.files.internal("block.png"));
        ballImage = new Texture(Gdx.files.internal("ball.png"));
		heartImage = new Texture(Gdx.files.internal("heart.png"));
		GOImage = new Texture(Gdx.files.internal("GO.png"));



		collSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"));
		Music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));


			Music.setLooping(true);
		    Music.setVolume(0.5f);
			//Music.play();


		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();


		roll = new Rectangle();

        ball = new Circle();
		onRoll = true;

		font = new BitmapFont();

        roll.x = 800 / 2 - 128 / 2;
		roll.y = 20;
		roll.width = 128;
		roll.height = 32;
		life = 3;



        ball.x = 800 / 2;
        ball.y = 20+32+16;
        ball.radius = 16;
		ballStart = ball.x;

		blocks = new int[blocksQX][blocksQY];
		for (int y = 0; y < blocksQY; y += 1) {
			for (int x = 0; x < blocksQX; x += 1) {
				blocks[x][y] = 1;

			}
		}



		blocksReal= new Rectangle[blocksQX][blocksQY];
		for (int y = 0; y < blocksQY; y += 1) {
			for (int x = 0; x < blocksQX; x += 1) {
                blocksReal[x][y] = new Rectangle();
				if(blocks[x][y]==1) {
						blocksReal[x][y].x = (32 + x * 64) ;
						blocksReal[x][y].y = (200 + y * 32) ;
						blocksReal[x][y].width = 64;
						blocksReal[x][y].height = 32;
				}
			}
		}

	}





	@Override
	public void render() {

		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		camera.update();


		batch.setProjectionMatrix(camera.combined);


		batch.begin();
		batch.draw(rollImage, roll.x, roll.y, roll.width, roll.height);
        batch.draw(ballImage, ball.x-ball.radius, ball.y-ball.radius, ball.radius*2, ball.radius*2);
		for (int y = 0; y < blocksQY; y += 1) {
			for (int x = 0; x < blocksQX; x += 1) {
				if(blocksReal[x][y]!= null) {
					batch.draw(blockImage, 32 + x * 64, 200 + y * 32, blocksReal[x][y].width, blocksReal[x][y].height);
				}
			}
		}

		batch.draw(heartImage, 670, 440, 32, 32);
		font.draw(batch, "ball x:" + (int)ball.x, 50,450);
		font.draw(batch, "ball y:" + (int)ball.y, 50,430);
		font.draw(batch, "goalPointX:" + (int)goalPointX, 50,400);
		font.draw(batch, "goalPointY:" + (int)goalPointY, 50,380);
		font.draw(batch, "speed x:" + (int)ballSpeedX, 50,350);
		font.draw(batch, "speed y:" + (int)ballSpeedY, 50,330);
		font.draw(batch, "x" + life, 700,460);
		if (GameOver) {
			Gdx.gl.glClearColor(156, 160, 0.2f, 1);
			batch.draw(GOImage, 0, 0, 800, 480);
		}
		batch.end();

        

		if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
			if (onRoll) {
				ballStart= ball.x;
				goalPointX = touchPos.x;
				goalPointY = touchPos.y;
				onRoll = false;
				a = (int) (goalPointX - ball.x);
				b = (int) (goalPointY - ball.y);
				time = (float) Math.sqrt(a * a + b * b) / ballSpeed;
			}
			if (time !=0) {
				ballSpeedX = a / time;
				ballSpeedY = b / time;
			}
            if (touchPos.x < roll.x) {
                roll.x -= 300 * Gdx.graphics.getDeltaTime();
            } else {
                roll.x += 300 * Gdx.graphics.getDeltaTime();
            }
        }


        ball.x += ballSpeedX * Gdx.graphics.getDeltaTime();
        ball.y += ballSpeedY * Gdx.graphics.getDeltaTime();


			/*if (touchPos.x < ball.x) {
				ball.x -= 300 * Gdx.graphics.getDeltaTime();
			} else {
				ball.x += 300 * Gdx.graphics.getDeltaTime();
			}
            if (touchPos.y < ball.y) {
                ball.y -= 300 * Gdx.graphics.getDeltaTime();
            } else {
                ball.y += 300 * Gdx.graphics.getDeltaTime();
            }*/


		if (Gdx.input.isKeyPressed(Keys.LEFT)) roll.x -= 300 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) roll.x += 300 * Gdx.graphics.getDeltaTime();

		if (onRoll){
			if (Gdx.input.isKeyPressed(Keys.LEFT)) ball.x -= 300 * Gdx.graphics.getDeltaTime();
			if (Gdx.input.isKeyPressed(Keys.RIGHT)) ball.x += 300 * Gdx.graphics.getDeltaTime();
		}

		if (Gdx.input.isKeyPressed(Keys.R)) {
			ResetGame();
		}


		if (roll.x < 0) roll.x = 0;
		if (roll.x > 800 - 64) roll.x = 800 - 64;

		for (int y = 0; y < blocksQY; y += 1) {
			for (int x = 0; x < blocksQX; x += 1) {
                if (blocksReal[x][y] != null){
                    if (Intersector.overlaps(ball,blocksReal[x][y])) {
                        blocks[x][y] = 0;
                        blocksReal[x][y] = null;
                        collSound.play();
						goalPointX = ball.x - (ballStart - ball.x);
						goalPointY = 0;
						Calc();
                }
                }
            }
		}

		if (Intersector.overlaps(ball,roll)) {

			goalPointY = 480;
			goalPointX = (roll.x+64) - 15*(roll.x+64-ball.x);
			Calc();
			ballStart = ball.x;


		}

		if (ball.x-ball.radius <= 0) {
			goalPointX = ball.y / Math.abs(ballSpeedY) * Math.abs(ballSpeedX);
			if (ballSpeedY < 0) {
				goalPointY = 0;
			}
			else{
				goalPointY = 480;
			}

			a = (int) (goalPointX - ball.x);
			b = (int) (goalPointY - ball.y);
			time = (float) Math.sqrt(a * a + b * b) / ballSpeed;
			if (time !=0) {
				ballSpeedX = a / time;
				ballSpeedY = b / time;
			}
			ballStart = ball.x;
		}
		if (ball.x+ball.radius >= 800) {
			goalPointX = 800 - (ball.y / Math.abs(ballSpeedY) * Math.abs(ballSpeedX));
			if (ballSpeedY < 0) {
				goalPointY = 0;
			}
			else{
				goalPointY = 480;
			}
			a = (int) (goalPointX - ball.x);
			b = (int) (goalPointY - ball.y);
			time = (float) Math.sqrt(a * a + b * b) / ballSpeed;
			if (time !=0) {
				ballSpeedX = a / time;
				ballSpeedY = b / time;
			}
			ballStart = ball.x;
		}
		if (ball.y+ball.radius >=480) {
			goalPointY = 0;
			if (ballSpeedX < 0) {
				goalPointX = 0;
			}
			else{
				goalPointX = 800;
			}
			a = (int) (goalPointX - ball.x);
			b = (int) (goalPointY - ball.y);
			time = (float) Math.sqrt(a * a + b * b) / ballSpeed;
			if (time !=0) {
				ballSpeedX = a / time;
				ballSpeedY = b / time;
			}
			ballStart = ball.x;
		}

		if (ball.y + ball.radius < 0){
			ResetGame();
			--life;
			if (life <=0){
				GameOver = true;
			}

		}






	}
	@Override
	public void dispose() {
		// высвобождение всех нативных ресурсов
		rollImage.dispose();
		ballImage.dispose();
		blockImage.dispose();
		Music.dispose();
		batch.dispose();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}