package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.GroupLayout.ParallelGroup;

public class Screen extends JFrame{

	private final int minX = 500, minY = 500;
	
	private String fromDir,toDir;
	
	public Screen(String fromDirectory, String toDirectory){
		this.fromDir = fromDirectory;
		this.toDir = toDirectory;
		initComponents();
		
	}
	
	public void initComponents(){
		
		setTitle("Starcraft Moderator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLayout(new FlowLayout());
		
		//GRAPH
		JPanel graphPanel = new JPanel();
			
		//RIGHT SIDE
		JPanel rightSidePanel = new JPanel();
		rightSidePanel.setLayout(new BoxLayout(rightSidePanel,BoxLayout.Y_AXIS));
		
		//FROM DIR GROUP
		JPanel fromDirPanel = new JPanel();
		fromDirPanel.setLayout(new SpringLayout());
		
		//TO DIR GROUP
		JPanel toDirPanel = new JPanel();
		toDirPanel.setLayout(new SpringLayout());
		
		//OPTIONS GROUP
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridLayout());
		
		//START BUTTON
		JButton startButton = new JButton("Start");
		
		//PUTTING IT ALL TOGETHER
		rightSidePanel.add(fromDirPanel);
		rightSidePanel.add(toDirPanel);
		rightSidePanel.add(optionsPanel);
		rightSidePanel.add(startButton);
		
		add(graphPanel);
		add(rightSidePanel);
		
		setMinimumSize(new Dimension(minX,minY));
				
		
		pack();
		setVisible(true);
		
	}
	
}
