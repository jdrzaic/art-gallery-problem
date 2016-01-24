package testing;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
			File f  = new File("simplep-simpllehAGP2013-greedy/" + dir.getFileName() + ".txt");
			f.getParentFile().mkdirs(); 
			f.createNewFile();
			bw = new BufferedWriter(
					 new OutputStreamWriter(
					 new BufferedOutputStream(
					 new FileOutputStream("simplep-simpllehAGP2013-greedy/" + dir.getFileName() + ".txt")),"UTF-8"));
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			if((file.getFileName().toString()).compareTo(".DS_Store") == 0) return FileVisitResult.CONTINUE;
			System.out.println(file.toString());
			try {
				sb.append(createResult(alg, file));
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
		Tester.testAlgorithm(new HeuristicGreedy(InitialSet.VERTEX_TRIANGULATION_COVER, new A7()), "simple_polygons_with_simple_holes_AGP2013");
	}
}
