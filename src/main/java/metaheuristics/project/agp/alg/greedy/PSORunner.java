package metaheuristics.project.agp.alg.greedy;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Camera;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

public class PSORunner {

	private static int populationNumPerTriang = 4;

	private static int[] populationTestChange = { 1, 3, 5, 10, 20 };

	private static int iteration = 30;

	private static int[] iterationTestChange = { 5, 10, 15, 30, 50 };

	private static GalleryInstance gi;

	private static GeometryFactory gf = new GeometryFactory();

	private static List<Polygon> cover;
	
	private static List<Camera> finalCameras;

	private static double giArea;

	public static double EPSILON = 0.01;

	public static String[] testFiles = {
//			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/test/min-8-1.pol",  
//			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/test/randsimple-60-23.pol",
//			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/test/randsimple-60-2.pol",
//			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/test/randsimple-40-5.pol",
//			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/test/randsimple-20-8.pol",
//			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/test/randsimple-80-18.pol" 
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/results/triang_A7/randsimple-60-3.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/results/triang_A7/randsimple-40-22.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/results/triang_A7/randsimple-60-9.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2007-minarea/min-120-1.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2007-minarea/min-194-1.pol",
			"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/agp2007-minarea/min-200-1.pol" 
		};
			
	public static void init(double epsilon, int iteracije, int population) {
		EPSILON = epsilon;
		iteration = iteracije;
		populationNumPerTriang = population;
	}
	
	private static int ID = 0;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException {
		try {
			// information about test output
			FileOutputStream fos = new FileOutputStream(new File(
					"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/results/TestingResults/ShortResGBB.txt"));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			// points output with apropriate ID
			FileOutputStream fos2 = new FileOutputStream(new File(
					"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/results/TestingResults/PointsGBB.txt"));
			BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(fos2));
			for (String file : testFiles) {
				System.out.println("Poceo sam za :  " + file);
				for (int pop : populationTestChange) {
					populationNumPerTriang = pop;
					for (int iter : iterationTestChange) {
						System.out.println("ID testa :  " + ID);
						iteration = iter;
						bw.write("ID:   " + ID + "\n");
						bw.write("Test sample:  " + file + "\n");
						bw.write("Population number:  " +  pop + "\n");
						bw.write("Number of iterations:  " +  iter + "\n");
						List<TriangleOptimization> psoTriangles = new ArrayList<>();
						long time = System.currentTimeMillis();
						findBestCameraPositions(psoTriangles, file);
						calculateMinCameraNum(psoTriangles);
						bw.write("Proslo vrijeme: " + (System.currentTimeMillis() - time) + " \nBroj potrebnih kamera: " + cover.size() + "\n");
						bw.write("Kamere pokrivaju:  " +  union.buffer(0).getArea() + "  od  " + giArea + "\n" );
						bw.write("Points: \n");
						int newLine = 0;
						bw2.write("ID:   " + ID + "\n");
						for(Camera cam : finalCameras){
							newLine++;
							bw2.write("(" + cam.x + ", " + cam.y + "),  ");
							if(newLine % 6 == 0){
								bw2.write("\n");
							}
						}
						bw.write("\n");
						bw2.write("\n");
						ID++;
					}
					bw.write("\n");
				}

			}
			bw.close();
			bw2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int process(String filename) {
		List<TriangleOptimization> psoTriangles = new ArrayList<>();

		long time = System.currentTimeMillis();

		findBestCameraPositions(psoTriangles, filename);
		calculateMinCameraNum(psoTriangles);

		return cover.size();
	}

	/**
	 * 
	 * @param psoTriangles
	 */
	private static void findBestCameraPositions(
			List<TriangleOptimization> psoTriangles, String filename) {
		gi = new BenchmarkFileInstanceLoader().load(filename);
		ConformingDelaunayTriangulationBuilder cdtb = new ConformingDelaunayTriangulationBuilder();

		cdtb.setSites(createPolygon(gi.getVertices()));
		GeometryCollection gc = (GeometryCollection) cdtb.getTriangles(gf);
		// printGeom(gc);
		// System.out.println(gc.union().getArea());
		// System.out.println(createPolygon(gi.getVertices()).getArea());
		// System.out.println(gc.getNumGeometries());
		Polygon gallery = createPolygon(gi.getVertices());

		for (int i = 0; i < gc.getNumGeometries(); ++i) {
			Polygon p = (Polygon) gc.getGeometryN(i);
			if (!gallery.contains(p)) {
				continue;
			}
			TriangleOptimization triangleOpt = new TriangleOptimization(gi,
					populationNumPerTriang, p, iteration);
			triangleOpt.process(gi);
			psoTriangles.add(triangleOpt);
		}

		giArea = gi.calculateArea();
		Collections.sort(psoTriangles);
	}

	/**
	 * @param psoTriangles
	 * 
	 */
	private static Geometry union;

	private static void calculateMinCameraNum(
			List<TriangleOptimization> psoTriangles) {
		cover = new ArrayList();
		finalCameras = new ArrayList<>();
		// provjera da li se dodavanjem kamere neznatno povecala vidljivost
		double unnecessaryCamCheck = 0;
		int br = 0;

		for (TriangleOptimization to : psoTriangles) {
			cover.add(to.visiblePolygon);
			finalCameras.add(to.getBest().getCam());
		}

		updateCoveredArea();

		double max = union.getArea();

		for (TriangleOptimization to : psoTriangles) {
			cover.remove(to.visiblePolygon);
			finalCameras.remove(to.getBest().getCam());
			updateCoveredArea();
			if (max - union.getArea() > EPSILON) {
				cover.add(to.visiblePolygon);
				finalCameras.add(to.getBest().getCam());
				updateCoveredArea();
			}
		}
	}

	private static void updateCoveredArea() {
		Polygon[] polygons = cover.toArray(new Polygon[cover.size()]);
		GeometryCollection polygonCollection = gf
				.createGeometryCollection(polygons);
		union = polygonCollection.buffer(0);
	}

	/**
	 * 
	 * @param bound
	 * @return
	 */
	static Polygon createPolygon(List<Coordinate> bound) {
		Coordinate[] boundary = new Coordinate[bound.size() + 1];
		for (int i = 0; i < boundary.length - 1; ++i)
			boundary[i] = bound.get(i);
		boundary[boundary.length - 1] = bound.get(0);
		return gf.createPolygon(boundary);
	}

}
