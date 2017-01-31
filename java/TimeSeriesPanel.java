import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.lang.Math.*;


public class TimeSeriesPanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {

	double maxy;
	double[][] data;
	int[][] pdata;
	double point;
	int ppoint;
	boolean pointflag;
	int[] panelSize;
	Plotter plotter;
	int[][] paxes;
	
	float[][] colors;
	
	double ystretch = .6;
	double xstretch = .8;

	public TimeSeriesPanel(double[][] d, double my, Plotter p) {

		setDoubleBuffered(true);
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		plotter = p;
		maxy = my;
		data = new double[d.length][];
		for (int i = 0; i < d.length; i++){
			data[i] = new double[d[i].length];
			for (int j = 0; j < d[i].length; j++)
				data[i][j] = d[i][j];
		}

		pointflag = false;
		getColors(d.length);
//		makePlot();

	}

	public void getColors(int num){
	
		double[] tempcolors = new double[]{  
//		1,           0,           0,      // RED
		0,           0.75,        0,      // GREEN
		0,           0,           1,      // BLUE
		0.75,        0,           0.75,   // MAGENTA
		1,           0.5,         0.25,   // ORANGE
		0,           0.75,        0.75,   // CYAN
		0.5,         0.5,         0.5};   // GREY
   
		colors = new float[num][3];
		int k = 0;
		double mult = 1.0;
		for (int i = 0; i < num; i++){
			if (k == tempcolors.length){
				k -= tempcolors.length;
				mult /= 2.0;
			}
			for (int j = 0; j < 3; j++)
				colors[i][j] = (float)(mult*tempcolors[k++]);
		}
   }
	
	

	public void change(double[][] d, double my) {

		maxy = my;
		data = new double[d.length][];
		for (int i = 0; i < d.length; i++){
			data[i] = new double[d[i].length];
			for (int j = 0; j < d[i].length; j++)
				data[i][j] = d[i][j];
		}

		pointflag = false;
		getColors(d.length);
		
		makePlot();
		repaint();
		
	}


	public void mousePressed(MouseEvent e) {
		e.consume();
	}

	public void mouseClicked(MouseEvent e) {
		e.consume();
	}

	public void mouseReleased(MouseEvent e) {
		ppoint = e.getX();
		int i = (int)((ppoint - (1 - xstretch) / 2 * panelSize[0]) / (xstretch * panelSize[0] / data[0].length));		
		if (i >= 0 && i < data[0].length){
			plotter.plotPoint(i);
			pointflag = true;
		}
		else {
			plotter.plotPoint();
			pointflag = false;
		}
		repaint();
		e.consume();
	}

	public void mouseDragged(MouseEvent e) {

		mouseReleased(e);
		e.consume();

	}

	public void mouseMoved(MouseEvent event) {
        	event.consume();
	}

	public void mouseEntered(MouseEvent e) {
		makePlot();
        repaint();
		e.consume();
	}

	public void mouseExited(MouseEvent e) {
	}

	public void update(Graphics g) {
		paint(g);
	}

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
		makePlot();
        repaint();
    }

    public void componentShown(ComponentEvent e) {
		makePlot();
		repaint();
    }




	public synchronized void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, panelSize[0], panelSize[1]);		

		for (int i = 0; i < data.length; i++){
			float[] color = colors[i];
			g.setColor(new Color(color[0], color[1], color[2]));
			for (int j = 0; j < data[0].length - 1; j++)
				g.drawLine(pdata[pdata.length-1][j], pdata[i][j], pdata[pdata.length-1][j+1], pdata[i][j+1]);
		}

		if (pointflag){
			g.setColor(Color.RED);
			g.drawLine(ppoint, paxes[0][1], ppoint, paxes[2][1]);
		}

		g.setColor(Color.BLACK);
		g.drawLine(paxes[0][0], paxes[0][1], paxes[1][0], paxes[1][1]);
		g.drawLine(paxes[0][0], paxes[0][1], paxes[2][0], paxes[2][1]);


	}


	float[] colormap(double t){
		t *= 5.0/6;
		float[] ret = new float[3];
		float max = 0;

		if (t < 1.0/3)
			ret[0] = (float)(1-3*t);
		else if (t > 2.0/3)
			ret[0] = (float)(-2+3*t);
		else ret[0] = 0;

		if (ret[0] > max)
			max = ret[0];

		if (t < 1.0/3)
			ret[1] = (float)(3*t);
		else if (t < 2.0/3)
			ret[1] = (float)(2-3*t);
		else ret[1] = 0;

		if (ret[1] > max)
			max = ret[1];

		if (t < 1.0/3)
			ret[2] = 0;
		else if (t < 2.0/3)
			ret[2] = (float)(-1+3*t);
		else ret[2] = (float)(3-3*t);

		if (ret[2] > max)
			max = ret[2];

		for (int i = 0; i < 3; i++)
			ret[i] /= 1.5 * max;
			
		return ret;
	}



	public void makePlot(){
		panelSize = new int[] { (int) (getSize().getWidth()), (int) (getSize().getHeight()) };
		int maxj = data[0].length;
		pdata = new int[data.length + 1][];
		pdata[data.length] = new int[data[0].length];
		for (int i = 0; i < data.length; i++){
			pdata[i] = new int[data[0].length];
			for (int j = 0; j < maxj; j++){
				pdata[data.length][j] = (int)(xstretch * j * panelSize[0] / maxj + (1 - xstretch) / 2 * panelSize[0]);
				pdata[i][j] = (int)(ystretch * (panelSize[1] - data[i][j] / maxy * panelSize[1]) + (1 - ystretch) / 2 * panelSize[1]);
			}
		}
		paxes = new int[3][];
		paxes[0] = new int[]{(int)((1 - xstretch) / 2 * panelSize[0]), (int)((1 - (1 - ystretch) / 2) * panelSize[1])};
		paxes[1] = new int[]{(int)((1 - (1 - xstretch) / 2) * panelSize[0]), (int)((1 - (1 - ystretch) / 2) * panelSize[1])};
		paxes[2] = new int[]{(int)((1 - xstretch) / 2 * panelSize[0]), (int)((1 - ystretch) / 2 * panelSize[1])};
	}


}