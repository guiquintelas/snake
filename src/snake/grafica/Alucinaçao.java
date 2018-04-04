package snake.grafica;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import snake.principal.Cell;
import snake.principal.Principal;

//esse sistema funciona com um JLabel com um gif de icone dentro de um JLabel q tem seu paint reinscrito para
//alterar seus niveis alpha
//sem usar esse sistema as gifs começam a piscar descontroladamente

@SuppressWarnings("serial")
public class Alucinaçao extends JLabel implements Runnable {
	public ImageIcon imagem;
	public float alpha = 0;
	private boolean aumentandoAlpha = true;
	public boolean jaAdd = false;
	public boolean ativo = true;
	public double xPosiçao;
	public double yPosiçao;
	private JLabel labelIcon;
	
	//variaveis para movimento
	private boolean isAndando = false;
	private double xVar;
	private double yVar;
	private static final int MOVIMENTO = 150;
	private int velocidade;
	private static final int MAX_VELOCIDADE = 90;
	private static final int MIN_VELOCIDADE = 300;
	

	public Alucinaçao() {
		String[] alus = { "/Alu/cogumeloAlu.gif", "/Alu/cogumeloAlu3.gif", "/Alu/caindoAlu.gif", 
				"/Alu/skateAlu.gif", "/Alu/ovelhaAlu.gif", "/Alu/pokeAlu.gif", "/Alu/fredAlu.gif"};

		int randonAlu = (int) (Math.random() * alus.length);

		try {
			imagem = new ImageIcon(new URL(getClass().getResource(alus[randonAlu]).toString()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		isAndando = false;
		if (alus[randonAlu].matches("/Alu/ovelhaAlu.gif") || alus[randonAlu].matches("/Alu/cogumeloAlu3.gif")) {
			isAndando = true;
			setPontoFinal();
		}

		this.xPosiçao = (int) (Math.random() * (Principal.TAMANHO_GRID * Cell.WIDTH - this.imagem.getIconWidth()));
		this.yPosiçao = 60 + (int) ((Math.random() * (Principal.TAMANHO_GRID * Cell.HEIGHT - this.imagem.getIconHeight())));

		setSize(imagem.getIconWidth(), imagem.getIconHeight());
		setLocation((int)xPosiçao, (int)yPosiçao);

		labelIcon = new JLabel(imagem);
		labelIcon.setBounds(0, 0, imagem.getIconWidth(), imagem.getIconHeight());
		labelIcon.setOpaque(false);
		this.add(labelIcon);
		this.setOpaque(false);

		new Thread(this).start();
	}

	public void paint(Graphics g) {
		synchronized (Cell.class) {
			while(CampoPanel.deformando) {
				try {
					Cell.class.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			super.paint(g2d);
		}
		
	}

	private float updateAlpha() {
		if (aumentandoAlpha && ativo == true) {
			alpha += 0.01f;
		} else {
			alpha -= 0.01f;
		}

		if (alpha >= 1) {
			alpha = 1;
			aumentandoAlpha = false;
		}

		if (alpha <= 0) {
			alpha = 0;
			ativo = false;
		}

		return alpha;
	}
	
	private void update() {
		if (isAndando) {
			xPosiçao -= getXYVar(xVar);
			yPosiçao -= getXYVar(yVar);
			setLocation((int)xPosiçao, (int)yPosiçao);
		}
	}
	
	private void setPontoFinal() {
		xVar = 1 + (int)(Math.random() * MOVIMENTO);
		yVar = MOVIMENTO - xVar;
		setVelocidade();
	}
	
	private double getXYVar(double xyVar) {
		return xyVar / velocidade;
	}
	
	private void setVelocidade() {
		int velocidadeMinima = MIN_VELOCIDADE - (Principal.nivelDrogas * 10);
		int velocidadeMaxima = MAX_VELOCIDADE + (Principal.nivelDrogas * 10);
		int velocidadeFinal  = velocidadeMinima - (int)(Math.random() * velocidadeMaxima);	
		if (velocidadeFinal <= MAX_VELOCIDADE) {
			velocidadeFinal = MAX_VELOCIDADE;
		}
		System.out.println(velocidadeFinal);
		velocidade = velocidadeFinal;
	}

	@Override
	public void run() {
		while (ativo) {
			
			if (!Principal.isPausado) {
				//repaint();
				updateAlpha();
				update();
			} 

			try {
				Thread.sleep(1000 / 60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
