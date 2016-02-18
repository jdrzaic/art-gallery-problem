package metaheuristics.project.agp.alg.greedy;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Triangle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import metaheuristics.project.agp.alg.Algorithm;
import metaheuristics.project.agp.alg.greedy.heuristics.Heuristic;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Camera;
import metaheuristics.project.agp.instances.util.Maths;


/**
 * Heuristic algorithm solving art gallery problem.
 * Uses {@link GalleryInstance} za reprezentaciju galerije.
 * 
 * @author jelenadrzaic
 *
 */
public class HeuristicGreedy implements Algorithm{

	/**
	 * Enum - pocetni pokrivaci iz kojeg biramo kamere
	 * @author jelenadrzaic
	 *
	 */
	public enum InitialSet {
		VERTEX_COVER,
		VERTEX_TRIANGULATION_COVER,
		TRIANGULATION_COVER
	}
	
	/**
	 * Broj kamera koje ubacujemo u jednoj iteraciji algoritma
	 */
	public static int k = 1;
	
	/**
	 * Tolerancija "nepokrivenosti" galerije
	 */
	public static double EPSILON = 0.01;

	/**
	 * Instanca {@link GeometryFactory} koristena u implementaciji
	 */
	private GeometryFactory gf = new GeometryFactory();
	
	/**
	 * Polygon - gallery.
	 */
	private Polygon main;
	
	/**
	 * Heuristic function used.
	 */
	private Heuristic h;
	
	/**
	 * Initial set from which cameras are chosen.
	 */
	private InitialSet is;
	
	/**
	 * Map of cameras and its visibility polygons.
	 */
	private HashMap<Camera, Polygon> visPolygons;
		
	/**
	 * current part of gallery covered.
	 */
	Geometry coverUnion;
	
	/**
	 * Instance to cover.
	 */
	GalleryInstance gi;

	/**
	 * Class constructor.
	 * @param is type of initial cover
	 * @param h heuristic function used
	 */
	public HeuristicGreedy(InitialSet is, Heuristic h) {
		this.is = is;
		this.h = h;
	}
	
	@Override
	public void process(GalleryInstance gi) throws 	IllegalArgumentException {
		
		this.gi = gi;
		this.visPolygons = new HashMap<>();
		this.coverUnion = null;
		this.main = createPolygon(gi.getVertices(), gi.getHoles());
		System.out.println("polygon created");
		List<Camera> init = createInitialSet(gi);
		System.out.println("here" + init.toString());
		for(int i = 0; i < init.size(); ++i) {
			List<Coordinate> bound = init.get(i).visibilityPolygon(gi).getVertices();
			visPolygons.put(init.get(i), 
					//za rupe, al ovdje ne treba
					createPolygon(bound, new ArrayList<>()));
			System.out.println(init.get(i).x + "  " + init.get(i).y);
		}
		boolean covered = false;
		while(!covered) {
			//changing manualy!!
			Camera[] cams = findBest2();
			if(visPolygons.size() <= 2) covered = true;
			if(cams.length > 0) {
				covered = checkIfCovered(cams[0]);
				System.out.println("camera in cover");
				if(cams.length > 1 && !covered) {
					covered = checkIfCovered(cams[1]);
				}
			} else {
				throw new IllegalArgumentException();
			}
		}
 	}
	
	/** 
	 * Tollerance setter
	 * @param tol tollerance, in percents
	 */
	public void setTol(double tol) {
		EPSILON = tol < 1 ? 0.01 : tol / 100;
		System.out.println("tu: " + EPSILON);
	}
	
	/**
	 * Method checks if gallery is covered after adding camera c.
	 * @param c camera to add
	 * @return true if gallery is covered, false otherwise
	 */
	private boolean checkIfCovered(Camera c) {
		double areaAll = main.getArea();
		if(coverUnion == null) {
			coverUnion = visPolygons.get(c);
		} else {
			coverUnion = coverUnion.union(visPolygons.get(c));
		}
		gi.addCamera(c);
		visPolygons.remove(c);
		System.out.println(areaAll - coverUnion.getArea() + "\n");
        if(Math.abs(areaAll - coverUnion.getArea()) < areaAll * EPSILON) {
            return true;
        }
        return false;
	}

	/**
	 * Best camera according to heuristic function used.
	 * @return best camera to add
	 */
	private Camera findBest() {
		double maxmi = -1;
		Camera max = null;
		for(Camera c : visPolygons.keySet()) {
			double mi = h.utilValue(visPolygons.get(c), coverUnion, gf);
			if(maxmi == -1 || mi > maxmi) {
				maxmi = mi;
				max = c;
			}
		}
		return max;
	}

    /**
     * Method returns 1 or 2 best cameras to chose, depending on the heuristic used.
     * if 1 camera has better score than all of pairs, it is returned. Otherwise, best pair is returned.
     */
	private Camera[] findBest2() {
		if(k == 1) {
			Camera[] toAdd = new Camera[1];
			toAdd[0] = findBest();
			return toAdd;
		}
		Camera[] toAdd = new Camera[2];
		Camera best1 = null;
		double maxmi = -1;
		double maxmi1 = -1;
		for(Camera c : visPolygons.keySet()) {
			double mi1 = h.utilValue(visPolygons.get(c), coverUnion, gf);
			if(maxmi1 == -1 ||  mi1 > maxmi1) {
				best1 = c;
				maxmi1 = mi1;
			}
			for(Camera d : visPolygons.keySet()) {
				if(d == c) continue;
				double mi = h.utilValue(visPolygons.get(c).union(visPolygons.get(d)), coverUnion, gf);
				if(maxmi == -1 || mi > maxmi) {
					maxmi = mi;
					toAdd[0] = c;
					toAdd[1] = d;
				}
			}
		}
		if(maxmi > maxmi1) {
			return toAdd;
		}
		Camera[] toAdd1 = new Camera[1];
		toAdd1[0] = best1;
		return toAdd1;
	}
	
