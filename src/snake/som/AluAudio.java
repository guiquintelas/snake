package snake.som;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

public class AluAudio extends RandomAudio{
	
	private static ArrayList<Clip> listaAudio = new ArrayList<Clip>();

	public AluAudio(String fileName) {
		super(fileName, listaAudio);
	}

}
