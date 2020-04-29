package snake.grafica;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;

public class BackAlucinacao {
	private ImageIcon aluBackImage;
	private Timer timerDuracao;
	private static boolean isAtivado = false;
	private boolean isFadingIn = true;
	private boolean isFadingOut = false;
	private float alpha = 0.0f;
	private static final long DURAcAO = 5000;
	private static final int FADE_DURAcAO = 800;
	private static final int DELAY = 40;
	
	public BackAlucinacao() {
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
		float alphaVar = (float) 1 / (FADE_DURAcAO / DELAY);
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
			timerDuracao = new Timer();
			timerDuracao.schedule(new TimerTask() {
				public void run() {
					resetar();
					System.out.println("Fim de AluBack");
					
				}
			}, DURAcAO);
			
			timerDuracao.schedule(new TimerTask() {
				public void run() {
					isFadingIn = false;
					System.out.println("normalizo");
					
				}
			}, FADE_DURAcAO);
			
			timerDuracao.schedule(new TimerTask() {
				public void run() {
					isFadingOut = true;
					System.out.println("fading out...");
					
				}
			}, DURAcAO - FADE_DURAcAO);
			
			
			timerDuracao.schedule(new TimerTask() {
				int time = 0;
				public void run() {
					time += DELAY;
					System.out.println(time);
					updateAlpha();
					if (time >= DURAcAO) this.cancel();
					
				}
			}, 0, DELAY);
		}
	}
	
	public void resetar() {
		if (isAtivado) {
			isAtivado = false;
			isFadingIn = true;
			isFadingOut = false;
			timerDuracao.cancel();
			setRandomGif();
		}
		
	}
}