	/**
	 * Method creates polygon with given bound and holes.
	 * @param bound bound of a polygon to create
	 * @param holes holes of a polygon to create
	 * @return created polygon
	 */
	Polygon createPolygon(List<Coordinate> bound, List<metaheuristics.project.agp.instances.components.Polygon> holes) {
		Coordinate[] boundary = new Coordinate[bound.size() + 1];
		for(int i = 0; i < boundary.length - 1; ++i) boundary[i] = bound.get(i);
		boundary[boundary.length - 1] = bound.get(0);
		LinearRing boundRing = gf.createLinearRing(boundary);
		LinearRing[] holesRing = new LinearRing[holes.size()];
		int index = 0;
		for(metaheuristics.project.agp.instances.components.Polygon h : holes) {
			Coordinate[] boundarHole = new Coordinate[h.getVertices().size() + 1];
			for(int i = 0; i < h.getVertices().size(); ++i) boundarHole[i] = h.getOnIndex(i);
			boundarHole[h.getVertices().size()] = h.getOnIndex(0);
			holesRing[index] = gf.createLinearRing(boundarHole);
			++index;
		}
		Polygon p = gf.createPolygon(boundRing, holesRing);
		return p;
	}

	/**
	 * Method returns initial cover for gallery.
	 * @param gi gallery to cover
	 * @return initial cover
	 */
	private List<Camera> createInitialSet(GalleryInstance gi) {
		switch (is) {
		case VERTEX_COVER:
			return createInitialVertexCover(gi);
		case TRIANGULATION_COVER:
			return createInitialTriangCover(gi);
		case VERTEX_TRIANGULATION_COVER:
			List<Camera> initVertexTrian = createInitialVertexCover(gi);
			initVertexTrian.addAll(createInitialTriangCover(gi));
			return initVertexTrian;
		default:
			return null;
		}
	}

	/**
	 * Method creates initial cover using Delaunay triangulation.
	 * @param gi gallery to cover
	 * @return initial cover generated
	 */
	private List<Camera> createInitialTriangCover(GalleryInstance gi) {
		List<Camera> ini = new ArrayList<>();
		ConformingDelaunayTriangulationBuilder cdtb =
				new ConformingDelaunayTriangulationBuilder();
		cdtb.setSites(this.main);
		GeometryCollection gc = (GeometryCollection)cdtb.getTriangles(gf);
		for(int i = 0; i < gc.getNumGeometries(); ++i) {
			Polygon p = (Polygon)gc.getGeometryN(i);
			Coordinate[] verts = p.getCoordinates();
			Triangle t = new Triangle(verts[0], verts[1], verts[2]);
			Coordinate centr = t.centroid();
			if(gf.createPoint(centr).within(main)) {
				ini.add(new Camera(centr.x, centr.y));
			}
		}
		return ini;
	}

	/**
	 * Method returns initial cover containing vertices of the polygon.
	 * @param gi gallery to cover
	 * @return vertex cover
	 */
	private List<Camera> createInitialVertexCover(GalleryInstance gi) {
		List<Camera> ini = new ArrayList<Camera>();
		addCandidates(gi.getVertices(), ini);
		return ini;
	}

	/**
	 * Method adds gallery vertices to list.
	 * @param vertices vertices of a gallery
	 * @param ini list of cameras
	 */
	private void addCandidates(List<Coordinate> vertices, List<Camera> ini) {
		int size = vertices.size();
		for(int i = 0; i < size; ++i) {
			LineSegment side1 = new LineSegment(vertices.get((i +1) % size), vertices.get(i % size));
			LineSegment side2 = new LineSegment(vertices.get((i + 1) % size), vertices.get((i + 2) % size));
			ini.add(calcInside(vertices, side1.p0, calcBisector(side1, side2)));
		}
		
	}

	
	private Camera calcInside(List<Coordinate> vertices, Coordinate v, Coordinate bisector) {
		double norm = Math.sqrt(bisector.x * bisector.x + bisector.y * bisector.y);
		Coordinate vbNormalized = Maths.cRound(new Coordinate(bisector.x / norm, bisector.y / norm));
		Coordinate cand1 = Maths.cRound(new Coordinate(v.x + vbNormalized.x * EPSILON, 
				v.y + vbNormalized.y * EPSILON));
		Coordinate cand2 = Maths.cRound(new Coordinate(v.x - vbNormalized.x * EPSILON, 
				v.y - vbNormalized.y * EPSILON));
		Point p1 = gf.createPoint(cand1);
		if(p1.within(main)) return new Camera(cand1.x, cand1.y);
		return new Camera(cand2.x, cand2.y);
	}

	private Coordinate calcBisector(LineSegment side1, LineSegment side2) {
		Coordinate v1 = Maths.cRound(new Coordinate(side1.p1.x - side1.p0.x, side1.p1.y - side1.p0.y));
		Coordinate v2 = Maths.cRound(new Coordinate(side2.p1.x - side2.p0.x, side2.p1.y - side2.p0.y));
		Coordinate vb = Maths.cRound(new Coordinate((v1.x + v2.x) / 2, (v1.y + v2.y) / 2));
		return vb;
	}
}
