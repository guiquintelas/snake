package snake.som;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

public class EatAluAudio extends RandomAudio{

	private static ArrayList<Clip> listaAudio = new ArrayList<Clip>();

	public EatAluAudio(String fileName) {
		super(fileName, listaAudio);
	}
}
