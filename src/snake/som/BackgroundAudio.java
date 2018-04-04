package snake.som;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

import snake.principal.Principal;

public class BackgroundAudio extends RandomAudio {
	private static ArrayList<Clip> listaAudio = new ArrayList<Clip>();
	private static Clip clipPausado;

	public BackgroundAudio(String fileName) {
		super(fileName, listaAudio);
	}

	private boolean isRodando() {
		for (int x = 0; x < listaAudio.size(); x++) {
			if (listaAudio.get(x).isRunning()) {
				return true;
			}
		}

		return false;
	}

	public void play() {

		if (Principal.musicaAtiva && !isRodando()) {
			super.play();
		}

		if (!Principal.musicaAtiva && isRodando()) {
			stop();
		}
	}

	public void pause() {
		if (clipPausado == null) {
			for (int x = 0; x < listaAudio.size(); x++) {
				if (listaAudio.get(x).isRunning()) {
					listaAudio.get(x).stop();
					clipPausado = listaAudio.get(x);
					System.out.println("Parou");
					return;
				}
			}
		}
	}
	
	public void resume() {
		
		if (clipPausado != null) {
			clipPausado.start();
			clipPausado = null;
		}
	}
	
	public void resetar() {
		for (int x = 0; x < listaAudio.size(); x++) {
			listaAudio.get(x).stop();
			listaAudio.get(x).setFramePosition(0);
		}
	}
	

}
