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
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy;
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy.InitialSet;
import metaheuristics.project.agp.alg.greedy.heuristics.A7;
import metaheuristics.project.agp.alg.hybrid.HybridAlgorithm;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Camera;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

public class Tester {
	
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
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			File f  = new File("agp2009a-simplerand-hybrid/" + dir.getFileName() + ".txt");
			f.getParentFile().mkdirs(); 
			f.createNewFile();
			bw = new BufferedWriter(
					 new OutputStreamWriter(
					 new BufferedOutputStream(
					 new FileOutputStream("agp2009a-simplerand-hybrid/" + dir.getFileName() + ".txt")),"UTF-8"));
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
		HybridAlgorithm ha = new HybridAlgorithm(InitialSet.TRIANGULATION_COVER, new A7());
		long start = System.currentTimeMillis();
		ha.process(file.toString(), "agp2009a-simplerand-hybrid/" + file.getFileName() + ".txt");
		long end = System.currentTimeMillis() - start;
		GalleryInstance gi = new BenchmarkFileInstanceLoader().load(file.toString());
		StringBuilder sb = new StringBuilder();
		sb.append(file.getFileName() + " " + gi.getVertices().size() + " " + gi.getHoles().size());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
					"agp2009a-simplerand-hybrid/" + file.getFileName() + ".txt"), "UTF-8"));
			String[] coordinates = br.readLine().split("\\s+");
			int cameraNum = coordinates.length / 2;
			sb.append(" " + cameraNum + " " + end);
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private static void saveCameras(GalleryInstance gi, Path dir) {
		BufferedWriter bw = null;
		try {
			File f  = new File("simplep-simpllehAGP2013-greedy/" + dir.getFileName() + ".txt");
			f.getParentFile().mkdirs(); 
			f.createNewFile();
			bw = new BufferedWriter(
					 new OutputStreamWriter(
					 new BufferedOutputStream(
					 new FileOutputStream("simplep-simpllehAGP2013-greedy/" + dir.getFileName() + ".txt")),"UTF-8"));
			StringBuilder sb = new StringBuilder();
			for(Camera c : gi.getCameras()) {
				sb.append(c.toString() + " ");
			}
			bw.write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {}
		}
	}

	public static void main(String[] args) {
		Tester.testAlgorithm(new HeuristicGreedy(InitialSet.TRIANGULATION_COVER, new A7()), "agp2009a-simplerand");
	}
}
