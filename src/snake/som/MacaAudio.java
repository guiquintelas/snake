package snake.som;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

public class MacaAudio extends RandomAudio {
	
	private static ArrayList<Clip> listaAudio = new ArrayList<Clip>();

	public MacaAudio(String fileName) {
		super(fileName, listaAudio);
	}
}
