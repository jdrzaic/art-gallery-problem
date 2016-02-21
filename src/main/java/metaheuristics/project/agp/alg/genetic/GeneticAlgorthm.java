package metaheuristics.project.agp.alg.genetic;

import java.io.File;
import java.io.*;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.*;

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
			List<String> cmd = new ArrayList<String>();
			cmd.add("GeneticAlgorithm");
			cmd.add(filePolygon);
			if (!initCover.isEmpty()) cmd.add(initCover);
			cmd.add(fileToSave);
			
			System.out.println(cmd);
			ProcessBuilder builder = new ProcessBuilder(cmd);
			builder.redirectErrorStream(true);
			builder.redirectOutput(Redirect.INHERIT);
			Process p = builder.start();
			try {
				p.waitFor();
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
}

class StreamGobbler extends Thread {
    InputStream is;

    // reads everything from is until empty. 
    StreamGobbler(InputStream is) {
        this.is = is;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
                System.out.println(line);    
        } catch (IOException ioe) {
            ioe.printStackTrace();  
        }
    }
}
