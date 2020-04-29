package snake.principal;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;

import snake.grafica.CampoPanel;
import snake.som.AluAudio;
import snake.som.Audio;
import snake.som.BackgroundAudio;
import snake.som.ChipsAudio;
import snake.som.EatAluAudio;
import snake.som.MacaAudio;
import snake.som.RandomAudio;

public class Principal implements Runnable {
	
	

	public static boolean gameOver = false;
	public static boolean isPausado = true;
	public static boolean isPcMerda = true;

	public static int tamanhoSnake = 4;
	private int score;
	private int highScore = 0;
	private int direcao = 1;
	public static int nivelDrogas = 0;

	public static final int TAMANHO_GRID = 18;
	private Cell grid[][] = new Cell[TAMANHO_GRID][TAMANHO_GRID];
	public LinkedList<Cell> cobraArray = new LinkedList<Cell>();

	private JFrame janela = new JFrame("Snake 1.9.9.5");
	private JLabel labelScore;
	private JLabel labelTamanho;
	private JLabel labelHighScore;

	//botoes de velocidade
	JRadioButton velLento;
	JRadioButton velNormal;
	JRadioButton velRapido;
	JRadioButton velXtreme;
	
	//check boxes de modos de jogo
	JCheckBox modoVelocidadeAumentandoBox;
	JCheckBox modoDrogasBox;
	JCheckBox modoMusicaBox;

	//velocidades
	private int velocidadeAtual = NORMAL; // padrao
	public static final int LENTO = 125;
	public static final int NORMAL = 100;
	public static final int RAPIDO = 75;
	public static final int XTREME = 50;

	//constantes para destravar
	public static final int XTREME_UN = 4000;
	public static final int VELOCIDADE_AUMENTANDO_UN = 5250;
	public static final int CHIPS_UN = 2000;
	public static final int DROGAS_UN = 6000;
	public static final int MUSICA_UN = 3000;

	//stats de destravar
	public static boolean isVelocidadeAumentandoUn = false;
	public static boolean isXtremeUn = true;
	public static boolean isChipsUn = true;
	public static boolean isDrogasUn = true;
	public static boolean isMusicaUn = true;

	//modo de jogo
	public static boolean gameModeVelocidadeAumentando = false;
	public static boolean gameModeDrogas = false;
	public static boolean chipsAtivo = false;
	public static boolean musicaAtiva = true;

	//tela onde c pintado o jogo
	CampoPanel campoPanel;

	//Audios
	RandomAudio somComendo1 = new MacaAudio("/Sons/comer1.wav");
	RandomAudio somComendo2 = new MacaAudio("/Sons/comer2.wav");
	RandomAudio somComendo3 = new MacaAudio("/Sons/comer3.wav");
	RandomAudio somComendo4 = new MacaAudio("/Sons/comer4.wav");
	RandomAudio comerChips1 = new ChipsAudio("/Sons/comerChips1.wav");
	RandomAudio comerChips2 = new ChipsAudio("/Sons/comerChips2.wav");
	RandomAudio aluAudio = new AluAudio("/Sons/alu1.wav");
	RandomAudio aluAudio2 = new AluAudio("/Sons/alu2.wav");
	RandomAudio aluAudio3 = new AluAudio("/Sons/alu3.wav");
	RandomAudio aluAudio4 = new AluAudio("/Sons/alu4.wav");
	RandomAudio eatAluAudio = new EatAluAudio("/Sons/bang.wav");
	RandomAudio eatAluAudio2 = new EatAluAudio("/Sons/call991.wav");
	RandomAudio getLuckyBack = new BackgroundAudio("/Sons/getLuckyBack.wav");
	RandomAudio thriftShopBack = new BackgroundAudio("/Sons/thriftShopBack.wav");
	Audio aplauso = new Audio("/Sons/clap.wav");
	Audio gameOverAudio = new Audio("/Sons/gameOver.wav");
	Audio pausarAudio = new Audio("/Sons/pausar.wav");
	Audio despausarAudio = new Audio("/Sons/despausar.wav");
	
