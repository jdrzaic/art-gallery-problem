package metaheuristics.project.agp.alg.greedy;

import java.awt.Point;
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

	private static int populationNumPerTriang = 8;

	private static int iteration = 100;

	private static GalleryInstance gi;
	
	private static GeometryFactory gf = new GeometryFactory();

	private static List<Polygon> cover;

	private static double giArea;

	public static double EPSILON = 0.01;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		List<TriangleOptimization> psoTriangles = new ArrayList<>();

		long time = System.currentTimeMillis();

		findBestCameraPositions(psoTriangles);
		calculateMinCameraNum(psoTriangles);

		System.out.println(cover.size() + " camera pokriva  "
				+ union.buffer(0).getArea() + "  od  " + giArea);
		System.out.println(	"Proslo vrijeme: " + (System.currentTimeMillis() - time));
	}
	


	/**
	 * 
	 * @param psoTriangles
	 */
	private static void findBestCameraPositions(List<TriangleOptimization> psoTriangles) {
		gi = new BenchmarkFileInstanceLoader().load(
				"/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem/results/triang_A7/randsimple-60-9.pol");
		ConformingDelaunayTriangulationBuilder cdtb = new ConformingDelaunayTriangulationBuilder();
		

		cdtb.setSites(createPolygon(gi.getVertices()));
		GeometryCollection gc = (GeometryCollection) cdtb.getTriangles(gf);
//		printGeom(gc);
//		System.out.println(gc.union().getArea());
//		System.out.println(createPolygon(gi.getVertices()).getArea());
//		System.out.println(gc.getNumGeometries());
		Polygon gallery = createPolygon(gi.getVertices());
	
		for (int i = 0; i < gc.getNumGeometries(); ++i) {
			Polygon p = (Polygon) gc.getGeometryN(i);
			if(! gallery.contains(p)){
				continue;
			}
			TriangleOptimization triangleOpt = new TriangleOptimization(gi, populationNumPerTriang, p, iteration);
			triangleOpt.process(gi);
			psoTriangles.add(triangleOpt);
		}

		giArea = gi.calculateArea();
		Collections.sort(psoTriangles);
	}

//	private static void printGeom(GeometryCollection gc) {
//		DecimalFormat df = new DecimalFormat("0.000");
//		System.out.println(gc.getCoordinates().length);
//		int br = 0;
//		for(Coordinate c : gc.getCoordinates()){
//			if(br++ % 4 == 0){
//				System.out.println("\n");
//			}
//			System.out.println(df.format(c.x) + "   " + df.format(c.y));
//		}
//	}

	/**
	 * @param psoTriangles
	 * 
	 */
	private static Geometry union;

	private static void calculateMinCameraNum(
			List<TriangleOptimization> psoTriangles) {
		cover = new ArrayList();
		// provjera da li se dodavanjem kamere neznatno povecala vidljivost
		double unnecessaryCamCheck = 0;
		int br = 0;

		for( TriangleOptimization to : psoTriangles){
			cover.add(to.visiblePolygon);
		}
		 
		updateCoveredArea();
		
		double max = union.getArea();
		
		for(TriangleOptimization to : psoTriangles){
			cover.remove(to.visiblePolygon);
			updateCoveredArea();
			if(max - union.getArea() > EPSILON){
				cover.add(to.visiblePolygon);
			}
		}

//		// idi po svim trokutima
//		for (TriangleOptimization to : psoTriangles) {
//			br++;
//			// stavi kameru
//			cover.add(to.visiblePolygon);
//			// updateaj povrsinu
//			updateCoveredArea();
//			
//			double curArea = union.getArea();
//			 if ( curArea - unnecessaryCamCheck <= EPSILON ) {
////			System.out.println("Micem kameru!" + br);
//				cover.remove(to);
//				continue;
//			}
//				
//			// provjeri da li smo pokrili cijeli prostor
//			if (giArea - curArea <= EPSILON && giArea - curArea >= 0 ) {
//			//	System.out.println("Zavrsili smo s potragom!");
//				break;
//			}
//			
//			unnecessaryCamCheck = curArea;
//		}
	}

	private static void updateCoveredArea() {
		Polygon[] polygons = cover
				.toArray(new Polygon[cover.size()]);
		GeometryCollection polygonCollection = gf	.createGeometryCollection(polygons);
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