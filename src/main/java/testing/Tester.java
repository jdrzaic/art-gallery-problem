package testing;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FileUtils;

import metaheuristics.project.agp.alg.Algorithm;
import metaheuristics.project.agp.alg.genetic.GeneticAlgorthm;
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy;
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy.InitialSet;
import metaheuristics.project.agp.alg.greedy.heuristics.A7;
import metaheuristics.project.agp.alg.hybrid.HybridAlgorithm;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Camera;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

public class Tester {
	
	static String resultsFolder = "test_results_and_samples/results/TestingResults/2009a-simplerand-hybrid-union-99.8-2/";
	static String initCoverFolder = "test_results_and_samples/results/TestingResults/2009a-simplerand-greedy-union-99.5/";
	static BenchmarkFileInstanceLoader bfil = new BenchmarkFileInstanceLoader();

	public static void testAlgorithm(Algorithm alg, String root) {
		Path start = Paths.get(root);
		try {
			Files.walkFileTree(start, new TestWalker(alg));
		} catch (IOException e) {
			e.printStackTrace();
		}   
	}
	
	public static class TestWalker implements FileVisitor<Path> {

		public TestWalker(Algorithm alg) {
			this.alg = alg;
		}
		
		Algorithm alg;
		StringBuilder sb = new StringBuilder();
		Writer bw = null;
		Writer errorw = null;
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			File f  = new File(resultsFolder + dir.getFileName() + ".txt");
			f.getParentFile().mkdirs(); 
			f.createNewFile();
			bw = new BufferedWriter(
					 new OutputStreamWriter(
					 new BufferedOutputStream(
					 new FileOutputStream(resultsFolder + dir.getFileName() + ".txt")),"UTF-8"));
			File error  = new File(resultsFolder + dir.getFileName() + ".error");
			error.getParentFile().mkdirs(); 
			error.createNewFile();
			errorw = new BufferedWriter(
					 new OutputStreamWriter(
					 new BufferedOutputStream(
					 new FileOutputStream(resultsFolder + dir.getFileName() + ".error")),"UTF-8"));
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			if((file.getFileName().toString()).compareTo(".DS_Store") == 0) return FileVisitResult.CONTINUE;
			System.out.println(file.toString());
			try {
				//sb.append(createResult(alg, file));
				bw.write(createResult(file) + System.lineSeparator());
				bw.flush();
				//sb.append(createResult(file));
			} catch(Exception e) {
				System.err.println(file.toString());
				errorw.write(file.toString() + System.getProperty("line.separator"));
				return FileVisitResult.CONTINUE;
			}
			sb.append(System.getProperty("line.separator"));
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			bw.write(sb.toString());
			bw.close();
			return FileVisitResult.CONTINUE;
		}
		
	}

	public static String createResult(Algorithm alg, Path file) {
		StringBuilder sb = new StringBuilder();
		GalleryInstance gi = bfil.load(file.toString());
		long start = System.currentTimeMillis();
		gi.setCameras(alg);
		long length = (System.currentTimeMillis() - start) / 1000;
		saveCameras(gi, file);
		sb.append(file.getFileName() + " " + gi.getVertices().size() + " " + gi.getHoles().size() + " " + gi.cameraNum() + " " + length);
		return sb.toString();
	}
	
	public static String createResult(Path file) {
		GeneticAlgorthm ga = new GeneticAlgorthm();
		long start = System.currentTimeMillis();
		ga.process(file.toString(), resultsFolder + file.getFileName() + ".txt", initCoverFolder + file.getFileName() + ".txt");
		long end = System.currentTimeMillis() - start;
		GalleryInstance gi = new BenchmarkFileInstanceLoader().load(file.toString());
		StringBuilder sb = new StringBuilder();
		sb.append(file.getFileName() + " " + gi.getVertices().size() + " " + gi.getHoles().size());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
					resultsFolder + file.getFileName() + ".txt"), "UTF-8"));
			String[] coordinates = br.readLine().split("\\s+");
			int cameraNum = coordinates.length / 2;
			sb.append(" " + cameraNum + " " + end);
			System.out.println("cameras genetic");
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private static void saveCameras(GalleryInstance gi, Path dir) {
		BufferedWriter bw = null;
		try {
			File f  = new File(resultsFolder + dir.getFileName() + ".txt");
			f.getParentFile().mkdirs(); 
			f.createNewFile();
			bw = new BufferedWriter(
					 new OutputStreamWriter(
					 new BufferedOutputStream(
					 new FileOutputStream(resultsFolder + dir.getFileName() + ".txt")),"UTF-8"));
			StringBuilder sb = new StringBuilder();
			for(Camera c : gi.getCameras()) {
				sb.append(c.toString() + " ");
			}
			bw.write(sb.toString());
		} catch (Exception e) {} 
		finally {
			try {
				bw.close();
			} catch (IOException e) {}
		}
	}

	public static void main(String[] args) {
		Tester.testAlgorithm(new HeuristicGreedy(InitialSet.VERTEX_TRIANGULATION_COVER, new A7()), 
				"test_results_and_samples/benchmarks/agp2009a-simplerand300");
	}
}
