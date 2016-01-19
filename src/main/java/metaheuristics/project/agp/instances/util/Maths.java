package metaheuristics.project.agp.instances.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.vividsolutions.jts.geom.Coordinate;

public class Maths {

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	public static Coordinate cRound(Coordinate c) {
		return new Coordinate(round(c.x, 10), round(c.y, 10));
	}
}
