package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

/**
 * A class to create deviation charts based on JFreeChart
 */
public class DeviationGrapher extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new deviation chart
	 *
	 * @param title  the frame title.
	 */
	public DeviationGrapher(YIntervalSeriesCollection dataset, String title, String yAxisLabel, String xAxisLabel, String[] xLabels, boolean integerOnly) {
		super(title);
		JPanel chartPanel =  new ChartPanel(createChart(dataset, title, yAxisLabel, xAxisLabel, xLabels, integerOnly));
		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 700));
		setContentPane(chartPanel);
	}

	/**
	 * Creates a chart.
	 *
	 * @param dataset  the data for the chart.
	 *
	 * @return a chart.
	 */
	private static JFreeChart createChart(XYDataset dataset,String title, String yAxisLabel, String xAxisLabel, String[] xLabels, boolean integerOnly) {

		// create the chart...
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				title,          // chart title
				xAxisLabel,                   // x axis label
				yAxisLabel,       // y axis label
				dataset,                  // data
				true,                     // include legend
				true,                     // tooltips
				false                     // urls
				);


		// get a reference to the plot for further customisation...
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainPannable(true);
		plot.setRangePannable(false);
		plot.setInsets(new RectangleInsets(5, 5, 5, 20));

		DeviationRenderer renderer = new DeviationRenderer(true, false);
		renderer.setSeriesStroke(0, new BasicStroke(3.0f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		renderer.setSeriesStroke(0, new BasicStroke(3.0f,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		renderer.setSeriesStroke(1, new BasicStroke(3.0f,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		renderer.setSeriesFillPaint(0, new Color(255, 200, 200));
		renderer.setSeriesFillPaint(1, new Color(200, 200, 255));
		plot.setRenderer(renderer);

		if (integerOnly) {
			// change the auto tick unit selection to integer units only...
			NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
			yAxis.setAutoRangeIncludesZero(false);
			yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		}

		// Create an NumberAxis
		if(xLabels != null ) {
			SymbolAxis xAxis = new SymbolAxis(xAxisLabel,
					xLabels);
			plot.setDomainAxis(xAxis);
		} else {
			NumberAxis xAxis = new NumberAxis();
			xAxis.setLabel(xAxisLabel);
			plot.setDomainAxis(xAxis);
		}




		//
		plot.setBackgroundPaint(new Color(0xe6e6e6));
		plot.setRangeTickBandPaint(new Color(0xe6e6e6));

		return chart;

	}

	/**
	 * Method to graph and save to image
	 * @param dataset
	 * @param title
	 * @param yAxisLabel
	 * @param xAxisLabel
	 * @param xLabels
	 * @param integerOnly
	 */

	public static void graph(YIntervalSeriesCollection dataset,File file, String title, String yAxisLabel, String xAxisLabel, boolean integerOnly ) {
		graph(dataset,file,title, yAxisLabel, xAxisLabel,null, integerOnly);
	}

	
	/**
	 * Method to graph and save to image 
	 * @param dataset
	 * @param file
	 * @param title
	 * @param yAxisLabel
	 * @param xAxisLabel
	 * @param xLabels
	 * @param integerOnly
	 */
	public static void graph(YIntervalSeriesCollection dataset, File file, String title, String yAxisLabel, String xAxisLabel, String[] xLabels, boolean integerOnly) {
		if (GraphicsEnvironment.isHeadless()) {
			graphNoUI(dataset, file, title, yAxisLabel,xAxisLabel,xLabels,integerOnly);
			return;
		}
		DeviationGrapher demo = new DeviationGrapher(dataset,title, yAxisLabel, xAxisLabel, xLabels, integerOnly);
		Container contentPane = demo.getContentPane();

		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

		BufferedImage image = new BufferedImage(contentPane.getWidth(), contentPane.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		contentPane.printAll(g2d);
		g2d.dispose();

		try {
			ImageIO.write(image, "png", file);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Server-safe version that generates the image without a GUI.	 
	 * @param dataset
	 * @param file
	 * @param title
	 * @param yAxisLabel
	 * @param xAxisLabel
	 * @param xLabels
	 * @param integerOnly
	 */
	private static void graphNoUI(YIntervalSeriesCollection dataset, File file, String title, String yAxisLabel, String xAxisLabel, String[] xLabels, boolean integerOnly) {
	    JFreeChart chart = createChart(dataset, title, yAxisLabel, xAxisLabel, xLabels, integerOnly);
	    try {
	    	ChartUtilities.saveChartAsPNG(file, chart, 1200, 700);	        
	    } catch (IOException e) {
	        System.err.println("Failed to save chart: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

}