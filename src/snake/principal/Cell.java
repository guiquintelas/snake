package snake.principal;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class Cell {
	public int xPosition;
	public int yPosition;
	public static final int WIDTH = 20;
	public static final int HEIGHT = 20;
	//que tipo de cell
	private boolean snake = false;
	private boolean food = false;
	private boolean corpo;
	//orientaçao
	public int orientaçao = 1;
	// 1 = esuqerda
	// 2 = cima
	// 3 = direita
	// 4 = baixo
	
	//integridade
	public static final double INTEGRIDADE_MAX = 100.0;
	public static double intregridade = 0;
	
	//celula q essa parte representa na cobra inteira
	public int celulasDoCorpo = 0;	
	
	//imagem
	public ImageIcon maçaSprite;
	public BufferedImage snakeSprite;
	
	private static ArrayList<BufferedImage> cobraCabeça = new ArrayList<BufferedImage>();
	private static ArrayList<BufferedImage> cobraCorpo = new ArrayList<BufferedImage>();
	private static ArrayList<BufferedImage> cobraCurva = new ArrayList<BufferedImage>();
	private static ArrayList<BufferedImage> cobraRabo = new ArrayList<BufferedImage>();
	
	private static final ImageIcon cogumeloNormal = carregarIcone("/Sprites/cogumeloNormal.gif");
	private static final ImageIcon cogumeloLado = carregarIcone("/Sprites/cogumeloLado.gif");
	private static final ImageIcon cogumeloQuina = carregarIcone("/Sprites/cogumeloQuina.gif");
	
	private static final ImageIcon maçaNormal = carregarIcone("/Sprites/maçaNormal.png");	
	private static final ImageIcon maçaLado = carregarIcone("/Sprites/maçaLado.png");	
	private static final ImageIcon maçaQuina = carregarIcone("/Sprites/maçaQuina.png");
	
	private static final ImageIcon chip = carregarIcone("/Sprites/chip.png");
	
	public Cell(int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
	}
	
	public static void carregarBitmapCobra() {
		BufferedImage bitmap = carregarImagem("/Sprites/bitmap_cobra.png");
		
		for (int y = 0; y < bitmap.getHeight(); y += HEIGHT) {
			for (int x = 0; x < bitmap.getWidth(); x += WIDTH) {
				if (x <= 60 && y == 0) {
					cobraCurva.add(bitmap.getSubimage(x, y, WIDTH, HEIGHT));
					continue;
				}
				
				if (x >= 80 && y == 0) {
					cobraCabeça.add(bitmap.getSubimage(x, y, WIDTH, HEIGHT));
					continue;
				}
				
				if (x <= 60 && y == 20) {
					cobraRabo.add(bitmap.getSubimage(x, y, WIDTH, HEIGHT));
					continue;
				}
				
				if (x >= 80 &&  x <= 100 && y == 20) {
					cobraCorpo.add(bitmap.getSubimage(x, y, WIDTH, HEIGHT));
				}
				
			}
		}
		
	}

	public int getX() {
		return this.xPosition * Cell.WIDTH;
	}

	public int getY() {
		return this.yPosition * Cell.HEIGHT;
	}

	public boolean isSnake() {
		return snake;
	}
	
	public void setCorpo(boolean corpo) {
		this.corpo = corpo;
		if (corpo = false) {
		}
	}

	public void setSnakeCabeça(int orientação) {
		this.celulasDoCorpo = 1;
		this.orientaçao = orientação;
		this.snake = true;
	}
	
	public void setSnake(boolean isSnake, int celulasDoCorpo) {
		this.snake = isSnake;
		this.celulasDoCorpo = celulasDoCorpo;

		if (isSnake == false) {
			celulasDoCorpo = 0;
			corpo = false;
		}
	}
	
	public void setCurva(int tipoDeCurva) {
		snakeSprite = cobraCurva.get(tipoDeCurva); 
	}

	public boolean isCabeça() {
		if (celulasDoCorpo == 1) {
			return true;
		}
		return false;
	}
	
	public boolean isRabo() {
		if (celulasDoCorpo == Principal.tamanhoSnake) {
			return true;
			
		}
		return false;
	}

	public boolean isFood() {
		if (food) {
			if (Principal.gameModeDrogas) {
				maçaSprite = cogumeloNormal;
			} else {
				maçaSprite = maçaNormal;
			}
			

			int quina = 0;
			if (this.xPosition == 0 || this.xPosition == (Principal.TAMANHO_GRID - 1)) {				
				
				if (Principal.gameModeDrogas) {
					maçaSprite = cogumeloLado;
				} else {
					maçaSprite = maçaLado;
				}
				
				quina++;
			}

			if (this.yPosition == 0 || this.yPosition == (Principal.TAMANHO_GRID - 1)) {
				
				if (Principal.gameModeDrogas) {
					maçaSprite = cogumeloLado;
				} else {
					maçaSprite = maçaLado;
				}
				
				quina++;
			}
			
			if (quina == 2) {
				if (Principal.gameModeDrogas) {
					maçaSprite = cogumeloQuina;
				} else {
					maçaSprite = maçaQuina;
				}
			}
			
			if (Principal.chipsAtivo) {
				maçaSprite = chip;
			}
			
		}
		return food;
	}

	public void setFood(boolean isFood) {
		this.food = isFood;
		if (isFood) {
			intregridade = INTEGRIDADE_MAX;
			return;
		}

	}

	public boolean isCampo() {
		if (food  || snake) {
			return false;
		}
		return true;
	}
	

	public void atualiza() {
		
		isFood();
		isCampo();
		
		if (isSnake()) {
			if (isCabeça()) {		
				snakeSprite = cobraCabeça.get(orientaçao - 1);
			return;
			}
			if (isRabo()) {
				snakeSprite = cobraRabo.get(orientaçao - 1);
				return;
			}
			
			if (corpo) {
				if (orientaçao == 1 || orientaçao == 3) {
					snakeSprite = cobraCorpo.get(0);
				} else {
					snakeSprite = cobraCorpo.get(1);
				}
				
				return;
			}
		}
		
		
	}
	
	public static void atualizaIntegridade() {
		intregridade -= 1;
		if (intregridade < 0) {
			intregridade = 0;
		}
	}

	public int pontos() {
		int pontos = 5;
		if (this.xPosition == 0 || this.xPosition == (Principal.TAMANHO_GRID - 1)) {
			pontos += 5;
		}

		if (this.yPosition == 0 || this.yPosition == (Principal.TAMANHO_GRID - 1)) {
			pontos += 5;
		}
		
		if (Principal.chipsAtivo) {
			return (int)(pontos * (intregridade / 100)) * 3;
		}
		
		return (int)(pontos * (intregridade / 100));
	}

	
	private static ImageIcon carregarIcone(String path) {
		ImageIcon imagem = null;
		try {
			imagem = new ImageIcon(new URL(Cell.class.getResource(path).toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imagem;
	}
	
	private static BufferedImage carregarImagem(String path) {
		BufferedImage imagem = null;
		try {
			imagem = ImageIO.read(Cell.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imagem;
	}
	
	
}
