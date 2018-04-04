package snake.grafica;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;

public class BackAlucinaçao {
	private ImageIcon aluBackImage;
	private Timer timerDuraçao;
	private static boolean isAtivado = false;
	private boolean isFadingIn = true;
	private boolean isFadingOut = false;
	private float alpha = 0.0f;
	private static final long DURAÇAO = 5000;
	private static final int FADE_DURAÇAO = 800;
	private static final int DELAY = 40;
	
	public BackAlucinaçao() {
		setRandomGif();
	}
	
	public static boolean isAtivado() {
		return isAtivado;
	}
	
	private void setRandomGif() {
		String[] gifs = {"/Alu/aluFundo.gif", "/Alu/aluFundo2.gif"};
		int randomIndex = (int)(Math.random() * gifs.length);
		aluBackImage = new ImageIcon(getClass().getResource(gifs[randomIndex]));
	}
	
	public void updateBackAlu(BufferedImage imagemParaPintar, ImageObserver io) {
		if (isAtivado) {
			Graphics2D g = (Graphics2D) imagemParaPintar.getGraphics();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
			g.drawImage(aluBackImage.getImage(), 0, 0, io);
		} 
	}
	
	public synchronized float getAlpha() {
		return alpha;
	}
	
	private synchronized void updateAlpha() {
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
	
	public void Ativar() {
		if (!isAtivado) {
			isAtivado = true;
			timerDuraçao = new Timer();
			timerDuraçao.schedule(new TimerTask() {
				public void run() {
					resetar();
					System.out.println("Fim de AluBack");
					
				}
			}, DURAÇAO);
			
			timerDuraçao.schedule(new TimerTask() {		
				public void run() {
					isFadingIn = false;
					System.out.println("normalizo");
					
				}
			}, FADE_DURAÇAO);
			
			timerDuraçao.schedule(new TimerTask() {		
				public void run() {
					isFadingOut = true;
					System.out.println("fading out...");
					
				}
			}, DURAÇAO - FADE_DURAÇAO);
			
			
			timerDuraçao.schedule(new TimerTask() {
				int time = 0;
				public void run() {
					time += DELAY;
					System.out.println(time);
					updateAlpha();
					if (time >= DURAÇAO) this.cancel();
					
				}
			}, 0, DELAY);
		}
	}
	
	public void resetar() {
		if (isAtivado) {
			isAtivado = false;
			isFadingIn = true;
			isFadingOut = false;
			timerDuraçao.cancel();
			setRandomGif();
		}
		
	}
}
