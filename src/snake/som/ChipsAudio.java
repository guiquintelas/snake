package snake.som;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

public class ChipsAudio extends RandomAudio{
	
	private static ArrayList<Clip> listaAudio = new ArrayList<Clip>();

	public ChipsAudio(String fileName) {
		super(fileName, listaAudio);
	}

}
