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

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.File;

public class Engine extends ApplicationAdapter {
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer1;
	private Texture img;
	private static ScreenViewport viewport;
	private Stage stage;

	private static File file;
	private static boolean filePendingLoading;
	private static AtlasImg atlasImg;

	private static Region region = new Region(0, 0, 0, 0);

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer1 = new ShapeRenderer();

		viewport = new ScreenViewport(new OrthographicCamera());

		stage = new Stage(viewport, batch);

		atlasImg = new AtlasImg();
		stage.addActor(atlasImg);
		atlasImg.setPosition(100, 100);

		Gdx.input.setInputProcessor(new InputHandler());
	}

	int selected;

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		loadAtlas();

		stage.act();
		stage.draw();

		selected = Window.getList().getSelectedIndex();

		shapeRenderer1.setProjectionMatrix(viewport.getCamera().combined);
		shapeRenderer1.setColor(1, 0, 0, 1);
		shapeRenderer1.begin(ShapeRenderer.ShapeType.Line);
		for (int i = 0; i < Window.getListModel().size(); i++) {
			if (i != selected)Window.getListModel().get(i).draw(shapeRenderer1, atlasImg);
		}
		shapeRenderer1.end();

		shapeRenderer1.setColor(0, 0, 1, 1);
		shapeRenderer1.begin(ShapeRenderer.ShapeType.Line);
		region.draw(shapeRenderer1, atlasImg);
		shapeRenderer1.end();

		if(!Window.getList().isSelectionEmpty()) {
			shapeRenderer1.setColor(0, 1, 0, 1);
			shapeRenderer1.begin(ShapeRenderer.ShapeType.Line);
			Window.getListModel().get(selected).draw(shapeRenderer1, atlasImg);
			shapeRenderer1.end();
		}
	}

	public void loadAtlas() {
		if (filePendingLoading) {
			img = new Texture(Gdx.files.absolute(file.getAbsolutePath()));
			atlasImg.setTexture(img);
			filePendingLoading = false;
			Gdx.app.debug("debug", "loaded image");

			Window.getSaveFile().setEnabled(true);
		}
	}

	public static void setAtlas(File file) {
		Engine.file = file;
		filePendingLoading = true;
		clearRegions();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void dispose() {
		if (img != null) img.dispose();
		stage.dispose();
		batch.dispose();
	}

	public static AtlasImg getAtlasImg() {
		return atlasImg;
	}

	public static Region getRegion() {
		return region;
	}

	public static void addCurrentRegion() {
		Window.getListModel().addElement(new Region(region));
	}

	public static void clearRegions() {
		Window.getListModel().clear();
	}

	public static void saveAtlas(File atlasFile) {
		String atlasText = file.getName() + "\n" +
				"size: " + atlasImg.getTexture().getWidth() + "," + atlasImg.getTexture().getHeight() + "\n" +
				"format: RGBA8888\n" +
				"filter: Nearest,Nearest\n" +
				"repeat: none";

		//ToDo: Update the export code to support grids
		Region region;
		Grid grid;
		for (int i = 0; i < Window.getListModel().size(); i++) {
			ShapeDrawable shapeDrawable = Window.getListModel().get(i);
			if (shapeDrawable instanceof Region) {
				region = (Region) shapeDrawable;
				atlasText += "\n" +
						region.getName() + "\n" +
						"  rotate: false\n" +
						"  xy: " + region.getLowerX() + ", " + region.getLowerY() + "\n" +
						"  size: " + region.getWidth() + ", " + region.getHeight() + "\n" +
						"  orig: 0, 0\n" +
						"  offset: 0, 0\n" +
						"  index: -1";
			} else if (shapeDrawable instanceof Grid) {
				grid = (Grid) shapeDrawable;
				for (int x = 0; x < grid.getGridWidth(); x++) {
					for (int y = 0; y < grid.getGridHeight(); y++) {
						atlasText += "\n" +
								grid.getName() + (x*grid.getGridHeight()+y) + "\n" +
								"  rotate: false\n" +
								"  xy: " + ((grid.getPaddingLeft() + grid.getCellWidth() + grid.getPaddingRight()) * x + grid.getPaddingLeft()) + ", " + (grid.getPaddingTop() + grid.getCellHeight() + grid.getPaddingBottom()) * y + grid.getPaddingBottom() + "\n" +
								"  size: " + grid.getCellWidth() + ", " + grid.getCellHeight() + "\n" +
								"  orig: 0, 0\n" +
								"  offset: 0, 0\n" +
								"  index: -1";
					}
				}
			}
		}

		Gdx.files.absolute(atlasFile.getAbsolutePath()).writeString(atlasText, false);
	}

	public static ScreenViewport getViewport() {
		return viewport;
	}
}
