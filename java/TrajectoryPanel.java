import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.lang.Math.*;


public class TrajectoryPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener, ComponentListener {

	double theta = Math.PI / 4.0;
	double phi = Math.PI / 4.0;
	int newx = 0;
	int newy = 0;
	double[] vector;

	int maxcount = 200;

	double maxx;
	double maxy;
	double maxz;
	double[][] trajectory;
	double[][] axes;
	int[][] ptrajectory;
	double[] point;
	int[] ppoint;
	boolean pointflag;
	int[][] paxes;
	int[] panelSize;
	String[] label;

	public TrajectoryPanel(double[] x, double[] y, double[] z, double max, String l0, String l1, String l2) {

		setDoubleBuffered(true);
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);


		label = new String[3];
		label[0] = l0;
		label[1] = l1;
		label[2] = l2;
				
		change(x, y, z, max);
		
	}

	public boolean isFocusable() {
		return true;
	}
	
	public void change(double[] x, double[] y, double[] z, double max) {

		maxx = max;
		maxy = max;
		maxz = max;

		for (int i = 0; i < x.length; i++){
			if (x[i] > maxx)
				maxx = x[i];
			if (y[i] > maxy)
				maxy = y[i];
			if (z[i] > maxz)
				maxz = z[i];
		}

		axes = new double[4][];
		axes[0] = new double[]{0.0, 0.0, 0.0};
		axes[1] = new double[]{maxx, 0.0, 0.0};
		axes[2] = new double[]{0.0, maxy, 0.0};
		axes[3] = new double[]{0.0, 0.0, maxz};


		makeTrajectory(x,y,z);

		init();

	}


	public void init(){
//		panelSize = new int[] { (int) (getSize().getWidth()), (int) (getSize().getHeight()) };
		pointflag = false;
		paxes = new int[4][];
		changeView(0.0,0.0);

	}

	void makeTrajectory(double[] x, double[] y, double[] z){
		double angle1 = 0;
		double angle2 = Math.PI/100;
		int f1 = makeTrajectory(x,y,z,angle1) - maxcount;
		if (f1 > 0){
			int f2 = makeTrajectory(x,y,z,angle2) - maxcount;
			int iters = 7;
			while (f1*f2 > 0){
				angle2 *= 2;
				f2 = makeTrajectory(x,y,z,angle2) - maxcount;
				iters++;
			}
			for (int iter = 0; iter < iters; iter ++){
				double mid = (angle1 + angle2) / 2;
				int fm = makeTrajectory(x,y,z,mid) - maxcount;
				if (f1 * fm < 0){
					f2 = fm;		
					angle2 = mid;
				}
			}
		}
		for (int i = 0; i < 4; i++){
			axes[i][0] -= maxx/2;
			axes[i][0] /= maxx;
		}
		for (int i = 0; i < trajectory.length; i++){
			trajectory[i][0] -= maxx/2;
			trajectory[i][0] /= maxx;
		}

		for (int i = 0; i < 4; i++){
			axes[i][1] -= maxy/2;
			axes[i][1] /= maxy;
		}
		for (int i = 0; i < trajectory.length; i++){
			trajectory[i][1] -= maxy/2;
			trajectory[i][1] /= maxy;
		}

		for (int i = 0; i < 4; i++){
			axes[i][2] -= maxz/2;
			axes[i][2] /= maxz;
		}
		for (int i = 0; i < trajectory.length; i++){
			trajectory[i][2] -= maxz/2;
			trajectory[i][2] /= maxz;
		}

	}


	int makeTrajectory(double[] x, double[] y, double[] z, double maxangle){
		double[][] tempTraj = new double [x.length][];
		tempTraj[0] = new double[]{x[0], y[0], z[0]};
		tempTraj[1] = new double[]{x[1], y[1], z[1]};
		double len = 0.0;
		int i = 1;
		double[] oldvec = new double[3];
		while (len < 1e-8){
			oldvec = new double[]{x[i] - x[0], y[i] - y[0], z[i] - z[0]};
			len = Math.sqrt(oldvec[0]*oldvec[0] + oldvec[1]*oldvec[1] + oldvec[2]*oldvec[2]);
			i++;
		}
		for (i = 0; i < 3; i++)
			oldvec[i] /= len;
		int count = 2;
	
		for (; i < x.length; i++){

			double[] newvec = new double[]{x[i] - tempTraj[count-1][0], y[i] - tempTraj[count-1][1], z[i] - tempTraj[count-1][2]};
			len = Math.sqrt(newvec[0]*newvec[0] + newvec[1]*newvec[1] + newvec[2]*newvec[2]);
//			if (len / Math.sqrt(maxx*maxx + maxy*maxy + maxz*maxz) < 1e-8)
//				continue;
			for (int j = 0; j < 3; j++)
				newvec[j] /= len;
			double angle = len * Math.acos(oldvec[0]*newvec[0] + oldvec[1]*newvec[1] + oldvec[2]*newvec[2]) / Math.sqrt(maxx*maxx + maxy*maxy + maxz*maxz);
			if (angle > maxangle || i == x.length - 1){
				tempTraj[count] = new double[]{x[i-1], y[i-1], z[i-1]};
				oldvec = new double[]{x[i-1] - x[i-2], y[i-1] - y[i-2], z[i-1] - z[i-2]};
				len = Math.sqrt(oldvec[0]*oldvec[0] + oldvec[1]*oldvec[1] + oldvec[2]*oldvec[2]);
				for (int j = 0; j < 3; j++)
					oldvec[j] /= len;
				count++;
			}
		}

		trajectory = new double[count][];
		for (i = 0; i < count; i++){
			trajectory[i] = new double[3];
			for (int j = 0; j < 3; j++)
				trajectory[i][j] = tempTraj[i][j];
		}
		return count;
	}

	private double dist(double[] x0, double[] x1, double[] x2){
	
		double[] v1 = new double[] {x2[0] - x1[0], x2[1] - x1[1], x2[2] - x1[2]};
		double[] v2 = new double[] {x1[0] - x0[0], x1[1] - x0[1], x1[2] - x0[2]};	
		double cross = Math.sqrt(Math.pow(v1[1]*v2[2] - v1[2]*v2[1],2) + Math.pow(v1[2]*v2[0] - v1[0]*v2[2],2) + Math.pow(v1[0]*v2[1] - v1[1]*v2[0],2));
		return cross / Math.sqrt(v1[0]*v1[0] + v1[1]*v1[1] + v1[2]*v1[2]);
		
	}


