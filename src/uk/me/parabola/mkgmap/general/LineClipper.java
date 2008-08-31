/*
 * Copyright (C) 2008 Steve Ratcliffe
 * 
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2 as
 *  published by the Free Software Foundation.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 * 
 * Author: Steve Ratcliffe
 * Create date: 30-Jun-2008
 */
package uk.me.parabola.mkgmap.general;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import uk.me.parabola.imgfmt.app.Area;
import uk.me.parabola.imgfmt.app.Coord;

/**
 * Routine to clip a polyline to a given bounding box.
 * @author Steve Ratcliffe
 */
public class LineClipper {

	/**
	 * Clips a polyline by the given bounding box.  This may produce several
	 * separate lines if the line meanders in and out of the box.
	 * This will work even if no point is actually inside the box.
	 * @param a The bounding area.
	 * @param coords A list of the points in the line.
	 * @return Returns null if the line is completely in the bounding box and
	 * this is expected to be the normal case.
	 * If clipping is needed then an array of point lists is returned.
	 */
	public static List<List<Coord>> clip(Area a, List<Coord> coords) {
		if (a == null)
			return null;

		// If all the points are inside the box then we just return null
		// to show that nothing was done and the line can be used.  This
		// is expected to be the normal case.
		boolean foundOutside = false;
		for (Coord co : coords) {
			if (!a.contains(co)) {
				foundOutside = true;
				break;
			}
		}
		if (!foundOutside)
			return null;

		List<List<Coord>> ret = new ArrayList<List<Coord>>();
		List<Coord> nlist = new ArrayList<Coord>();
		ret.add(nlist);

		Iterator<Coord> it = coords.iterator();
		Coord last = it.next();
		while (it.hasNext()) {
			Coord co = it.next();
			Coord[] ends = {last, co};
			ends = clip(a, ends);
			if (ends == null) {
				last = co;
				continue;
			}
			if (last.equals(ends[0])) {
				if (nlist.isEmpty())
					nlist.add(last);
			} else {
				// Need to start a new one
				nlist = new ArrayList<Coord>();
				ret.add(nlist);
				nlist.add(ends[0]);
			}
			nlist.add(ends[1]);
			last = co;
		}
		return ret;  
	}

	/**
	 * A straight forward implementation of the Liang-Barsky algorithm as described
	 * in the referenced web page.
	 * @param a The clipping area.
	 * @param ends The start and end of the line the contents of this will
	 * be changed if the line is clipped to contain the new start and end
	 * points.  A point that was inside the box will not be changed.
	 * @return An array of the new start and end points if any of the line is
	 * within the box.  If the line is wholy outside then null is returned.
	 * If a point is within the box then the same coordinate object will
	 * be returned as was passed in.
	 * @see <a href="http://www.skytopia.com/project/articles/compsci/clipping.html">Liang-Barsky algorithm</a>
	 */
	private static Coord[] clip(Area a, Coord[] ends) {
		double d = 0.00001;
		assert ends.length == 2;

		double x1 = ends[0].getLongitude();
		double y1 = ends[0].getLatitude();

		double x2 = ends[1].getLongitude();
		double y2 = ends[1].getLatitude();

		double dx = x2 - x1;
		double dy = y2 - y1;

		double[] t = {0, 1};

		double p, q;

		p = -dx;
		q = -(a.getMinLong() - x1);
		boolean scrap = checkSide(t, p, q);
		if (scrap) return null;

		p = dx;
		q = a.getMaxLong() - x1;
		scrap = checkSide(t, p, q);
		if (scrap) return null;

		p = -dy;
		q = -(a.getMinLat() - y1);
		scrap = checkSide(t, p, q);
		if (scrap) return null;

		p = dy;
		q = a.getMaxLat() - y1;
		scrap = checkSide(t, p, q);
		if (scrap) return null;

		assert t[0] >= 0;
		assert t[1] <= 1;

		if (t[0] > 0)
			ends[0] = new Coord((int) (y1 + t[0] * dy + d), (int) (x1 + t[0] * dx + d));

		if (t[1] < 1)
			ends[1] = new Coord((int)(y1 + t[1] * dy + d), (int) (x1 + t[1] * dx + d));
		return ends;
	}

	private static boolean checkSide(double[] t, double p, double q) {
		double r = q/p;

		if (p == 0 && q < 0)
			return true;

		if (p < 0) {
			if (r > t[1])
				return true;
			else if (r > t[0])
				t[0] = r;
		} else {
			if (r < t[0])
				return true;
			else if (r < t[1])
				t[1] = r;
		}
		return false;
	}

	public static void main(String[] args) {
		Area a = new Area(60, 70, 150, 230);
		Coord[] co = new Coord[] {
				new Coord(20, 30),
				new Coord(160, 280),
		};

		clip(a, co);
	}
}