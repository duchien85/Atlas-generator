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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class InputHandler implements InputProcessor {
	private int rightMouse = -1, leftMouse = -1;
	private int rightMouseStartX = 0, rightMouseStartY = 0;
	private float rightStartX = 0, rightStartY = 0;

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
			case Input.Keys.C:
				Engine.getAtlasImg().center();
				return true;
			default:
				return true;
		}
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == 1 && rightMouse == -1) {
			rightMouse = pointer;
			rightMouseStartX = screenX;
			rightMouseStartY = screenY;
			rightStartX = Engine.getAtlasImg().getX();
			rightStartY = Engine.getAtlasImg().getY();
			return true;
		}

		if (button == 0 && leftMouse == -1) {
			leftMouse = pointer;
			Engine.getRegion().setRegionX1(Engine.getAtlasImg().toAtlasImageCordX(screenX));
			Engine.getRegion().setRegionY1(Engine.getAtlasImg().toAtlasImageCordY((int) Engine.getViewport().getWorldHeight() - screenY));
			Engine.getRegion().setRegionX2(Engine.getAtlasImg().toAtlasImageCordX(screenX));
			Engine.getRegion().setRegionY2(Engine.getAtlasImg().toAtlasImageCordY((int) Engine.getViewport().getWorldHeight() - screenY));
			Engine.getRegion().setActive(true);
			return true;
		} else return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (rightMouse == pointer) {
			rightMouse = -1;
			return true;
		} else if (leftMouse == pointer) {
			leftMouse = -1;
			Engine.getRegion().setActive(false);
			Engine.addCurrentRegion();
			return true;
		} else return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (rightMouse == pointer) {
			Engine.getAtlasImg().setPosition(rightStartX -(rightMouseStartX -screenX), rightMouseStartY -screenY+ rightStartY);
			return true;
		} else if (leftMouse == pointer) {
			Engine.getRegion().setRegionX2(Engine.getAtlasImg().toAtlasImageCordX(screenX));
			Engine.getRegion().setRegionY2(Engine.getAtlasImg().toAtlasImageCordY((int) Engine.getViewport().getWorldHeight() - screenY));
			return true;
		} else return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		Engine.getAtlasImg().changeZoom(-amount);
		return true;
	}
}
