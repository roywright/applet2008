import java.applet.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;



public class Plotter extends Applet{

//	TO BE READ FROM HTML FILE:
	int type = 0;
	String imagefile = "image0.png";
	double[] parameters = new double[] 
		      {1, 1, 1, 

		       1,   0.9, 0.25,
		       0.25, 1,   0.9,
		       0.9, 0.25, 1,

		       0.25, 0.25, 0.25,

		       0.25,

		       10, 10, 10,

		       .1, .2, .15,
		       .15,  .1, .2,
		       .2,  .15, .1,

		      /*  .165,   .07, */  .1,

		        .1,  .1,  .1,

		        .3};
	double[] defaultIC = new double[]{0.11, 0.12, 0.13, 10.0, 10.0, 10.0, 0.0};
	double defaultp1 = 0.165;
	double defaultp2 = 0.07;
	double[] defaultb1 = new double[]{0,0.2};
	double[] defaultb2 = new double[]{0,0.2};
	int defaultn1 = 28;
	int defaultn2 = 29;
	int defaultinvn = 6;
	double defaultinv = 500.0;
	double defaultmaxtime = 1500.0;
	double defaultinvdensity = 0.01;
	double defaultstep = 0.01;
	int defaultmaxsteps1 = (int)(6000 * defaultinv / defaultmaxtime);
	int defaultmaxsteps2 = 6000 - defaultmaxsteps1;
	int[] defaultseries = new int[]{0,1,2,6};
	int[] defaulttrajectory = new int[]{0,1,2};
	double defaultmax = 40.0;
	String defaultlabel1 = "1";
	String defaultlabel2 = "2";
	String defaultlabel3 = "3";
	
//	************************************************


	TrajectoryPanel trajectoryPanel;
	MapPanel mapPanel;
	TimeSeriesPanel timeSeriesPanel;
	WrightSolver theSolver;

	double[][] ts;
	
	public void init() {
		getParams();
		this.setLayout(new GridLayout(1,3));
		mapPanel = new MapPanel(imagefile,this);
		this.add(mapPanel);

		theSolver = new WrightSolver(type, defaultn1, defaultn2, parameters);

		double[][] ts1 = theSolver.timeSeries(defaultIC, defaultp1, defaultp2, defaultinv, defaultstep, defaultmaxsteps1);

		double[] dinv = new double[ts1.length];
		for (int i = 0; i < ts1.length; i++)
			dinv[i] = ts1[i][ts1[0].length-1];
		dinv[defaultinvn] = defaultinvdensity;

		double[][] ts2 = theSolver.timeSeries(dinv, defaultp1, defaultp2, defaultmaxtime-defaultinv, defaultstep, defaultmaxsteps2);

		ts = append(ts1, ts2);

		double[][] series = new double[defaultseries.length][];
		for (int i = 0; i < defaultseries.length; i++)
			series[i] = ts[defaultseries[i]];
		timeSeriesPanel = new TimeSeriesPanel(series,defaultmax,this);
		this.add(timeSeriesPanel);

		trajectoryPanel = new TrajectoryPanel(ts[defaulttrajectory[0]],ts[defaulttrajectory[1]],ts[defaulttrajectory[2]],defaultmax,defaultlabel1,defaultlabel2,defaultlabel3);
		this.add(trajectoryPanel);
		mapPanel.paramPoint((defaultp1-defaultb1[0])/(defaultb1[1]-defaultb1[0]), (defaultp2-defaultb2[0])/(defaultb2[1]-defaultb2[0]));

	}