//	public void run() {
//	}


	public void mousePressed(MouseEvent e) {
		e.consume();
//		repaint();
	}

	public void mouseClicked(MouseEvent e) {
		e.consume();
//		repaint();
	}

	public void mouseReleased(MouseEvent e) {
		grabFocus();
		e.consume();
//		repaint();
	}

	public void keyPressed(KeyEvent e){
		e.consume();
	}
	public void keyReleased(KeyEvent e){
//		System.out.println(e.getKeyChar());
		switch (e.getKeyCode()) {
			case KeyEvent.VK_1:	theta = 0; phi = 0; break;
			case KeyEvent.VK_2:	theta = -Math.PI/2; phi = 0;  break;
			case KeyEvent.VK_3:	theta = -Math.PI/2; phi = Math.PI/2;  break;
			case KeyEvent.VK_NUMPAD1:	theta = 0; phi = 0;  break;
			case KeyEvent.VK_NUMPAD2:	theta = -Math.PI/2; phi = 0;  break;
			case KeyEvent.VK_NUMPAD3:	theta = -Math.PI/2; phi = Math.PI/2;  break;
		}
		changeView(0,0);
		e.consume();
	}
	public void keyTyped(KeyEvent e){
		e.consume();
	}

	public void mouseDragged(MouseEvent event) {
		int i = event.getX();
		int j = event.getY();
		event.consume();
		int oldx = newx;
		int oldy = newy;
		newx = i;
		newy = j;

		changeView(-.01*(double)(newx - oldx), .01*(double)(newy - oldy));
	}

	public void mouseMoved(MouseEvent event) {
        	int i = event.getX();
	        int j = event.getY();
        	event.consume();
		newx = i;
		newy = j;
//		repaint();
	}

	public void mouseEntered(MouseEvent e) {
		e.consume();
//		repaint();
	}

	public void mouseExited(MouseEvent e) {
		e.consume();
//		repaint();
	}

	public void update(Graphics g) {
		paint(g);
	}

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
//        panelSize = new int[] { (int) (getSize().getWidth()), (int) (getSize().getHeight()) };
		projection();
        repaint();
    }

    public void componentShown(ComponentEvent e) {
//        panelSize = new int[] { (int) (getSize().getWidth()), (int) (getSize().getHeight()) };
		projection();
		repaint();
    }


	public void plotPoint(double x, double y, double z){
		point = new double[]{x/maxx - .5, y/maxy - .5, z/maxz - .5};
		pointflag = true;
		projection();
		repaint();
	}

	public void plotPoint(){
		pointflag = false;
		projection();
		repaint();
	}

	public void changeView(double dtheta, double dphi) {
		theta += dtheta;
		phi += dphi;
//		if (phi > Math.PI / 4)
//			phi = Math.PI / 4;
//		if (phi < -Math.PI / 4)
//			phi = -Math.PI / 4;
		getVector();
		projection();
		repaint();
	}

	public void getVector() {
		vector = new double[3];
		vector[0] = Math.cos(theta) * Math.cos(phi);
		vector[1] = Math.sin(theta) * Math.cos(phi);
		vector[2] = Math.sin(phi);
	}

	public void projection(){
		panelSize = new int[] { (int) (getSize().getWidth()), (int) (getSize().getHeight()) };
		ptrajectory = new int[trajectory.length][];
		for (int i = 0; i < trajectory.length; i++)
			ptrajectory[i] = screen(project(trajectory[i]));
		for (int i = 0; i < 4; i++)
			paxes[i] = screen(project(axes[i]));
		if (pointflag)
			ppoint = screen(project(point));
	}

	public double[] project(double[] point){
		double[] point2 = new double[2];
		point2[0] = -Math.sin(theta) * point[0] + Math.cos(theta) * point[1];
		point2[1] = -Math.cos(theta) * Math.sin(phi) * point[0] - Math.sin(theta) * Math.sin(phi) * point[1] + Math.cos(phi) * point[2];
		return point2;
	}



	public synchronized void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, panelSize[0], panelSize[1]);		
		g.setColor(Color.BLACK);
		for (int i = 0; i < 3; i++){
			g.drawLine(paxes[0][0], paxes[0][1], paxes[i+1][0], paxes[i+1][1]);
			if (Math.abs(Math.abs(vector[i]) - 1.0) > 1.0e-8)
				g.drawString(label[i],paxes[i+1][0], paxes[i+1][1]);
		}
		g.setColor(Color.BLUE);
		for (int i = 0; i < ptrajectory.length - 1; i++)
			g.drawLine(ptrajectory[i][0], ptrajectory[i][1], ptrajectory[i+1][0], ptrajectory[i+1][1]);
		if (pointflag){
			g.setColor(Color.RED);
			g.fillOval(ppoint[0]-panelSize[0]/100, ppoint[1]-panelSize[1]/100, panelSize[0]/50, panelSize[1]/50);
		}

	}


	public int[] screen(double[] point){

		int[] ret = new int[2];
		ret[0] = (int)((panelSize[0])/2*(point[0]+1));
		ret[1] = (int)(panelSize[1] - (panelSize[1])/2*(point[1]+1));
		return ret;
	}


}