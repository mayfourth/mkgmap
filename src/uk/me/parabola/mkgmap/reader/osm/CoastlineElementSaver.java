/*
 * Copyright (C) 2010, 2012.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 or
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 */
package uk.me.parabola.mkgmap.reader.osm;

import uk.me.parabola.util.EnhancedProperties;

/**
 * This saver only keeps ways with <code>natural=coastline</code> tags. 
 * This is used for loading of extra coastline files. 
 * @author WanMil
 */
public class CoastlineElementSaver extends ElementSaver {

	public CoastlineElementSaver(EnhancedProperties args) {
		super(args);
	}

	public void addNode(Node node) {
	}

	public void addWay(Way way) {
		String tag = way.getTag("natural");
		if (tag != null && tag.contains("coastline")) {
			// remove all tags => the natural=coastline is implicitly known
			way.removeAllTags();
			super.addWay(way);
		}
	}

	public void addRelation(Relation rel) {
	}

	public void convert(OsmConverter converter) {
	}
}