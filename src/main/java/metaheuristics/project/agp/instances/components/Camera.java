package metaheuristics.project.agp.instances.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.math.Vector2D;

import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;
import metaheuristics.project.agp.instances.util.Maths;

/**
 * Class represents one camera.
 * Instances of this class are used for covering 
 * {@link GalleryInstance}.
 * @author jelena
 *
 */
public class Camera extends Coordinate{

	public static double ALPHA = 0.000001;
	public static double EPSILON = 0.00000001;
	
	/**
	 * Class constructor.
	 * @param x x coordinate value.
	 * @param y x coordinate value.
	 */
	public Camera(double x, double y) {
		super(Maths.round(x, 11), Maths.round(y,11));
	}
	
	public Camera(Coordinate c) {
		super(new Coordinate(Maths.round(c.x,11), Maths.round(c.y, 11)));
	}

	/**
	 * Method determining visibility polygon of a camera in
	 * a given {@link GalleryInstance}.
	 * @param gi {@link GalleryInstance} camera is in.
	 * @return visibility polygon of a camera.
	 */
	public Polygon visibilityPolygon(GalleryInstance gi) {
		TreeMap<Double, Coordinate> vPolygonCoords = new TreeMap<Double, Coordinate>();
		goOver(gi, vPolygonCoords, -1);
		for(int i = 0; i < gi.getHoles().size(); ++i) {
			goOver(gi, vPolygonCoords, i);
		}
		ArrayList<Coordinate> vertices = new ArrayList<Coordinate>();
		vertices.addAll(vPolygonCoords.values());
		return new Polygon(vertices);
	}
	
	public Polygon visibilityPolygon2(GalleryInstance gi) {
		TreeSet<PolarPoint> pointSet = new TreeSet<>(new Comparator<PolarPoint>() {
			@Override
			public int compare(PolarPoint o1, PolarPoint o2) {
				return Double.compare(o1.angle(), o2.angle());
			}
		});
		addPoints(pointSet, gi.getVertices(), -1);
		int nOfHoles = gi.getHoles().size();
		for(int i = 0; i < nOfHoles; ++i) {
			addPoints(pointSet, gi.getHoles().get(i).getVertices(), i);
		}
		
		return null;
	}

	private void addPoints(TreeSet<PolarPoint> pointSet, List<Coordinate> vertices, int hole) {
		int vSize = vertices.size();
		LineSegment ls1 = new LineSegment(this, vertices.get(0));
		PolarPoint point1 =  new PolarPoint(vertices.get(0), ls1.angle(), hole);
		pointSet.add(point1);
		double angle1 = ls1.angle();
		for(int i = 1; i <= vSize; ++i) {
			LineSegment ls2 = new LineSegment(this, vertices.get(i % vSize));
			double angle2 = ls2.angle();
			PolarPoint point2 = new PolarPoint(vertices.get(i % vSize), ls1.angle(), hole);
			pointSet.add(point2);
			if(angle1 < angle2) {
				point1.setStarts(i - 1);
				point2.setEnds(i - 1);
			} else {
				point1.setEnds(i - 1);
				point2.setStarts(i - 1);
			}
			ls1 = ls2;
			point1 = point2;
			angle1 = angle2;
		}
	}

	private void goOver(GalleryInstance gi, TreeMap<Double, Coordinate> vPolygonCoords, int n) {
		List<Coordinate> ends = (n == -1) ? gi.getVertices() : gi.getHoleOnIndex(n).getVertices();
		for(int i = 0; i < ends.size(); ++i) {
			Coordinate c = ends.get(i);
			LineSegment ls = new LineSegment(this, c);
			LineSegment lst1 = calculateRotated(ls, ALPHA);
			LineSegment lst2 = calculateRotated(ls, -ALPHA);
			Coordinate best;
			if((best = findClosest(gi, ls)) != null) vPolygonCoords.put(ls.angle(), best);
			if((best = findClosest(gi, lst1)) != null )vPolygonCoords.put(lst1.angle(), best);
			if((best = findClosest(gi, lst2)) != null )vPolygonCoords.put(lst2.angle(), best);
		}
	}

	private Coordinate findClosest(GalleryInstance gi, LineSegment ls) {
		int size = gi.getVertices().size();
		double mindist = -1;
		Coordinate minCoord = null;
		for(int i = 0; i < size; ++i) {
			LineSegment side = new LineSegment(gi.getVertices().get(i % size), 
					gi.getVertices().get((i + 1) % size));
			Coordinate is;
			if((is = (ls.lineIntersection(side))) == null) continue;
			is  = Maths.cRound(is);
			double dist = is.distance(ls.p0);
			if((mindist == -1 || dist < mindist) && side.distance(is) < EPSILON && 
					new Vector2D(ls.p0, ls.p1).dot(new Vector2D(ls.p0, is)) >= 0) {
				mindist = dist;
				minCoord = is;
			}
		}
		for(Polygon hole : gi.getHoles()) {
			size = hole.getVertices().size();
			for(int i = 0; i < size; ++i) {
				LineSegment side = new LineSegment(hole.getVertices().get(i % size),
					hole.getVertices().get((i + 1) % size));
				Coordinate is = Maths.cRound(ls.lineIntersection(side));
				double dist = is.distance(ls.p0);
				if((mindist == -1 || dist < mindist) && side.distance(is) < EPSILON && 
						//so that is on the right side
						new Vector2D(ls.p0, ls.p1).dot(new Vector2D(ls.p0, is)) >= 0) {
					mindist = dist;
					minCoord =is;
				}
			}
		}
		return minCoord;
	}

	/**
	 * Method rotating line for d gradients.
	 * returns LineSegment with the same starting point as passed segment.
	 * @param ls line segment to rotate
	 * @param d gradients to rotate for.
	 * @return new rotated line segment.
	 */
	private LineSegment calculateRotated(LineSegment ls, double d) {
		double vx = ls.p1.x - ls.p0.x;
		double vy = ls.p1.y - ls.p0.y;
		double cos = Math.cos(d);
		double sin = Math.sin(d);
		double rx = vx * cos - vy * sin;
		double ry = vx * sin + vy * cos;
		double nx = ls.p0.x + rx;
		double ny = ls.p0.y + ry;
		return new LineSegment(ls.p0, Maths.cRound(new Coordinate(nx, ny)));
	}
	
	public String toString() {
		return "" + this.x + " " + this.y;
	}
	
	public static class PolarPoint {
		double polar;
		ArrayList<Integer> starts = new ArrayList<>();
		ArrayList<Integer> ends = new ArrayList<>();
		int hole;
		Coordinate c;
		
		public PolarPoint(Coordinate c, double polar, int hole) {
			this.c = c;
			this.polar = polar;
			this.hole = hole;
		}

		public void setStarts(int i){
			this.starts.add(i);
		}
		
		public void setEnds(int i) {
			this.ends.add(i);
		}
		
		public ArrayList<Integer> starts() {
			return starts;
		}
		
		public ArrayList<Integer> ends() {
			return ends;
		}
		
		public double angle() {
			return polar;
		}
		
		public int hole() {
			return hole;
		}
	}
}
