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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Window extends JFrame {
	LwjglAWTCanvas canvas;
	private static DefaultListModel<Region> listModel;
	private static JList<Region> list;
	private JFileChooser fileChooser;

	public Window() {
		setTitle("Atlas generator");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

		//Button and list panel
		final JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setPreferredSize(new Dimension(200, 600));

		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter(".png", "png"));

		//Save file button
		JButton openFile = new JButton("Open file");
		openFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (fileChooser.showOpenDialog(Window.this) == JFileChooser.APPROVE_OPTION) {
					Engine.setAtlas(fileChooser.getSelectedFile());
				}
			}
		});
		openFile.setBounds(0, 0, 199, 30);
		panel.add(openFile);

		//Open file button
		JButton saveFile = new JButton("Save file");
		saveFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (fileChooser.showSaveDialog(Window.this) == JFileChooser.APPROVE_OPTION) {
					Engine.saveAtlas(fileChooser.getSelectedFile());
				}
			}
		});
		saveFile.setBounds(0, 30, 199, 30);
		panel.add(saveFile);

		final JPopupMenu menu = new JPopupMenu();


		//Region list
		listModel = new DefaultListModel<Region>();
		list = new JList<Region>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				check(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				check(e);
			}

			public void check(MouseEvent e) {
				if (e.isPopupTrigger()) { //if the event shows the menu
					if (!listModel.isEmpty()) {
						list.setSelectedIndex(list.locationToIndex(e.getPoint())); //select the item
						if (!list.isSelectionEmpty()) menu.show(list, e.getX(), e.getY()); //and show the menu
					}
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(0, 60, 199, 600 - 60);
		panel.add(scrollPane);
		//panel.add(list);
		panel.setBorder(BorderFactory.createLineBorder(Color.black));

		//Region list menu
		JMenuItem delete = new JMenuItem("Delete");
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				listModel.remove(list.getSelectedIndex());
			}
		});
		menu.add(delete);

		JMenuItem edit = new JMenuItem("Edit");
		edit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				final JDialog dialog = new JDialog(Window.this);
				dialog.setTitle("Edit region");
				dialog.setResizable(false);
				dialog.setSize(300, 60);
				dialog.setLayout(new GridBagLayout());
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

				final Region region = listModel.get(list.getSelectedIndex());

				GridBagConstraints c = new GridBagConstraints();
				c.gridheight = 1;
				c.gridwidth = 1;
				c.gridx = 0;
				c.gridy = 0;
				c.insets = new Insets(3,3,3,3);

				final JSpinner spinnerX1 = new JSpinner(new SpinnerNumberModel(region.getRegionX1(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
				spinnerX1.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent changeEvent) {
						region.setRegionX1((Integer) spinnerX1.getModel().getValue());
					}
				});
				dialog.add(spinnerX1, c);

				final JSpinner spinnerY1 = new JSpinner(new SpinnerNumberModel(region.getRegionY1(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
				spinnerY1.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent changeEvent) {
						region.setRegionY1((Integer) spinnerY1.getModel().getValue());
					}
				});
				c.gridx = 1;
				dialog.add(spinnerY1, c);

				final JSpinner spinnerX2 = new JSpinner(new SpinnerNumberModel(region.getRegionX2(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
				spinnerX2.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent changeEvent) {
						region.setRegionX2((Integer) spinnerX2.getModel().getValue());
					}
				});
				c.gridx = 0;
				c.gridy = 1;
				dialog.add(spinnerX2, c);

				final JSpinner spinnerY2 = new JSpinner(new SpinnerNumberModel(region.getRegionY2(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
				spinnerY2.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent changeEvent) {
						region.setRegionY2((Integer) spinnerY2.getModel().getValue());
					}
				});
				c.gridx = 1;
				dialog.add(spinnerY2, c);

				final JTextField textField = new JTextField(region.getName());
				textField.getDocument().addDocumentListener(new DocumentListener() {
					@Override
					public void insertUpdate(DocumentEvent documentEvent) {update();}
					@Override
					public void removeUpdate(DocumentEvent documentEvent) {update();}
					@Override
					public void changedUpdate(DocumentEvent documentEvent) {update();}

					private void update() {
						region.setName(textField.getText());
						list.repaint();
					}
				});
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = 2;
				c.gridx = 0;
				c.gridy = 2;
				dialog.add(textField, c);

				JButton button = new JButton("Done");
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						dialog.dispose();
					}
				});
				c.gridy = 3;
				dialog.add(button, c);

				dialog.setVisible(true);
				dialog.pack();
			}
		});
		menu.add(edit);

		getContentPane().add(panel, 0);

		//Libgdx
		canvas = new LwjglAWTCanvas(new Engine());
		canvas.getCanvas().setSize(700, 600);
		getContentPane().add(canvas.getCanvas(), 1);

		pack();
		setSize(900, 600);
	}

	@Override
	public void dispose() {
		super.dispose();
		Gdx.app.exit();
	}

	public static DefaultListModel<Region> getListModel() {
		return listModel;
	}

	public static JList<Region> getList() {
		return list;
	}
}
