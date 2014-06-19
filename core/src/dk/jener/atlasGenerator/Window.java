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

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Window extends JFrame {
	LwjglAWTCanvas canvas;
	private static DefaultListModel<ShapeDrawable> listModel;
	private static JList<ShapeDrawable> list;
	private JFileChooser fileChooser;
	private static JMenuItem saveFile;

	public Window() {
		setTitle("Atlas generator");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(true);
		getContentPane().setLayout(new BorderLayout());

		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter(".png", "png"));

		createRegionList();
		createMenuBar();
		createCanvas();

		pack();
		setSize(900, 600);
		setLocationRelativeTo(null);
	}

	public void createRegionList() {
		final JPopupMenu menu = new JPopupMenu();

		//Region list
		listModel = new DefaultListModel<ShapeDrawable>();
		list = new JList<ShapeDrawable>(listModel);
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
		list.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent keyEvent) {}
			@Override
			public void keyPressed(KeyEvent keyEvent) {}

			@Override
			public void keyReleased(KeyEvent keyEvent) {
				switch (keyEvent.getKeyCode()) {
					case KeyEvent.VK_BACK_SPACE:
					case KeyEvent.VK_DELETE:
						if (!list.isSelectionEmpty()) listModel.remove(list.getSelectedIndex());
						return;
					case KeyEvent.VK_ENTER:
						showDialog(listModel.get(list.getSelectedIndex()));
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(200, Integer.MAX_VALUE));

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
				showDialog(listModel.get(list.getSelectedIndex()));
			}
		});
		menu.add(edit);

		//add region list to content pane
		getContentPane().add(scrollPane, BorderLayout.LINE_START);
	}

	public void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		//File Menu
		JMenu fileMenu = new JMenu("File");
		JMenuItem openFile = new JMenuItem("Open image");
		openFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (fileChooser.showOpenDialog(Window.this) == JFileChooser.APPROVE_OPTION) {
					Engine.setAtlas(fileChooser.getSelectedFile());
				}
			}
		});
		fileMenu.add(openFile);

		saveFile = new JMenuItem("Save atlas");
		saveFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (fileChooser.showSaveDialog(Window.this) == JFileChooser.APPROVE_OPTION) {
					Engine.saveAtlas(fileChooser.getSelectedFile());
				}
			}
		});
		saveFile.setEnabled(false);

		fileMenu.add(openFile);
		fileMenu.add(saveFile);
		menuBar.add(fileMenu);


		//Tools menu
		JMenu toolsMenu = new JMenu("Tools");
		JMenuItem createGrid = new JMenuItem("Create grid");
		createGrid.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				showGridDialog(new Grid(), true);
			}
		});
		toolsMenu.add(createGrid);
		menuBar.add(toolsMenu);


		//About menu
		JMenu generalMenu = new JMenu("General");
		JMenuItem licenseItem = new JMenuItem("license");
		licenseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				showLicenseDialog();
			}
		});
		generalMenu.add(licenseItem);
		menuBar.add(generalMenu);


		setJMenuBar(menuBar);
	}

	public void createCanvas() {
		canvas = new LwjglAWTCanvas(new Engine());
		getContentPane().add(canvas.getCanvas(), BorderLayout.CENTER);
	}

	public void showDialog(ShapeDrawable shapeDrawable) {
		if (shapeDrawable instanceof Region) showRegionDialog((Region) shapeDrawable);
		else if (shapeDrawable instanceof Grid) showGridDialog((Grid) shapeDrawable, false);
	}

	public void showRegionDialog(final Region region) {
		final JDialog dialog = new JDialog(this);
		dialog.setTitle("Edit region");
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(false);
		dialog.setSize(300, 60);
		dialog.setLayout(new GridBagLayout());
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

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

	public void showLicenseDialog() {
		final JDialog dialog = new JDialog(this);
		dialog.setTitle("License");
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(true);
		dialog.setMinimumSize(new Dimension(300, 300));
		dialog.setSize(650, 650);
		dialog.setLayout(new BorderLayout());
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


		JTextArea licensetextArea = new JTextArea();
		licensetextArea.setLineWrap(false);
		licensetextArea.setCaretPosition(0);
		licensetextArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(licensetextArea);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		dialog.add(scrollPane, BorderLayout.CENTER);

		dialog.setVisible(true);

		try {
			InputStream inputStream = Window.class.getClassLoader().getResourceAsStream("license.txt");
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader reader = new BufferedReader(inputStreamReader);

			String line;
			while ((line = reader.readLine()) != null) {
				licensetextArea.append(line + "\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showGridDialog(final Grid grid, final boolean isNew) {
		final JDialog dialog = new JDialog(this);
		dialog.setTitle("Grid");
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(false);
		dialog.setLayout(new GridBagLayout());
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(3, 3, 3, 3);

		dialog.add(new JLabel("Grid width"), c);
		c.gridx = 1;
		final JSpinner gridWidth = new JSpinner(new SpinnerNumberModel(grid.getGridWidth(), 1, Integer.MAX_VALUE, 1));
		gridWidth.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				grid.setGridWidth((Integer) gridWidth.getValue());
			}
		});
		dialog.add(gridWidth, c);

		c.gridx = 0;
		c.gridy = 1;
		dialog.add(new JLabel("Grid height"), c);
		c.gridx = 1;
		final JSpinner gridHeight = new JSpinner(new SpinnerNumberModel(grid.getGridHeight(), 1, Integer.MAX_VALUE, 1));
		gridHeight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				grid.setGridHeight((Integer) gridHeight.getValue());
			}
		});
		dialog.add(gridHeight, c);

		c.gridx = 0;
		c.gridy = 2;
		dialog.add(new JLabel("Cell width"), c);
		c.gridx = 1;
		final JSpinner cellWidth = new JSpinner(new SpinnerNumberModel(grid.getCellWidth(), 1, Integer.MAX_VALUE, 1));
		cellWidth.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				grid.setCellWidth((Integer) cellWidth.getValue());
			}
		});
		dialog.add(cellWidth, c);

		c.gridx = 0;
		c.gridy = 3;
		dialog.add(new JLabel("Cell height"), c);
		c.gridx = 1;
		final JSpinner cellHeight = new JSpinner(new SpinnerNumberModel(grid.getCellHeight(), 0, Integer.MAX_VALUE, 1));
		cellHeight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				grid.setCellHeight((Integer) cellHeight.getValue());
			}
		});
		dialog.add(cellHeight, c);

		c.gridx = 0;
		c.gridy = 4;
		dialog.add(new JLabel("Padding right"), c);
		c.gridx = 1;
		final JSpinner paddingRight = new JSpinner(new SpinnerNumberModel(grid.getPaddingRight(), 0, Integer.MAX_VALUE, 1));
		paddingRight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				grid.setPaddingRight((Integer) paddingRight.getValue());
			}
		});
		dialog.add(paddingRight, c);

		c.gridx = 0;
		c.gridy = 5;
		dialog.add(new JLabel("Padding left"), c);
		c.gridx = 1;
		final JSpinner paddingLeft = new JSpinner(new SpinnerNumberModel(grid.getPaddingLeft(), 0, Integer.MAX_VALUE, 1));
		paddingLeft.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				grid.setPaddingLeft((Integer) paddingLeft.getValue());
			}
		});
		dialog.add(paddingLeft, c);

		c.gridx = 0;
		c.gridy = 6;
		dialog.add(new JLabel("Padding top"), c);
		c.gridx = 1;
		final JSpinner paddingTop = new JSpinner(new SpinnerNumberModel(grid.getPaddingTop(), 0, Integer.MAX_VALUE, 1));
		paddingTop.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				grid.setPaddingTop((Integer) paddingTop.getValue());
			}
		});
		dialog.add(paddingTop, c);

		c.gridx = 0;
		c.gridy = 7;
		dialog.add(new JLabel("Padding bottom"), c);
		c.gridx = 1;
		final JSpinner paddingBottom = new JSpinner(new SpinnerNumberModel(grid.getPaddingBottom(), 0, Integer.MAX_VALUE, 1));
		paddingBottom.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				grid.setPaddingBottom((Integer) paddingBottom.getValue());
			}
		});
		dialog.add(paddingBottom, c);

		c.gridx = 0;
		c.gridy = 8;
		dialog.add(new JLabel("Name"), c);
		c.gridx = 1;
		final JTextField nameTextField = new JTextField(grid.getName());
		nameTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent documentEvent) {
				change();
			}

			@Override
			public void removeUpdate(DocumentEvent documentEvent) {
				change();
			}

			@Override
			public void changedUpdate(DocumentEvent documentEvent) {
				change();
			}

			public void change() {
				grid.setName(nameTextField.getText());
			}
		});
		dialog.add(nameTextField, c);

		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		JButton doneButton = new JButton("Done");
		doneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if (isNew) Window.getListModel().addElement(grid);
				dialog.dispose();
			}
		});
		dialog.add(doneButton, c);

		dialog.pack();
		dialog.setVisible(true);
	}

	public static DefaultListModel<ShapeDrawable> getListModel() {
		return listModel;
	}

	public static JList<ShapeDrawable> getList() {
		return list;
	}

	public static JMenuItem getSaveFile() {
		return saveFile;
	}
}
