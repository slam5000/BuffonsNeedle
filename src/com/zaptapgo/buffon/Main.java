package com.zaptapgo.buffon;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.Date;
import java.util.Random;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.zaptapgo.buffon.gui.ZoomAndPanCanvas;
import com.zaptapgo.buffon.needle.Counter;
import com.zaptapgo.buffon.needle.Needle;

public class Main implements ActionListener {
	
	public static final int FRAME_DELAY = 20; //A frame delay of 20 is approximately 50 frames per second
	
	public static final int MAXIMUM_DRAWN_NEEDLES = 1000;
	
	public static final int MAX_NEEDLES_PER_SECOND = 1000000;
	
	public static final boolean DEBUG_LOG_PI_AT_INTERVAL = true;
	
	public static int needlesPerSecond = 0;
	
	public static int needleDiameter = 100;
	
	public static Random rand = new Random();
	
	public static JFrame tools;
	
	public static JFrame frame;
	
	public static JFrame results;
	
	public static JFrame grid;
	
	public static JLabel count;
	
	public static JLabel value;
	
	public static JLabel pi;
	
	public static JLabel onTarget;
	
	public static JLabel offTarget;
	
	public static JTextField nLength;
	
	public static JLabel percent;
	
	public static int gridMode = 0;
	
	public static void main(String[] args) {
		//Create the GUI
		frame = new JFrame("Buffon");
        tools = new JFrame("Tools");
        results = new JFrame("Results");
        grid = new JFrame("Grid Type");
        Box buttonsBox = Box.createHorizontalBox();
        ButtonGroup group = new ButtonGroup();
        JRadioButton sgrid = new JRadioButton("Standard");
        sgrid.setActionCommand("0");
        sgrid.setSelected(true);
        JRadioButton polar = new JRadioButton("Polar (broken)");
        polar.setActionCommand("1");
        JRadioButton hgrid = new JRadioButton("Full Grid");
        hgrid.setActionCommand("2");
        group.add(sgrid);
        group.add(hgrid);
        group.add(polar);
        buttonsBox.add(sgrid);
        buttonsBox.add(polar);
        buttonsBox.add(hgrid);
        Main main = new Main();
        sgrid.addActionListener(main);
        polar.addActionListener(main);
        hgrid.addActionListener(main);
        JPanel gridPanel = new JPanel();
        gridPanel.add(buttonsBox);
        grid.add(gridPanel);
        grid.setVisible(true);
        grid.setAlwaysOnTop(true);
        tools.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        results.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JPanel panel = new JPanel();
        Box uiPanel = Box.createVerticalBox();
        count = new JLabel("Needles: 0");
        JSlider slider = new JSlider(0, MAX_NEEDLES_PER_SECOND, 0);
        slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
					Main.needlesPerSecond = (int)source.getValue();
					//System.out.println(needlesPerSecond);
			    }
				
			}
			
		});
        JButton clear = new JButton("Clear");
        clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Needle.needles.clear();
				Counter.offTarget = 0;
				Counter.onTarget = 0;
				Counter.total = 0;
			}
		});
        
        JButton calculate = new JButton("Calculate");
        calculate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread calculate = new Thread(new Counter(false));
				calculate.setPriority(Thread.MAX_PRIORITY);
				calculate.run();
			}
			
		});
        
        Box sliderBox = Box.createHorizontalBox();
        JLabel min = new JLabel("0");
        JLabel max = new JLabel(String.valueOf(MAX_NEEDLES_PER_SECOND));
        sliderBox.add(min);
        sliderBox.add(slider);
        sliderBox.add(max);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ZoomAndPanCanvas canvas = new ZoomAndPanCanvas();
        final ZoomAndPanCanvas canvasFinal = canvas;
        value = new JLabel("Needles per second: 0");
        value.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        count.setAlignmentY(JLabel.CENTER_ALIGNMENT);
        uiPanel.add(value, BorderLayout.WEST);
        uiPanel.add(sliderBox);
        nLength = new JTextField("100");
        nLength.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				 String text = Main.nLength.getText();
				 int newNeedleLength = 0;
				 try {
					 newNeedleLength = Integer.parseInt(text);
				 } catch (NumberFormatException e) {
					 UIManager.getLookAndFeel().provideErrorFeedback(Main.nLength); 
					 return;
				 }
				 if (newNeedleLength <= 0 && newNeedleLength < 1000000) {
					 Main.nLength.setText(String.valueOf(Main.needleDiameter));
					 UIManager.getLookAndFeel().provideErrorFeedback(Main.nLength); 
					 return;
				 }
				 Main.needleDiameter = newNeedleLength;
			}
		});
        Box buttons = Box.createHorizontalBox();
        buttons.add(calculate, BorderLayout.WEST);
        buttons.add(clear, BorderLayout.WEST);
        uiPanel.add(nLength, BorderLayout.CENTER);
        uiPanel.add(buttons, BorderLayout.CENTER);
        panel.add(uiPanel);
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        
        tools.add(uiPanel);
        tools.pack();
        tools.setVisible(true);
        tools.setAlwaysOnTop(true);
        tools.setResizable(false);
        
        //Results creation
        Box resultsBox = Box.createVerticalBox();
        JPanel resultsPanel = new JPanel();
        onTarget = new JLabel("On Target: 0");
        offTarget = new JLabel("Off Target: 0");
        percent = new JLabel("Percent Hit: ?");
        pi = new JLabel("Appx Pi: ???");
        resultsBox.add(count, BorderLayout.WEST);
        resultsBox.add(onTarget, BorderLayout.WEST);
        resultsBox.add(offTarget, BorderLayout.WEST);
       // results.add(pi, BorderLayout.WEST);
        resultsBox.add(percent, BorderLayout.WEST);
        resultsBox.add(pi, BorderLayout.WEST);
        resultsPanel.add(resultsBox);
        results.add(resultsPanel);
        results.setAlwaysOnTop(true);
        results.setResizable(false);
        results.pack();
        results.setVisible(true);
        
        
        Thread gameThread = new Thread(new GameLoop(canvas));
        gameThread.setPriority(Thread.MAX_PRIORITY);
        gameThread.start();
        
        frame.addKeyListener(new KeyListener() {
		
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == 'm'){
					System.out.println("\nThe current matrix for the ZoomAndPanCanvas is as follows:");
					canvasFinal.zoomAndPanListener.showMatrix(canvasFinal.zoomAndPanListener.getCoordTransform());
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
        frame.setVisible(true);
    }
	
	private static class GameLoop implements Runnable {
		
		/**
		 * Is the Main render loop running
		 */
		boolean isRunning;
		/**
		 * The Main Canvas on which to draw the simulations output
		 */
		ZoomAndPanCanvas gui;
		/**
		 * A time adjustment made to ensure a constant FPS (frames per second)
		 */
		long cycleTime;
		/**
		 * Incremented each frame and reset at 50, used to determine one
		 * seccond without increasing computation time assuming that the FPS is constant
		 */
		private int increment;

		/**
		 * The last time an update was called
		 */
	    long lastTime = 0L;
		
	    /**
	     * Creates a new GameLoop object representing the main render, and logic loop
	     * @param canvas The canvas to render to
	     */
		public GameLoop(ZoomAndPanCanvas canvas){
			this.gui = canvas;
			this.isRunning = true;
		}

		@Override
		public void run() {
			cycleTime = System.currentTimeMillis();
			gui.createBufferStrategy(2);
			BufferStrategy buffer = gui.getBufferStrategy();
			while(isRunning){
				doWorldTick();
				updateGui(buffer);
				synchFramerate();
			}
		}
		
		/**
		 * Sends a message to the Java.awt render Thread to draw the current
		 * output to the screen.
		 * @param strat The Bufferstrategy to draw to
		 */
		private void updateGui(BufferStrategy strat) {
			ZoomAndPanCanvas canvas = ZoomAndPanCanvas.maincanvas;
			canvas.repaint();			
		}
		
		/**
		 * Delays the next tick until a certain time has passed
		 * to ensure a constant FPS
		 */
		private void synchFramerate() {
			cycleTime = cycleTime + FRAME_DELAY;
			long difference = cycleTime - System.currentTimeMillis();
			try {
				Thread.sleep(Math.max(0, difference));
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Does one tick of the main logic loop
		 */
		private void doWorldTick(){
			int repetitions = 0;
			if (Main.needlesPerSecond > 50) {
				repetitions = (int)((float) needlesPerSecond / 50f);
			} else {
				if ((new Date()).getTime() - lastTime >= 1000) {
					lastTime = (new Date()).getTime();
					repetitions = needlesPerSecond;
				} else {
					repetitions = 0;
				}
			}
			for (int i = 0; i < repetitions; i++) {
				//Generate random doubles from 0 - 1 and adjust the value so it lies
				//as a more prescise decimal value in the range of -10000 to 10000
				double x = (rand.nextDouble() * (10001 + 10000)) - 10000;
				double y = (rand.nextDouble() * (10001 + 10000)) - 10000;
				//Generate a random angle from 0 - 360 degrees, from a random
				//double from 0-1 for added prescision
				double a = (rand.nextDouble() * 360);
				//Get the point one needle length away from the first point,
				//when traveling in angle a
				double x2 = (x + needleDiameter * Math.cos(Math.toRadians(a)));
				double y2 = (y + needleDiameter * Math.sin(Math.toRadians(a)));
				//Create a new needle using those coordinates
				Needle n = new Needle(x, y, x2, y2);
				Counter.total++;
				switch (gridMode) {
				case 0:
					//Old intersection checking formula
					//O = 2^n
					/*for (int h = -10000; h <= 10000; h += 100) {
						if ((n.x1 < h && n.x2 > h) || (n.x1 > h && n.x2 < h)) {
							Counter.onTarget++;
							break;
						}
					} */
					
					//New faster intersection checking formula, would work on a
					//infinetely expanding grid, and requires no loops,
					// O = 2
					if (Util.floorToMultiple(n.x1, 100) != Util.floorToMultiple(n.x2, 100)) { 
						Counter.onTarget++;
					}
					break;
				case 1:
					//INACURATE! This is a rough approximation of if th eneedle
					//crosses the polar grid, this is not exact, and with larger
					//needles, the inacuracies become more apparent.
					for (int h = 100; h <= 10000; h += 100) {
						if (((n.x1 * n.x1) + (n.y1 * n.y1) > (h * h) &&
								(n.x2 * n.x2) + (n.y2 * n.y2) < (h * h)) ||
								((n.x1 * n.x1) + (n.y1 * n.y1) < (h * h) &&
								(n.x2 * n.x2) + (n.y2 * n.y2) > (h * h))) {
							Counter.onTarget++;
							break;
						}
					}
					break;
				case 2:
					//Check if needle crosses on y axis
					boolean flag = Util.floorToMultiple(n.x1, 100) !=
							Util.floorToMultiple(n.x2, 100);
					//Check if needle crosses on x axis
					boolean flag2 = Util.floorToMultiple(n.y1, 100) !=
							Util.floorToMultiple(n.y2, 100);
					if (flag && flag2) {
						Counter.onTarget++;
					}
					break;
				}
			}
			//Update the GUI
			increment++;
			if (increment >= 50) {
				//Do this only once every second due to large computational complexity (Multiple
				//costly division operations)
				Thread calculate = new Thread(new Counter(true));
				//Set to Minimum priority so as not to interfere with the display of the simulation
				calculate.setPriority(Thread.MIN_PRIORITY);
				calculate.run();
				increment = 0;
			}
			Main.count.setText("Needles: " + Counter.total);
			Main.tools.setLocation(Main.frame.getX() + Main.frame.getSize().width - Main.tools.getSize().width,
					Main.frame.getLocation().y);
			Main.value.setText("Needles per second: " + Main.needlesPerSecond);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//The radio button grid type selection was changed, modify the grid type according
		//to the message recieved, and update the grid appropriately.
		System.out.println("Changed to " + e.getActionCommand());
		int newGrid = Integer.parseInt(e.getActionCommand());
		gridMode = newGrid;
	}
	
	
}