	private void getParams(){
	
	imagefile = getParameter("image");
	String paramfile = getParameter("data");


		try {
			URL url = new URL(getCodeBase(), paramfile);
			BufferedReader buf = new BufferedReader(
			new InputStreamReader(url.openStream()));
			
			type = Integer.parseInt(buf.readLine());
			
			
			String paramstring = buf.readLine();
			String specialstring = buf.readLine();


			StringTokenizer st = new StringTokenizer(specialstring," ");
			defaultn1 = Integer.parseInt(st.nextToken());
			defaultn2 = Integer.parseInt(st.nextToken());


			
			st = new StringTokenizer(paramstring," ");
			parameters = new double[st.countTokens() - 2];
			int j = 0;
			for (int i = 0; i < parameters.length + 2; i++)
				if (i == defaultn1)
					defaultp1 = Double.parseDouble(st.nextToken());
				else if (i == defaultn2)
					defaultp2 = Double.parseDouble(st.nextToken());
				else 
					parameters[j++] = Double.parseDouble(st.nextToken());
//			for (int i = 0; i < parameters.length; i++)
//				System.out.println(parameters[i]);
//				System.out.println(defaultp1);
//				System.out.println(defaultp2);
				
			st = new StringTokenizer(buf.readLine()," ");
			for (int i = 0; i < 2; i++)
				defaultb1[i] = Double.parseDouble(st.nextToken());


			st = new StringTokenizer(buf.readLine()," ");
			for (int i = 0; i < 2; i++)
				defaultb2[i] = Double.parseDouble(st.nextToken());

			st = new StringTokenizer(buf.readLine()," ");
			defaultIC = new double[st.countTokens()];
			for (int i = 0; i < defaultIC.length; i++)
					defaultIC[i] = Double.parseDouble(st.nextToken());

			
			defaultinvn = Integer.parseInt(buf.readLine());
			defaultinvdensity = Double.parseDouble(buf.readLine());
			defaultinv = Double.parseDouble(buf.readLine());
			defaultmaxtime = Double.parseDouble(buf.readLine());

			st = new StringTokenizer(buf.readLine()," ");
			defaultseries = new int[st.countTokens()];
			for (int i = 0; i < defaultseries.length; i++)
					defaultseries[i] = Integer.parseInt(st.nextToken());

			st = new StringTokenizer(buf.readLine()," ");
			defaulttrajectory = new int[3];
			for (int i = 0; i < 3; i++)
					defaulttrajectory[i] = Integer.parseInt(st.nextToken());

			defaultmax = Double.parseDouble(buf.readLine());

			buf.close();
		}
		catch (Exception ioe) { 
			System.out.println("ERROR");
		}


	String dl1 = getParameter("label1");
	String dl2 = getParameter("label2");
	String dl3 = getParameter("label3");
	if (dl1 != null)
		defaultlabel1 = dl1;
	if (dl2 != null)
		defaultlabel2 = dl2;
	if (dl3 != null)
		defaultlabel3 = dl3;

	}

	public void plotPoint(int i){
		trajectoryPanel.plotPoint(ts[defaulttrajectory[0]][i],ts[defaulttrajectory[1]][i],ts[defaulttrajectory[2]][i]);
	}
	public void plotPoint(){
		trajectoryPanel.plotPoint();
	}
	public void paramPoint(double x, double y){
		defaultp1 = defaultb1[0] + x*(defaultb1[1] - defaultb1[0]);
		defaultp2 = defaultb2[0] + y*(defaultb2[1] - defaultb2[0]);
		double[][] ts1 = theSolver.timeSeries(defaultIC, defaultp1, defaultp2, defaultinv, defaultstep, defaultmaxsteps1);

		double[] dinv = new double[ts1.length];
		for (int i = 0; i < ts1.length; i++)
			dinv[i] = ts1[i][ts1[0].length-1];
		dinv[defaultinvn] = defaultinvdensity;

		double[][] ts2 = theSolver.timeSeries(dinv, defaultp1, defaultp2, defaultmaxtime-defaultinv, defaultstep, defaultmaxsteps2);

		ts = append(ts1, ts2);
		double[][] series = new double[defaultseries.length][];
		for (int i = 0; i < defaultseries.length; i++)
			series[i] = ts[defaultseries[i]];

		timeSeriesPanel.change(series,defaultmax);
		trajectoryPanel.change(ts[defaulttrajectory[0]],ts[defaulttrajectory[1]],ts[defaulttrajectory[2]],defaultmax);

	}
	public void paramPoint(){
	}

	public double[][] append(double[][] ts1, double[][] ts2){
		double[][] ret = new double[ts1.length][ts1[0].length + ts2[0].length];
		for (int i = 0; i < ts1.length; i++){
			for (int j = 0; j < ts1[0].length; j++)
				ret[i][j] = ts1[i][j];
			for (int j = 0; j <  ts2[0].length; j++)
				ret[i][ts1[0].length + j] = ts2[i][j];
		}
		return ret;
	}


}