package metaheuristics.project.agp.alg.genetic;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class GeneticAlgorthm {
	
	public int process(String filePolygon, String fileToSave) {
		return process(filePolygon, fileToSave, "");
	}
	
	public int process(String filePolygon, String fileToSave, String initCover) {
		System.out.println("genetic calld");
		try {
			String execName = System.getProperty("os.name").startsWith("Windows") ? "./GeneticAlgorithm.exe" : "./GeneticAlgorithm";
			Process p = Runtime.getRuntime().exec(execName + " " +  
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
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		int n = 0;
		try {
			String cameras = FileUtils.readFileToString(new File(fileToSave));
			String[] coordinates = cameras.split("\\s+");
			n = coordinates.length / 2;
		} catch (IOException e) {
			System.err.println("cant read results");
		}
		return n;
	}
}
