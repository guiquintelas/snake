package snake.grafica;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class MsgCampo implements Runnable{
	private String msg;
	private float alpha = 0.0f;
	private boolean isAtivado = false;
	private boolean isFadingIn = true;
	private boolean isFadingOut = false;
	private int timer = 0;
	private double y = 70;
	private static final double Y_VARIAÇAO = 10;
	private static final long DURAÇAO = 5000;
	private static final int FADE_DURAÇAO = 800;
	private static final int DELAY = 40;
	
	public void setText(String text) {
		msg = "Liberou " + text + "!";
		ativar();
	}
	
	public void pintarMsg(BufferedImage imagemParaPintar) {
		if (isAtivado) {
			Graphics2D g = (Graphics2D) imagemParaPintar.getGraphics();
			g.setColor(Color.BLACK);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g.setFont(new Font("Press Start 2P", Font.BOLD, 15));
			if (msg.length() > 20) { 
				g.setFont(new Font("Press Start 2P", Font.BOLD, 13));
				g.drawString(msg, 20, (int)y);
			} else {
				g.drawString(msg, 60, (int)y);
			}
			
		}
		
	}
	
	private void updateY() {
		double yVar = Y_VARIAÇAO / (FADE_DURAÇAO / DELAY);
		if (isFadingIn) {
			y -= yVar;
		}
		
		if (isFadingOut) {
			y += yVar;
		}
	}
	
	private void updateAlpha() {
		if (timer >= DURAÇAO - FADE_DURAÇAO) {
			isFadingOut = true;
		}
		
		if (timer >= FADE_DURAÇAO) {
			isFadingIn = false;
		}	

		float alphaVar = (float) 1 / (FADE_DURAÇAO / DELAY);
		if (isFadingIn) {
			alpha += alphaVar;
			if (alpha >= 1) {
				alpha = 1.0f;
			}
		} 
		
		if (isFadingOut) {
			alpha -= alphaVar;
			if (alpha <= 0) {
				alpha = 0.0f;
			}
		}
	}
	
	private void updateTimer() {
		timer += DELAY;
		if (timer >= DURAÇAO) isAtivado = false;
	}
	
	private void ativar() {
		isAtivado = true;
		new Thread(this).start();
	}
	
	private void resetar() {
		isFadingIn = true;
		timer = 0;
		isFadingOut = false;
		alpha = 0.0f;
	}


	public void run() {
		while (isAtivado) {
			updateAlpha();
			updateY();
			updateTimer();
			
			
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		resetar();
	}
	
}
