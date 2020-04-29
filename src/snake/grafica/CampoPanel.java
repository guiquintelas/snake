package snake.grafica;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import snake.principal.Cell;
import snake.principal.Principal;

@SuppressWarnings("serial")
public class CampoPanel extends JPanel implements Runnable {

	private Thread thread;
	private boolean rodando = false;
	private Cell[][] grid;
	//imagem q sera montada fora do campo para ser pintada de uma vez sc, evitando flashing de objetos
	private BufferedImage imagemParaPintar;
	private BufferedImage imagemParaPintarInicial;
	private Graphics2D g;
	private BufferedImage imagemGUI;
	private Graphics2D gGUI;
	private boolean imagemParaPintarIniciada;
	private ArrayList<PontosGanhosAnimacao> listaPontos = new ArrayList<PontosGanhosAnimacao>();
	private ArrayList<Alucinacao> listaAlu = new ArrayList<Alucinacao>();
	public DeformacaoTela deformadorDeTela;
	public BackAlucinacao backAlu;
	public MsgCampo msgCampo;

	public CampoPanel(JFrame janela, Cell[][] grid) {
		janela.setContentPane(this);
		this.setOpaque(false);
		this.setLayout(null);
		this.grid = grid;

		//iniciando imagens
		try {
			imagemParaPintarInicial = ImageIO.read(getClass().getResourceAsStream("/Sprites/snakeBack.jpg"));
			imagemParaPintar = ImageIO.read(getClass().getResourceAsStream("/Sprites/snakeBack.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		imagemGUI = new BufferedImage(janela.getWidth(), 60, BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D) imagemParaPintar.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		gGUI = (Graphics2D) imagemGUI.getGraphics();

		//iniciando deformador
		deformadorDeTela = new DeformacaoTela();

		//inciando o controlador de alucinacao de fundo
		backAlu = new BackAlucinacao();

		//iniciar controlador de msg no campo
		msgCampo = new MsgCampo();

		//iniciando thread
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
			rodando = true;
		}

	}

	@Override
	public void run() {

		final int OPTICAL_FPS = 60;

		int fps = 0;
		long timer = System.currentTimeMillis();
		while (rodando) {

			long now = System.currentTimeMillis();

			if (!Principal.isPausado) {
				repintarGUI();
				resetarImagem();
				backAlu.updateBackAlu(imagemParaPintar, this);
				repintarCampoInicio();
				deformarTela();
				manterPontosGanhos();
				updateAlucinacao();
				msgCampo.pintarMsg(imagemParaPintar);

				this.repaint();

				fps++;
				if (System.currentTimeMillis() - timer > 1000) {
					System.out.println("FPS: " + fps);
					fps = 0;
					timer = System.currentTimeMillis();
				}
			}

			//System.out.println(System.currentTimeMillis() - now);
			//System.out.println((1000/OPTICAL_FPS - (System.currentTimeMillis() - now)));
			long tempoDecorrido = System.currentTimeMillis() - now;
			if (tempoDecorrido > 1000 / OPTICAL_FPS) {
				tempoDecorrido = 1000 / OPTICAL_FPS - 4;
			}
			try {
				//atualiza a 60 FPS
				Thread.sleep((1000 / OPTICAL_FPS - tempoDecorrido));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void paint(Graphics g) {
		synchronized (Cell.class) {
			while (deformando && imagemParaPintarIniciada) {
				try {
					Cell.class.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			super.paint(g);
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		synchronized (Cell.class) {
			while (deformando) {
				try {
					Cell.class.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			Graphics2D gTela = (Graphics2D) g;
			gTela.drawImage(imagemParaPintar, 0, 60, this);
			gTela.drawImage(imagemGUI, 0, 0, this);
		}

	}

	//c sincronizado para no caso do metodo parar() for chamado bem na hora q o g tbm esta sendo utilizado para pintar o campo
	public synchronized void parar() {

		this.rodando = false;
		listaPontos.clear();
		for (int x = 0; x < listaAlu.size(); x++) {
			remove(listaAlu.get(x));
		}
		listaAlu.clear();

		g.setColor(Color.BLACK);
		g.setFont(new Font("Press Start 2P", Font.PLAIN, 30));
		g.drawString("GAME OVER", 45, 165);
		g.setFont(new Font("Press Start 2P", Font.PLAIN, 11));
		g.drawString("Pressione SPACE para novo jogo...", 5, 290);
		System.out.println("GAME OVER");
		this.repaint();

	}

	public void start() {
		rodando = true;
		thread = new Thread(this);
		thread.start();

	}

	public void repintarGUI() {

		gGUI.setColor(Color.WHITE);
		gGUI.fillRect(250, 25, 105, 30);

		gGUI.setColor(Color.BLACK);
		gGUI.drawLine(0, 59, imagemParaPintar.getHeight(), 59);

		gGUI.setColor(Color.GRAY);
		gGUI.drawLine(115, 0, 115, 58);
	}

	private void resetarImagem() {
		g.drawImage(imagemParaPintarInicial, 0, 0, this);
	}

	public void repintarCampoInicio() {
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid.length; y++) {

				if (grid[x][y] != null) {

					if (grid[x][y].isFood()) {
						pintarIntegridade(grid[x][y]);

						g.drawImage(grid[x][y].macaSprite.getImage(), grid[x][y].getX(), grid[x][y].getY(), Cell.WIDTH, Cell.HEIGHT, this);

					} else if (grid[x][y].isSnake()) {
						g.drawImage(grid[x][y].snakeSprite, grid[x][y].getX(), grid[x][y].getY(), this);

					}

					//a imagem foi pintada pelo menos uma vez ja
					imagemParaPintarIniciada = true;

				}

			}
		}

	}

	private void pintarIntegridade(Cell comidaCell) {
		gGUI.drawImage(comidaCell.macaSprite.getImage(), 335, 35, Cell.WIDTH, Cell.HEIGHT, this);

		//ajusta a string no lugar certo
		int xTexto = 262;
		if (Cell.intregridade < 100) {
			xTexto += 18;
			if (Cell.intregridade < 10) {
				xTexto += 18;
			}

		}

		gGUI.setColor(Color.BLACK);
		gGUI.setFont(new Font("Press Start 2P", Font.PLAIN, 18));
		gGUI.drawString((int) Cell.intregridade + "%", xTexto, 55);

	}

	public synchronized void pintarPausado() {
		resetarImagem();
		repintarGUI();
		repintarCampoInicio();

		g.setColor(Color.BLACK);
		g.setFont(new Font("Press Start 2P", Font.PLAIN, 30));
		g.drawString("PAUSADO", 70, 65);

		//lista de destravamentos
		g.setFont(new Font("Press Start 2P", Font.BOLD, 12));
		g.drawString("Itens para destravar:", 40, 100);

		//destravamento do chips
		g.setFont(new Font("Press Start 2P", Font.PLAIN, 10));
		g.drawString("Chips................", 25, 130);

		if (Principal.isChipsUn) {
			g.drawString("LIBERADO", 235, 130);
		} else {
			g.drawString(Principal.CHIPS_UN + " Pontos", 235, 130);
		}

		//destravamento musica
		g.drawString("Musica...............", 25, 150);

		if (Principal.isMusicaUn) {
			g.drawString("LIBERADO", 235, 150);
		} else {
			g.drawString(Principal.MUSICA_UN + " Pontos", 235, 150);
		}

		//destravamento do xTreme
		g.drawString("Modo Xtreme..........", 25, 170);

		if (Principal.isXtremeUn) {
			g.drawString("LIBERADO", 235, 170);
		} else {
			g.drawString(Principal.XTREME_UN + " Pontos", 235, 170);
		}

		//destravamento do modo velocidade
		g.drawString("Modo Velocidade......", 25, 190);

		if (Principal.isVelocidadeAumentandoUn) {
			g.drawString("LIBERADO", 235, 190);
		} else {
			g.drawString(Principal.VELOCIDADE_AUMENTANDO_UN + " Pontos", 235, 190);
		}

		//destravamento modo Alucinacoes

		g.drawString("Modo Alucinógenos....", 25, 210);

		if (Principal.isDrogasUn) {
			g.drawString("LIBERADO", 235, 210);
		} else {
			g.drawString(Principal.DROGAS_UN + " Pontos", 235, 210);
		}
		//fim de lista de destravamentos

		g.setFont(new Font("Press Start 2P", Font.PLAIN, 11));
		g.drawString("Pressione SPACE para continuar...", 5, 290);

		this.repaint();
	}

	public void criarPontosGanhos(int pontosGanhos, Cell lugarDaComida) {
		listaPontos.add(new PontosGanhosAnimacao(Integer.toString(pontosGanhos), lugarDaComida.getX(), lugarDaComida.getY(), Principal.chipsAtivo));
		System.out.println("criou animacao de pontos ganhos");
	}

	public void manterPontosGanhos() {

		for (int x = 0; x < listaPontos.size(); x++) {
			if (!listaPontos.get(x).isAtivo()) {

				remove(listaPontos.get(x));
				listaPontos.remove(x);

			} else if (!listaPontos.get(x).jaAdd) {
				add(listaPontos.get(x));
				listaPontos.get(x).jaAdd = true;
			}
		}
	}

	public void updateAlucinacao() {
		if (listaAlu.size() < Principal.nivelDrogas / 3) {
			listaAlu.add(new Alucinacao());
			System.out.println("criou alucinacao");
		}

		for (int x = 0; x < listaAlu.size(); x++) {
			if (listaAlu.get(x).ativo) {
				if (!listaAlu.get(x).jaAdd) {
					this.add(listaAlu.get(x));
					listaAlu.get(x).jaAdd = true;
				}

			} else {

				this.remove(listaAlu.get(x));
				listaAlu.remove(x);
			}
		}
	}

	public static boolean deformando = false;

	public synchronized void deformarTela() {
		deformando = true;
		synchronized (Cell.class) {

			deformadorDeTela.updateStatus();

			//BufferedImage imagemDeformada = deformadorDeTela.animacaoDeformar(imagemParaPintar);

			g.drawImage(deformadorDeTela.animacaoDeformar(imagemParaPintar), 0, 0, this);

			Cell.class.notify();
		}
		deformando = false;
	}

}
