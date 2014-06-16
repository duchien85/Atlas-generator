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

package dk.jener.atlasGenerator.desktop;

import dk.jener.atlasGenerator.Window;

import javax.swing.*;

public class DesktopLauncher {

	public static void main (String[] arg) {
		/*LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = true;
		config.initialBackgroundColor = new Color(0);
		config.title = "Atlas generator";
		config.width = Common.WIDTH;
		config.height = Common.HEIGHT;*/

		//new LwjglAWTCanvas(new Engine());
		//Gdx.app.setLogLevel(Application.LOG_DEBUG);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Window window = new Window();
				window.setVisible(true);
			}
		});
	}
}
