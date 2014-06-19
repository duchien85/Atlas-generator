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

public abstract class Actor extends com.badlogic.gdx.scenes.scene2d.Actor {

	public void center() {
		setPosition(Engine.getViewport().getWorldWidth()/2-getWidth()/2, Engine.getViewport().getWorldHeight()/2-getHeight()/2);
	}
}
