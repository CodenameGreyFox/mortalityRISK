package gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class for the initial selector window of the model
 */

public class FirstWindow {

	private JFrame frmTheNameOf;
	private final ButtonGroup NonSpatialModel = new ButtonGroup();

	/**
	 * Launch the application.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length == 0) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						FirstWindow window = new FirstWindow();
						window.frmTheNameOf.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}); 
		} else {
			NoGUI.initialization(args);
		}
	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public FirstWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTheNameOf = new JFrame();
		frmTheNameOf.setIconImage(Toolkit.getDefaultToolkit().getImage(FirstWindow.class.getResource("/resources/Risky 16x16.png")));
		frmTheNameOf.setResizable(false);


		frmTheNameOf.setTitle("mortalityRISK");	
		frmTheNameOf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTheNameOf.getContentPane().setLayout(new MigLayout("insets 10, gap 0 0", "[grow,left][][push][]", "[grow,center][16.00][][16.00][][16.00][][16.00][]"));
		JLabel lblNewLabel_7 = new JLabel("");
		lblNewLabel_7.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_7.setIcon(new ImageIcon(FirstWindow.class.getResource("/resources/Logotipo-RISKY-Mortality - Even Smaller.png")));
		frmTheNameOf.getContentPane().add(lblNewLabel_7, "flowx,cell 0 0 4 1,growx");

		JLabel lblNewLabel_1 = new JLabel("<html><center>Estimating the impact of infrastructure-induced<br>mortality on wildlife populations</center></html>");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 15));
		frmTheNameOf.getContentPane().add(lblNewLabel_1, "cell 0 2 4 1,alignx center");

		JLabel lblNewLabel = new JLabel("MODEL TYPE:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		frmTheNameOf.getContentPane().add(lblNewLabel, "flowx,cell 0 4 4 1,alignx center");
				
				JLabel lblNewLabel_1_2_1 = new JLabel("Authors:");
				lblNewLabel_1_2_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
				frmTheNameOf.getContentPane().add(lblNewLabel_1_2_1, "cell 0 7");
		
				JLabel versionLabel = new JLabel("<html><b>v1.0.0</b></html>");
				versionLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
				frmTheNameOf.getContentPane().add(versionLabel, "cell 3 7,alignx right,aligny bottom");


		JLabel lblNewLabel_1_2 = new JLabel("Neves T., Clara G.");
		lblNewLabel_1_2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		frmTheNameOf.getContentPane().add(lblNewLabel_1_2, "cell 0 8,alignx left");

		JRadioButton NonSpatial = new JRadioButton("Non-Spatial");
		NonSpatialModel.add(NonSpatial);
		frmTheNameOf.getContentPane().add(NonSpatial, "cell 0 4 4 1,alignx left");

		JRadioButton Spatial = new JRadioButton("Spatial");
		Spatial.setSelected(true);
		NonSpatialModel.add(Spatial);
		frmTheNameOf.getContentPane().add(Spatial, "cell 0 4 4 1,alignx right");


		JButton btnNewButton = new JButton("Start");
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (NonSpatial.isSelected()) {
					NonSpatialMultiSpeciesWindow.launchApplication();
				} else {
					SpatialMultiSpeciesWindow.launchApplication();
				}

				frmTheNameOf.dispose();
			}
		});
		frmTheNameOf.getContentPane().add(btnNewButton, "flowx,cell 0 6 4 1,alignx center");
		
				JLabel lblNewLabel_2 = new JLabel("[Legal/Credits]");
				lblNewLabel_2.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						new AboutDialog(frmTheNameOf,versionLabel.getText().replace("<html><b>", "").replace("</b></html>", "")).setVisible(true);
					}
				});
				frmTheNameOf.getContentPane().add(lblNewLabel_2, "cell 3 8");

		frmTheNameOf.pack();
		frmTheNameOf.setLocationRelativeTo(null);

	}


}
