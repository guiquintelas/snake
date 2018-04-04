package snake.som;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

public class MaçaAudio extends RandomAudio {
	
	private static ArrayList<Clip> listaAudio = new ArrayList<Clip>();

	public MaçaAudio(String fileName) {
		super(fileName, listaAudio);
	}
}