	//construtor
	public Principal() {
		Cell.carregarBitmapCobra();
	}

	//Carregando Fontes
	private void carregarFonte(String fontName) throws FontFormatException, IOException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream(fontName)));
	}

	// correcao do bug onde se voce mudase a direcao duas vezes antes do update
	// do campo a cobra poderia andar para dentro de si
	private boolean direcaoJaMudada = false;

	private void criarJanela() {
		janela.setVisible(true);
		janela.setSize(365, 445);
		janela.setLocationRelativeTo(null);
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		janela.setResizable(false);
		janela.setLayout(null);
		janela.setBackground(Color.WHITE);
		janela.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (isPausado) {
						if (!gameOver) ((BackgroundAudio) getLuckyBack).resume();
						isPausado = false;
						despausarAudio.play();
					} else {
						((BackgroundAudio) getLuckyBack).pause();
						isPausado = true;
						pausarAudio.play();
					}

					if (gameOver == true) {
						novoJogo();
					}
				}

				if (e.getKeyCode() == KeyEvent.VK_LEFT) {

					if (direcao != 3 && !direcaoJaMudada) {
						direcao = 1;
						direcaoJaMudada = true;
					}
				}

				if (e.getKeyCode() == KeyEvent.VK_UP) {
					if (direcao != 4 && !direcaoJaMudada) {
						direcao = 2;
						direcaoJaMudada = true;
					}
				}

				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (direcao != 1 && !direcaoJaMudada) {
						direcao = 3;
						direcaoJaMudada = true;
					}
				}

				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if (direcao != 2 && !direcaoJaMudada) {
						direcao = 4;
						direcaoJaMudada = true;
					}
				}

			}
		});

		campoPanel = new CampoPanel(janela, grid);

		labelScore = new JLabel();
		labelScore.setText("Pontos: " + score);
		labelScore.setBounds(5, 20, 100, 20);
		janela.getContentPane().add(labelScore);
		labelScore.repaint();

		labelTamanho = new JLabel();
		labelTamanho.setText("Tamanho: " + tamanhoSnake);
		labelTamanho.setBounds(5, 0, 100, 20);
		janela.getContentPane().add(labelTamanho);

		labelHighScore = new JLabel();
		labelHighScore.setText("High Score: " + highScore);
		labelHighScore.setBounds(5, 40, 150, 20);
		janela.getContentPane().add(labelHighScore);

		velLento = new JRadioButton("Lento");
		velLento.setBounds(116, 0, 80, 15);
		velLento.setOpaque(false);
		velLento.setFocusable(false);
		janela.getContentPane().add(velLento);
		velLento.repaint();

		velNormal = new JRadioButton("Normal");
		velNormal.setBounds(116, 15, 80, 15);
		velNormal.setOpaque(false);
		velNormal.setSelected(true);
		velNormal.setFocusable(false);
		janela.getContentPane().add(velNormal);
		velNormal.repaint();

		velRapido = new JRadioButton("Rápido");
		velRapido.setBounds(116, 30, 80, 15);
		velRapido.setOpaque(false);
		velRapido.setFocusable(false);
		janela.getContentPane().add(velRapido);
		velRapido.repaint();

		velXtreme = new JRadioButton("Xtreme");
		velXtreme.setBounds(116, 45, 80, 14);
		velXtreme.setOpaque(false);
		velXtreme.setFocusable(false);
		janela.getContentPane().add(velXtreme);
		velXtreme.repaint();

		ButtonGroup vel = new ButtonGroup();
		vel.add(velLento);
		vel.add(velNormal);
		vel.add(velRapido);
		vel.add(velXtreme);
		
		modoVelocidadeAumentandoBox = new JCheckBox("Velocidade Aumentada");
		modoVelocidadeAumentandoBox.setBounds(200, -3, 180, 20);
		modoVelocidadeAumentandoBox.setOpaque(false);
		modoVelocidadeAumentandoBox.setFocusable(false);
		janela.getContentPane().add(modoVelocidadeAumentandoBox);
		modoVelocidadeAumentandoBox.repaint();
		modoVelocidadeAumentandoBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (modoVelocidadeAumentandoBox.isSelected()) {
					gameModeVelocidadeAumentando = true;
				} else {
					gameModeVelocidadeAumentando = false;
				}							
			}
		});
		
		modoDrogasBox = new JCheckBox("Alucincgenos");
		modoDrogasBox.setBounds(200, 15, 180, 20);
		modoDrogasBox.setOpaque(false);
		modoDrogasBox.setFocusable(false);
		janela.getContentPane().add(modoDrogasBox);
		modoDrogasBox.repaint();
		modoDrogasBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (modoDrogasBox.isSelected()) {
					final JFrame janelaAviso = new JFrame("Aviso");
					janelaAviso.setVisible(true);
					janelaAviso.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					janelaAviso.requestFocus();
					janelaAviso.setSize(250, 100);
					janelaAviso.setLocationRelativeTo(null);
					janelaAviso.setLayout(null);
					janela.setEnabled(false);
					
					JTextPane msg = new JTextPane();
					msg.setBounds(30, 10, 250, 20);
					msg.setText("O seu PC c uma MERDA?");
					msg.setBackground(janelaAviso.getContentPane().getBackground());
					msg.setEditable(false);
					janelaAviso.add(msg);
					
					JButton botaoSim = new JButton("Sim");
					botaoSim.setBounds(5, 40, 60, 20);
					janelaAviso.add(botaoSim);
					botaoSim.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							janela.setEnabled(true);
							modoDrogasBox.setSelected(true);	
							gameModeDrogas = true;	
							janelaAviso.dispose();						
							janela.requestFocus();
							isPcMerda = true;
						}
					});
					
					JButton botaoNao = new JButton("Nao");
					botaoNao.setBounds(170, 40, 60, 20);
					janelaAviso.add(botaoNao);
					botaoNao.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {							
							janela.setEnabled(true);
							modoDrogasBox.setSelected(true);
							gameModeDrogas = true;	
							janelaAviso.dispose();	
							janela.requestFocus();
							isPcMerda = false;
						}
					});
					
					modoDrogasBox.setSelected(false);
					
				} else {
					gameModeDrogas = false;
				}
				
			}
		});
		
		modoMusicaBox = new JCheckBox("Music");
		modoMusicaBox.setOpaque(false);
		modoMusicaBox.setFocusable(false);
		modoMusicaBox.setBounds(200, 35, 80, 20);
		janela.getContentPane().add(modoMusicaBox);
		modoMusicaBox.repaint();
		modoMusicaBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (modoMusicaBox.isSelected()) {
					musicaAtiva = true;
					if (!isPausado) tocarBackground();
					
				} else {
					musicaAtiva = false;
					tocarBackground();
				}
			}
		});
	}

	private void criarCampo() {
		System.out.println("criou campo");
		//janela.repaint();

		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid.length; y++) {
				grid[x][y] = new Cell(x, y);
			}
		}
	}

	private void novoJogo() {

		gameOver = false;
		isPausado = true;

		score = 0;
		tamanhoSnake = 4;
		direcao = 1;
		nivelDrogas = 0;

		cobraArray.clear();

		labelScore.setText("Pontos: " + score);
		labelTamanho.setText("Tamanho: " + tamanhoSnake);

		criarCampo();
		criarCobra();
		criarFood();

		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid.length; y++) {
				grid[x][y].atualiza();
			}
		}

		campoPanel.start();

		new Thread(this).start();
		janela.requestFocus();
	}

	public void checarDestrava() {
		if (highScore >= XTREME_UN && isXtremeUn == false) {
			isXtremeUn = true;
			aplauso.play();
			campoPanel.msgCampo.setText("XTreme");
		}
		
		if (highScore >= VELOCIDADE_AUMENTANDO_UN && isVelocidadeAumentandoUn == false) {
			isVelocidadeAumentandoUn = true;
			aplauso.play();
			campoPanel.msgCampo.setText("Modo Velocidade");
		}
		
		if (highScore >= CHIPS_UN && isChipsUn == false) {
			isChipsUn = true;
			aplauso.play();
			campoPanel.msgCampo.setText("Chips");
		}
		
		if (highScore >= DROGAS_UN && isDrogasUn == false) {
			isDrogasUn = true;
			aplauso.play();
			campoPanel.msgCampo.setText("Drogas");
		}
		
		if (highScore >= MUSICA_UN && isMusicaUn == false) {
			isMusicaUn = true;
			musicaAtiva = true;
			modoMusicaBox.setEnabled(true);
			modoMusicaBox.setSelected(true);
			tocarBackground();
			aplauso.play();
			campoPanel.msgCampo.setText("Musica");
		}
		
	}
	
	public void checarModos() {
		if (gameModeVelocidadeAumentando) {
			velocidadeAtual -= 2;
			System.out.println("aumento a velocidade");
		}
	}
	
	private void tocarBackground() {
		getLuckyBack.play();
	}

	private void criarCobra() {
		int tamanho = 1;
		for (int x = grid.length / 2; x < grid.length / 2 + tamanhoSnake; x++) {

			cobraArray.add(grid[x][grid.length / 2]);
			grid[x][grid.length / 2].setSnake(true, tamanho);
			System.out.println("tamanho array: " + cobraArray.size());
			tamanho++;
		}
		
		atualizaCobra();

	}

	private void moverCobra() {

		if (direcao == 1) {
			int x = cobraArray.getFirst().xPosition;
			int y = cobraArray.getFirst().yPosition;

			if ((x - 1) != -1) {

				if (grid[x - 1][y].isSnake()) {
					gameOver = true;
					System.out.println("Bateu em uma cobra, seu safado");
					return;
				}

				if (grid[x - 1][y].isFood()) {
					comerFood(grid[x - 1][y]);
				}

				grid[x - 1][y].setSnakeCabeca(direcao);
				moverResto(grid[x - 1][y]);

				return;

			} else {
				gameOver = true;
				System.out.println("Perdeu");
			}
			return;
		}

		if (direcao == 2) {
			int x = cobraArray.getFirst().xPosition;
			int y = cobraArray.getFirst().yPosition;

			if ((y - 1) != -1) {

				if (grid[x][y - 1].isSnake()) {
					gameOver = true;
					System.out.println("Bateu em uma cobra, seu safado");
					return;
				}

				if (grid[x][y - 1].isFood()) {
					comerFood(grid[x][y - 1]);
				}

				grid[x][y - 1].setSnakeCabeca(direcao);
				moverResto(grid[x][y - 1]);
				return;

			} else {

				gameOver = true;
				System.out.println("Perdeu");
			}
			return;

		}

		if (direcao == 3) {
			int x = cobraArray.getFirst().xPosition;
			int y = cobraArray.getFirst().yPosition;

			if ((x + 1) != grid.length) {

				if (grid[x + 1][y].isSnake()) {
					gameOver = true;
					System.out.println("Bateu em uma cobra, seu safado");
					return;
				}

				if (grid[x + 1][y].isFood()) {
					comerFood(grid[x + 1][y]);
				}

				grid[x + 1][y].setSnakeCabeca(direcao);
				moverResto(grid[x + 1][y]);
				return;

			} else {
				gameOver = true;
				System.out.println("Perdeu");
			}
			return;
		}

		if (direcao == 4) {
			int x = cobraArray.getFirst().xPosition;
			int y = cobraArray.getFirst().yPosition;

			if ((y + 1) != grid.length) {

				if (grid[x][y + 1].isSnake()) {
					gameOver = true;
					System.out.println("Bateu em uma cobra, seu safado");
					return;
				}

				if (grid[x][y + 1].isFood()) {
					comerFood(grid[x][y + 1]);
				}

				grid[x][y + 1].setSnakeCabeca(direcao);
				moverResto(grid[x][y + 1]);
				return;

			} else {
				gameOver = true;
				System.out.println("Perdeu");
			}

		}

	}

	private void moverResto(Cell cabecaNova) {
			cobraArray.addFirst(cabecaNova);

			if (cobraArray.size() != tamanhoSnake) {
				cobraArray.getLast().setSnake(false, 0);
				cobraArray.getLast().atualiza();
				cobraArray.removeLast();
			}
			
			atualizaCobra();
	}
	
	public void atualizaCobra() {
		for (int x = 0; x < cobraArray.size(); x++) {
			cobraArray.get(x).setSnake(true, x + 1);
			
			//para o rabo sempre apontar para a direcao oposta ao penultimo segmento de cobra
			if (x == cobraArray.size() - 1) {
				cobraArray.get(x).orientacao = cobraArray.get(x - 1).orientacao;
				cobraArray.get(x).setCorpo(false);
				cobraArray.get(x).atualiza();
				break;
			}
			
			
			if (x > 0 && x < cobraArray.size() - 1 && cobraArray.get(x - 1).orientacao == cobraArray.get(x).orientacao)  {
				cobraArray.get(x).setCorpo(true);
				cobraArray.get(x).atualiza();
				continue;
			}
			
			if (x > 0 && x < cobraArray.size() - 1 && cobraArray.get(x - 1).orientacao != cobraArray.get(x).orientacao)  {
				if (cobraArray.get(x - 1).orientacao == 4 && cobraArray.get(x).orientacao == 1 || cobraArray.get(x - 1).orientacao == 3 && cobraArray.get(x).orientacao == 2) {
					cobraArray.get(x).setCurva(0);
					cobraArray.get(x).atualiza();
					continue;
				}
				
				if (cobraArray.get(x - 1).orientacao == 4 && cobraArray.get(x).orientacao == 3 || cobraArray.get(x - 1).orientacao == 1 && cobraArray.get(x).orientacao == 2) {
					cobraArray.get(x).setCurva(1);
					cobraArray.get(x).atualiza();
					
					continue;
				}
				
				if (cobraArray.get(x - 1).orientacao == 1 && cobraArray.get(x).orientacao == 4 || cobraArray.get(x - 1).orientacao == 2 && cobraArray.get(x).orientacao == 3) {
					cobraArray.get(x).setCurva(2);
					cobraArray.get(x).atualiza();
					continue;
				}
				
				if (cobraArray.get(x - 1).orientacao == 2 && cobraArray.get(x).orientacao == 1 || cobraArray.get(x - 1).orientacao == 3 && cobraArray.get(x).orientacao == 4) {
					cobraArray.get(x).setCurva(3);
					cobraArray.get(x).atualiza();
					continue;
				}
				
			}
			
			
			
			
			cobraArray.get(x).atualiza();
		}
	}

	private void criarFood() {
		int randonX;
		int randonY;
		int probabilidadeChips;
		chipsAtivo = false;
		
		while (true) {
			probabilidadeChips = 1 + (int) (Math.random() * 100);
			randonX = (int) (Math.random() * 18);
			randonY = (int) (Math.random() * 18);

			if (grid[randonX][randonY].isCampo()) {
				
				if (probabilidadeChips <= 5 && isChipsUn) {
					chipsAtivo = true;
				}
				
				System.out.println("X: " + randonX);
				System.out.println("Y: " + randonY);
				break;
			}
		}
		grid[randonX][randonY].setFood(true);
		grid[randonX][randonY].atualiza();
		System.out.println("nova comida add no campo");

	}

	private void comerFood(Cell cellDaComida) {
		//tocar som 	
		if (chipsAtivo) {
			comerChips1.play();
		} else {
			somComendo1.play();
		}
		
		//aumentar nivel de alucinacao
		if (gameModeDrogas) {
			nivelDrogas++;
			
			if (nivelDrogas % 4 == 0 && nivelDrogas >= 3) {
				aluAudio.play();
			}
			
			if (nivelDrogas % 6 == 0 && nivelDrogas >= 12) {
				campoPanel.backAlu.Ativar();
			}
			
			if (nivelDrogas % 3 == 0 && nivelDrogas >= 6) {
				eatAluAudio.play();
				campoPanel.deformadorDeTela.setFlashing(true);
			}
		}
		

		//aumentando os pontos
		score += (tamanhoSnake - 3) * cellDaComida.pontos() * bonusPontos();
		labelScore.setText("Pontos: " + score);
		
		//aumentoando maior pontuacco
		if (score > highScore) {
			highScore = score;
		}
		labelHighScore.setText("High Score: " + highScore);
		
		//checando para ver se o modo de velocidade esta ativado
		checarModos();

		//pintando pontos ganhos na tela
		campoPanel.criarPontosGanhos((int) ((tamanhoSnake - 3) * cellDaComida.pontos() * bonusPontos()), cellDaComida);

		tamanhoSnake++;
		labelTamanho.setText("Tamanho: " + tamanhoSnake);

		criarFood();
		cellDaComida.setFood(false);
	}

	private double bonusPontos() {
		double velocidade = (velocidadeAtual - 125) * (-1);
		
		double pontos = 0.5 + (0.5 * velocidade / 25);
		
		
		if (gameModeVelocidadeAumentando) {
			pontos += 0.5;
		}
		
		if (gameModeDrogas) {
			pontos += 0.5;
		}
		System.out.println("formula pontos: " + pontos);
		return pontos;
	}
	
	private void iniciarFinalizar(boolean isIniciar) {
		if (isIniciar) {
			
			if (velLento.isSelected()) {
				velocidadeAtual = LENTO;
			}
			
			if (velNormal.isSelected()) {
				velocidadeAtual = NORMAL;
			}
			
			if (velRapido.isSelected()) {
				velocidadeAtual = RAPIDO;
			}
			
			if (velXtreme.isSelected()) {
				velocidadeAtual = XTREME;
			}
			
			velLento.setEnabled(false);
			velNormal.setEnabled(false);
			velRapido.setEnabled(false);
			velXtreme.setEnabled(false);
		
			modoVelocidadeAumentandoBox.setEnabled(false);
			modoDrogasBox.setEnabled(false);
			modoMusicaBox.setEnabled(false);
			
			//para poder desativar a musica dentro do jogo
			if (isMusicaUn) {
				modoMusicaBox.setEnabled(true);
			}
			
		} else {
			velLento.setEnabled(true);
			velNormal.setEnabled(true);
			velRapido.setEnabled(true);
			if (isXtremeUn) {
				velXtreme.setEnabled(true);
			}
			
			if (isVelocidadeAumentandoUn) {
				modoVelocidadeAumentandoBox.setEnabled(true);
			}			
			
			if (isDrogasUn) {
				modoDrogasBox.setEnabled(true);
			}
			
			if (isMusicaUn) {
				modoMusicaBox.setEnabled(true);
			}
		}
	}

	@Override
	public synchronized void run() {	
		iniciarFinalizar(true);

		
		while (!gameOver) {

			campoPanel.pintarPausado();

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			while (!gameOver && !isPausado) {
				direcaoJaMudada = false;
				
				moverCobra();
				checarDestrava();		
				tocarBackground();

				try {
					Thread.sleep(velocidadeAtual);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//esta aqui para comecar no 100, e nao 99
				Cell.atualizaIntegridade();
			}
			
			
			
		}
		//pausa a backMusic
		((BackgroundAudio) getLuckyBack).resetar();
		
		//para alucinacao do fundo
		campoPanel.backAlu.resetar();
		
		//toca o som de game over 
		gameOverAudio.play();

		//para a tela de atualizar
		campoPanel.parar();

		iniciarFinalizar(false);

	}

	public static void main(String[] args) {
		Principal main = new Principal();
		try {
			main.carregarFonte("/Fonts/PressStart2P.ttf");
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		main.criarJanela();
		main.criarCampo();
		main.criarCobra();
		main.criarFood();

		new Thread(main).start();
	}
}
