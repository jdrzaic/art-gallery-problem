package metaheuristics.project.agp.alg.greedy.heuristics;

import java.util.HashMap;
import java.util.HashSet;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import metaheuristics.project.agp.instances.components.Camera;

public abstract class Heuristic {

	public abstract double utilValue(Polygon polygon, HashMap<Camera, Polygon> cover,
			GeometryFactory gf);
}
