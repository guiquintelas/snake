package snake.grafica;

import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import com.jhlabs.image.*;

import snake.principal.Principal;

public class DeformaçaoTela implements Runnable {
	private boolean ativo = false;
	private BufferedImage imagemRodada;
	private double angulo;
	private float raio = 100;
	private float quantidade = 1;
	private boolean isDesfazendo = false;
	private int timer = 1;
	//FILTROS
	private InvertFilter invert;
	private TwirlFilter twirl;
	private PinchFilter pinch;
	private SwimFilter swim;
	private EmbossFilter emboss;

	//armazenament de variaveis 
	private int oldNivelDrogas = 0;

	//thread
	private Thread thread;

	//boleanas para checar qual filtro esta ativo
	private boolean isTwirl = false;
	private boolean isInvert = false;
	private boolean isPinch = false;
	private boolean isSwim = false;
	private boolean isEmboss = false;
	
	//constantes
	private float x;
	private float y;
	private int timerSwim = 1;
	private boolean isFlashingAtivado = false;
	

	public DeformaçaoTela() {
		this.ativo = false;

		invert = new InvertFilter();
		twirl = new TwirlFilter();
		pinch = new PinchFilter();
		swim = new SwimFilter();
		emboss = new EmbossFilter();
	}

	public void updateStatus() {
		if (Principal.nivelDrogas % 4 == 0 && Principal.nivelDrogas >= 4 && !ativo) {
			//elimina a possibilidade da deformaçao se repetir no msm nivel de drogas
			if (Principal.nivelDrogas == oldNivelDrogas) {
				return;
			}

			oldNivelDrogas = Principal.nivelDrogas;

			System.out.println("Deformaçao ativada");
			
			setRandomFilter();
			
			this.x = getRandomXY();
			this.y = getRandomXY();
			
			angulo = 0;
			raio = 100;
			quantidade = 1;
			timer = 1;
			isDesfazendo = false;

			if (thread == null) {
				ativo = true;
				thread = new Thread(this);
				thread.start();
			}

		}
		
		if (isTwirl) {
			twirl.setCentreX(x);
			twirl.setCentreY(y);
			twirl.setRadius(raio);
			twirl.setAngle((float) Math.toRadians(angulo));
		}
		
		if (isPinch) {
			pinch.setCentreX(x);
			pinch.setCentreY(y);
			pinch.setAmount(-0.3f);
			pinch.setRadius(raio);
			pinch.setAngle((float) Math.toRadians(angulo));
		}
		
		if (isSwim) {
			swim.setScale(100.0f);
			swim.setAmount(quantidade);
			swim.setTime(timerSwim);
		}
		
	}
	
	public BufferedImage animaçaoDeformar(BufferedImage imagemADeformar) {
		
		if (Principal.isPcMerda) return imagemADeformar;
		
		if (isFlashingAtivado) {
			int random = 1 + (int)(Math.random() * 10);
			if (random >= 4) {
				if (isInvert) invert.filter(imagemADeformar, imagemADeformar);
				if (isEmboss) emboss.filter(imagemADeformar, imagemADeformar);
			}
					
		}
		
		if (BackAlucinaçao.isAtivado()) {
			this.ativo = false;
		}
		
		if (ativo) {
			if (isTwirl) twirl.filter(imagemADeformar, imagemADeformar);
			if (isPinch) pinch.filter(imagemADeformar, imagemADeformar);
			if (isSwim) swim.filter(imagemADeformar, imagemADeformar);
			
			

			return imagemRodada;
		}
		return imagemADeformar;
	}


	public void setFlashing(boolean isFlashingAtivado) {
		
		int randomFilter = (int)(Math.random() * 2);

		if (randomFilter == 0) isInvert = true;
		if (randomFilter == 1) isEmboss = true;
		
		if (!this.isFlashingAtivado) {
			Timer flashingTimer = new Timer();
			flashingTimer.schedule(new TimerTask() {			
				@Override
				public void run() {
					DeformaçaoTela.this.isFlashingAtivado = false;
					isInvert = false;
					isEmboss = false;
					
				}
			}, 1500);
		}
		this.isFlashingAtivado = isFlashingAtivado;
		
		
	}

	

	private void animarTwirl() {
		animarAngulo();
		animarRaio();
	}
	
	private void animaPinch() {
		animarRaio();
		animarAngulo();
	}
	
	private void animaSwim() {
		updateTimerSwim();
		animaQuantidade();
	}
	
	private void updateTimerSwim() {
		if (timer % 1 == 0) {
			timerSwim += 0.8;
		}
	}

	private void updateTimer() {
		timer++;
		if (timer > 60) {
			isDesfazendo = true;
		}
		
		if (timer >= 120) {
			ativo = false;
		}
	}
	
	private void setRandomFilter() {
		isTwirl = false;
		isPinch = false;
		
		int random = 1 + (int)(Math.random() * 3);
		
		if (random == 1) isTwirl = true;
		if (random == 2) isPinch = true;
		if (random == 3) isSwim = true;
	}
	
	private float getRandomXY() {
		return 0.3f + (float)(Math.random() * 4) / 10;
	}
	
	

	private double getAnguloVar() {
		return Principal.nivelDrogas / 8 + 1;
	}

	private void animarAngulo() {
		if (!isDesfazendo) {
			if (timer >= 30) {
				angulo += getAnguloVar();
			} else {
				angulo -= getAnguloVar();
			}
		} else {
			if (timer >= 90) {
				angulo -= getAnguloVar();
			} else {
				angulo += getAnguloVar();
			}
		}

	}

	private void animarRaio() {
		if (!isDesfazendo) {
			raio += 3;

		} else {
			raio -= 3;
			if (raio <= 100) {
				raio = 100;
			}
		}
	}
	
	private float getQuantidadeVar() {
		return (float) Principal.nivelDrogas / 50 + 0.3f;
	}
	
	private void animaQuantidade() {
		if (!isDesfazendo) {
			quantidade += getQuantidadeVar();

		} else {
			quantidade -= getQuantidadeVar();
		}
	}

	@Override
	public void run() {
		System.out.println("inicio thread");
		

		while (ativo) {

			while (!Principal.isPausado && ativo) {

				if (Principal.gameOver) {
					ativo = false;
				}
				
				updateTimer();
				
				if (isTwirl) animarTwirl();
				if (isPinch) animaPinch();
				if (isSwim) animaSwim();

				try {
					Thread.sleep(1000 / 30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}

		thread = null;
		System.out.println("fim de deformaçao");

	}

}
