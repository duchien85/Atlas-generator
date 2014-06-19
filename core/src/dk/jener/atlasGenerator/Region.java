/*
 * Atlas generator is utility to generate .atlas files for a sprite sheet images that's compatible with LibGDX
 * Copyright (C) 2014  Jacob Jenner Rasmussen <http://jener.dk/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dk.jener.atlasGenerator;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Region implements ShapeDrawable {
	private static int id = 0;

	private int regionX1, regionX2, regionY1, regionY2;
	private String name;
	private boolean active = true;

	public Region(int regionX1, int regionX2, int regionY1, int regionY2) {
		this.regionX1 = regionX1;
		this.regionX2 = regionX2;
		this.regionY1 = regionY1;
		this.regionY2 = regionY2;
		this.name = "region " + id;
		id++;
	}

	public Region(Region region) {
		this(region.getRegionX1(), region.getRegionX2(), region.getRegionY1(), region.getRegionY2());
	}

	public int getRegionX1() {
		return regionX1;
	}

	public void setRegionX1(int regionX1) {
		this.regionX1 = regionX1;
	}

	public int getRegionX2() {
		return regionX2;
	}

	public void setRegionX2(int regionX2) {
		this.regionX2 = regionX2;
	}

	public int getRegionY1() {
		return regionY1;
	}

	public void setRegionY1(int regionY1) {
		this.regionY1 = regionY1;
	}

	public int getRegionY2() {
		return regionY2;
	}

	public void setRegionY2(int regionY2) {
		this.regionY2 = regionY2;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getWidth() {
		return getUpperX()-getLowerX();
	}

	public int getHeight() {
		return getUpperY()-getLowerY();
	}

	public int getLowerX() {
		return Math.min(getRegionX1(), getRegionX2());
	}

	public int getLowerY() {
		return Math.min(getRegionY1(), getRegionY2());
	}

	public int getUpperX() {
		return Math.max(getRegionX1(), getRegionX2());
	}

	public int getUpperY() {
		return Math.max(getRegionY1(), getRegionY2());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = (name.trim().equals("")) ? "region" : name.trim();
	}

	public void draw(ShapeRenderer shapeRenderer, AtlasImg atlasImg) {
		if (active) {
			//top
			shapeRenderer.line(
					atlasImg.fromAtlasImageCordX(regionX1),
					atlasImg.fromAtlasImageCordY(regionY1),
					atlasImg.fromAtlasImageCordX(regionX2)+ atlasImg.getZoom(),
					atlasImg.fromAtlasImageCordY(regionY1));
			//left
			shapeRenderer.line(
					atlasImg.fromAtlasImageCordX(regionX1),
					atlasImg.fromAtlasImageCordY(regionY1),
					atlasImg.fromAtlasImageCordX(regionX1),
					atlasImg.fromAtlasImageCordY(regionY2)+ atlasImg.getZoom());
			//bottom
			shapeRenderer.line(
					atlasImg.fromAtlasImageCordX(regionX1),
					atlasImg.fromAtlasImageCordY(regionY2)+ atlasImg.getZoom(),
					atlasImg.fromAtlasImageCordX(regionX2)+ atlasImg.getZoom(),
					atlasImg.fromAtlasImageCordY(regionY2)+ atlasImg.getZoom());
			//right
			shapeRenderer.line(
					atlasImg.fromAtlasImageCordX(regionX2)+ atlasImg.getZoom(),
					atlasImg.fromAtlasImageCordY(regionY1),
					atlasImg.fromAtlasImageCordX(regionX2)+ atlasImg.getZoom(),
					atlasImg.fromAtlasImageCordY(regionY2)+ atlasImg.getZoom());
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
