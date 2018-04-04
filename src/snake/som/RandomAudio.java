package snake.som;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

public class RandomAudio extends Audio{
	
	private ArrayList<Clip> listaAudio;
	private int randonIndex;

	public RandomAudio(String fileName, ArrayList<Clip> lista) {
		super(fileName);
		this.listaAudio = lista;
		listaAudio.add(this.clip);		
		
	}

	public void play() {
		
		for (int x = 0; x < listaAudio.size(); x++) {
			if (listaAudio.get(x).isRunning()) {
				listaAudio.get(x).stop();
			}
		}

		//impedir q o msm som toque duas vezes seguidas
		int newRandonIndex =  (int) (Math.random() * listaAudio.size());
		
		while (randonIndex == newRandonIndex) {
			newRandonIndex = (int) (Math.random() * listaAudio.size());
		}
		
		randonIndex = newRandonIndex;

		listaAudio.get(newRandonIndex).setFramePosition(0);
		listaAudio.get(newRandonIndex).start();
		System.out.println("Tocou " + newRandonIndex);

	}

	public void stop() {
		for (int x = 0; x < listaAudio.size(); x++) {
			if (listaAudio.get(x).isRunning()) {
				listaAudio.get(x).stop();
				listaAudio.get(x).setFramePosition(0);
			}
		}
	}
}
