import java.awt.*;
import java.net.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.imageio.*;
import java.lang.Math.*;


public class MapPanel extends JPanel implements MouseListener, ComponentListener {

	BufferedImage image;
	Image scaled;
	double xstretch = 0.8;
	double ystretch = 0.8;
	int[] panelSize;
	int[] ppoint;
	Plotter plotter;
	boolean pointflag;
	double x = -1.0;
	double y = -1.0;

	public MapPanel(String filename,Plotter p){

		setDoubleBuffered(true);
		addComponentListener(this);
		addMouseListener(this);

		image = null;
		plotter = p;
		try {
			URL url = new URL(p.getCodeBase(), filename);
			image = ImageIO.read(url);
		} catch (Exception e) {
		}
		pointflag = false;
	}

	public void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, panelSize[0], panelSize[1]);
		if(image != null) {
			g.drawImage(scaled, (int)(panelSize[0]*(1-xstretch)/2), (int)(panelSize[1]*(1-ystretch)/2), null);
		}
		if (pointflag){
			g.setColor(Color.BLACK);
			g.fillOval(ppoint[0]-(panelSize[0]/100 + 2), ppoint[1]-(panelSize[1]/100 + 2), panelSize[0]/50 + 4, panelSize[1]/50 + 4);
			g.setColor(Color.WHITE);
			g.fillOval(ppoint[0]-(panelSize[0]/100), ppoint[1]-(panelSize[1]/100), panelSize[0]/50, panelSize[1]/50);
		}

	}




	public void mousePressed(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		int[] ppoint_ = new int[]{e.getX(), e.getY()};
		double x_ = (ppoint_[0] - (1 - xstretch) / 2 * panelSize[0]) / (xstretch * panelSize[0]);
		double y_ = (double)(panelSize[1] - ppoint_[1]);
		y_ = (y_ - (1 - ystretch) / 2 * panelSize[1]) / (ystretch * panelSize[1]);
		if (x_ >= 0 && x_ <= 1 && y_ >= 0 && y_ <= 1){
			x = x_;
			y = y_;
			ppoint = new int[] {ppoint_[0], ppoint_[1]};
			plotter.paramPoint(x,y);
			pointflag = true;
			repaint();
		}
		else {
//			plotter.paramPoint();
//			pointflag = false;
		}
	}
	
	public void paramPoint(double xx, double yy){
		x = xx;
		y = yy;
		if (panelSize != null)
			makeMap();
	}


	public void mouseEntered(MouseEvent e) {
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
		makeMap();
        repaint();
    }

    public void componentShown(ComponentEvent e) {
		makeMap();
		repaint();
    }

	public void makeMap(){
		panelSize = new int[]{(int)(getSize().getWidth()), (int)(getSize().getHeight())};
		scaled = image.getScaledInstance((int)(xstretch*panelSize[0]), (int)(ystretch*panelSize[1]), Image.SCALE_SMOOTH);

		ppoint = new int[2];
		ppoint[0] = (int)(x * (xstretch * panelSize[0]) + (1 - xstretch) / 2 * panelSize[0]);
		ppoint[1] = (int)(panelSize[1] - (y * (ystretch * panelSize[1]) + (1 - ystretch) / 2 * panelSize[1]));
		if (x >= 0 && x <= 1 && y >= 0 && y <= 1)
			pointflag = true;
		else 
			pointflag = false;
	}






}