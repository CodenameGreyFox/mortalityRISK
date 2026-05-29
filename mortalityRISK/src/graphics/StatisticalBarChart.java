package graphics;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

/**
 * A class to create bar charts based on JFreeChart
 */
public class StatisticalBarChart extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a bar chart from the provided dataset
	 * @param title
	 * @param dataset
	 */
	public StatisticalBarChart(String title, CategoryDataset dataset) {
		super(title);
		JPanel jpanel = createPanel(dataset);
		jpanel.setPreferredSize(new Dimension(1200, 700));
		setContentPane(jpanel);
	}

	/**
	 * Creates the main chart
	 * @param categorydataset
	 * @return
	 */
	private static JFreeChart createChart(CategoryDataset categorydataset) {
		JFreeChart jfreechart = ChartFactory.createLineChart("Percentage of Extinction along Road Density", "Road Density", "Percentage of extinction",
				categorydataset, PlotOrientation.VERTICAL, true, true, false);
		jfreechart.setBackgroundPaint(Color.white);
		CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
		categoryplot.setBackgroundPaint(Color.lightGray);
		categoryplot.setRangeGridlinePaint(Color.white);

		CategoryAxis xAxis =  categoryplot.getDomainAxis();
		xAxis.setCategoryMargin(0.1);
		xAxis.setLowerMargin(0);
		xAxis.setUpperMargin(0);

		NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
		numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		numberaxis.setAutoRangeIncludesZero(false);
		numberaxis.setRange(0,101);
		StatisticalBarRenderer statisticalbarrenderer = new StatisticalBarRenderer();
		statisticalbarrenderer.setErrorIndicatorPaint(Color.black);
		statisticalbarrenderer.setIncludeBaseInRange(false);
		categoryplot.setRenderer(statisticalbarrenderer);
		statisticalbarrenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		statisticalbarrenderer.setBaseItemLabelsVisible(true);
		statisticalbarrenderer.setBasePositiveItemLabelPosition(
				new ItemLabelPosition(ItemLabelAnchor.INSIDE6, TextAnchor.BOTTOM_CENTER));
		
		categoryplot.setBackgroundPaint(new Color(0xe6e6e6));
        
		return jfreechart;
	}
	/**
	 * Creates the panel with the graph
	 * @param dataset
	 * @return the panel
	 */
	public static JPanel createPanel(CategoryDataset dataset) {
		JFreeChart jfreechart = createChart(dataset);
		return new ChartPanel(jfreechart);
	}

	/**
	 * Creates the bar graph
	 * @param dataset
	 */
	public static void graph(CategoryDataset dataset) {
		StatisticalBarChart statisticalbarchartdemo1 = new StatisticalBarChart(
				"Survival along Road Density", dataset);
		statisticalbarchartdemo1.pack();
		RefineryUtilities.centerFrameOnScreen(statisticalbarchartdemo1);
		statisticalbarchartdemo1.setVisible(true);		
	}

	/**
	 * Creates the bar graph and saves it to a file
	 * @param dataset
	 * @param file
	 */
	public static void graph(CategoryDataset dataset, File file) {
		if (GraphicsEnvironment.isHeadless()) {
			graphNoUI(dataset, file);
			return;
		}
		StatisticalBarChart statisticalbarchartdemo1 = new StatisticalBarChart(
				"Survival along Road Density", dataset);
		 Container contentPane = statisticalbarchartdemo1.getContentPane();
		statisticalbarchartdemo1.pack();
		RefineryUtilities.centerFrameOnScreen(statisticalbarchartdemo1);
		statisticalbarchartdemo1.setVisible(true);
				
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
	 */
	public static void graphNoUI(CategoryDataset dataset, File file) {
	    JFreeChart jfreechart = createChart(dataset);
	    try {
	        ChartUtilities.saveChartAsPNG(file, jfreechart, 1200, 700);
	        System.out.println("Chart saved to: " + file.getAbsolutePath());
	    } catch (IOException e) {
	        System.err.println("Error saving chart in headless mode");
	        e.printStackTrace();
	    }
	}

}