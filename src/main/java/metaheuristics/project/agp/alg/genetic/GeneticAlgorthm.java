package metaheuristics.project.agp.alg.genetic;

import java.io.IOException;

import metaheuristics.project.agp.alg.Algorithm;
import metaheuristics.project.agp.instances.GalleryInstance;

public class GeneticAlgorthm {
	
	public GeneticAlgorthm() {}

	public void process(String filePolygon, String fileToSave) {
		process(filePolygon, fileToSave, "");
	}
	
	public void process(String filePolygon, String fileToSave, String initCover) {
		System.out.println("genetic calld");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			Process p = Runtime.getRuntime().exec("./GeneticAlgorithm " +  
				  filePolygon  + " " + initCover + " " + fileToSave);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				System.err.println("Error wainting bash");
			}
		} catch (IOException e) {
			System.err.println("Error executing bash");
		}
		System.out.println("genetic ended");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		GeneticAlgorthm ga = new GeneticAlgorthm();
		ga.process("cam.txt", "bbb.txt");
	}
}
