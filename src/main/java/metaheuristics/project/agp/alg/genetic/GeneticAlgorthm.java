package metaheuristics.project.agp.alg.genetic;

import java.io.File;
import java.io.*;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Genetic algorithm for solving Art Gallery Problem.
 * @author jelenadrzaic
 *
 */
public class GeneticAlgorthm {
	
	/**
	 * Main method. Returns number ofcameras needed to cover polygon stored
	 * in file "filePolygon". Saves coordinates of cameras into file "fileToSave".
	 * @param filePolygon file containing polygon
	 * @param fileToSave file to save cameras into.
	 * @return number of cameras
	 */
	public int process(String filePolygon, String fileToSave) {
		return process(filePolygon, fileToSave, "");
	}
	
	/**
	 * Same as method above.
	 * @param filePolygon file contains polygon to cove.
	 * @param fileToSave file to save results into.
	 * @param initCover initial cover (set of cameras) used by algorithm.
	 * @return number of cameras
	 */
	public int process(String filePolygon, String fileToSave, String initCover) {
		try {

			String os = System.getProperty("os.name");
			String execName = "";
			String cmd = "";
			if(os.startsWith("Windows")) {
				execName = "./GeneticAlgorithm.exe";
				String maybeInitCover = initCover.isEmpty() ? "" : "\"" + initCover + "\"";
				cmd = execName + " \"" + filePolygon  + "\" " + maybeInitCover + " \"" + fileToSave + "\"";
			} else {
				execName = "./GeneticAlgorithm";
				cmd = execName + " " + filePolygon  + " " + initCover + " " + fileToSave;
			}
			System.out.println(cmd);
			Process p = Runtime.getRuntime().exec(cmd);
			try {
				p.waitFor();
				copy(p.getInputStream(), System.out);
				copy(p.getErrorStream(), System.out);
			} catch (InterruptedException e) {
				System.err.println("Error wainting bash");
			}
		} catch (IOException e) {
			System.err.println("Error executing bash");
		}
		try {
			Thread.sleep(3000);
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

	  static void copy(InputStream in, OutputStream out) throws IOException {
	    while (true) {
	      int c = in.read();
	      if (c == -1) break;
	      out.write((char)c);
	    }
	  }
}
