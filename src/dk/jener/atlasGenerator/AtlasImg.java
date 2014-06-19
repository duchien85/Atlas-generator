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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class AtlasImg extends Actor {
	private Texture texture;
	private int zoom = 1;

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
		setSize(texture.getWidth(), texture.getHeight());
		center();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (texture != null) batch.draw(texture, getX(), getY(), getWidth()*zoom, getHeight()*zoom);
	}

	public void changeZoom(int change) {
		zoom = (zoom+change <= 0) ? 1 : zoom+change;
	}

	public int getZoom() {
		return zoom-1;
	}

	public int toAtlasImageCordX(int x) {
		return (x-(int) getX())/zoom;
	}

	public int toAtlasImageCordY(int y) {
		return (y-(int) getY())/zoom;
	}

	public int fromAtlasImageCordX(int x) {
		return (int) getX()+(x*zoom);
	}

	public int fromAtlasImageCordY(int y) {
		return (int) getY()+(y*zoom);
	}
}
