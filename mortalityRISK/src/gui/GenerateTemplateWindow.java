package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;

import net.miginfocom.swing.MigLayout;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.ImageIcon;

public class GenerateTemplateWindow {

	private static JFrame frmTemplateSettings;
	private static final ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * For testing purposes
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize("Spatial");
					frmTemplateSettings.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	/**
	 * Launch the application.
	 */
	public static void launchApplication (String type) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize(type);
					frmTemplateSettings.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Checks if the window is open or not
	 * @return
	 */
	public static boolean isOpen() {
		if (frmTemplateSettings != null) {
			return frmTemplateSettings.isVisible();
		} else {
			return  false;
		}
	}
	/**
	 * Create the application.
	 */
	public GenerateTemplateWindow(String type) {
		initialize(type);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private static void initialize(String type) {
		
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

		frmTemplateSettings = new JFrame();
		frmTemplateSettings.setAlwaysOnTop(true);
		frmTemplateSettings.setIconImage(Toolkit.getDefaultToolkit().getImage(GenerateTemplateWindow.class.getResource("/resources/Risky 16x16.png")));
		frmTemplateSettings.setTitle("Generate Template");
		frmTemplateSettings.setBounds(100, 100, 191, 213);
		frmTemplateSettings.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmTemplateSettings.getContentPane().setLayout(new MigLayout("", "[][]", "[][][][][][]"));
		
		JLabel lblSpeciesOrientation = new JLabel("Species Orientation:");
		frmTemplateSettings.getContentPane().add(lblSpeciesOrientation, "flowx,cell 0 0 2 1,alignx center");
		
		JRadioButton rdbtnVertical = new JRadioButton("Vertical");
		buttonGroup.add(rdbtnVertical);
		frmTemplateSettings.getContentPane().add(rdbtnVertical, "cell 0 1");
		
		JRadioButton rdbtnHorizontal = new JRadioButton("Horizontal");
		rdbtnHorizontal.setSelected(true);
		buttonGroup.add(rdbtnHorizontal);
		frmTemplateSettings.getContentPane().add(rdbtnHorizontal, "cell 1 1");
		
		JCheckBox chckbxSexDifferentiation = new JCheckBox("Sex Differentiation");
		chckbxSexDifferentiation.setSelected(true);
		frmTemplateSettings.getContentPane().add(chckbxSexDifferentiation, "flowx,cell 0 2 2 1,alignx center");
		
		JSpinner numberOfSpecies = new JSpinner();
		numberOfSpecies.setPreferredSize(new Dimension(50,20));
		numberOfSpecies.setValue(2);
		frmTemplateSettings.getContentPane().add(numberOfSpecies, "cell 0 4 2 1,alignx center");
		frmTemplateSettings.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JButton btnGenerateTemplate = new JButton("Generate Template");
		btnGenerateTemplate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			String	location = System.getProperty("user.dir");
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setDialogTitle("Select Location");
					fileChooser.setCurrentDirectory(new File(location + System.getProperty("file.separator")));
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (fileChooser.showSaveDialog( new JFrame()) == JFileChooser.APPROVE_OPTION) {
					  File file = fileChooser.getSelectedFile();
						location = file.getAbsolutePath();
					  // save to file
						try {
							XLSreader.createTemplate(location + System.getProperty("file.separator") + "InputFile.xlsx", (int) numberOfSpecies.getValue(), chckbxSexDifferentiation.isSelected(),type, rdbtnVertical.isSelected());
							frmTemplateSettings.setVisible(false); 
							frmTemplateSettings.dispose();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}


			}
		});
		
		JLabel lblNumberOfSpecies = new JLabel("Number Of Species:");
		frmTemplateSettings.getContentPane().add(lblNumberOfSpecies, "flowx,cell 0 3 2 1,alignx center");
		frmTemplateSettings.getContentPane().add(btnGenerateTemplate, "cell 0 5 2 1,alignx center");
		
		JLabel lblInfoSexDif = new JLabel("");
		lblInfoSexDif.setIcon(new ImageIcon(GenerateTemplateWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoSexDif.setToolTipText("<html>\r\n<p>Choose to able to set traits differently for males and females.</p>\r\n</html>");
		lblInfoSexDif.setHorizontalAlignment(SwingConstants.RIGHT);
		frmTemplateSettings.getContentPane().add(lblInfoSexDif, "cell 0 2,alignx right");
		
		JLabel lblInfoOrientation = new JLabel("");
		lblInfoOrientation.setIcon(new ImageIcon(GenerateTemplateWindow.class.getResource("/resources/Information Icon.png")));
		lblInfoOrientation.setToolTipText("<html>\r\n<p>Choose vertical to have each row represent a species,</p>\r\n<p>or choose horizontal to have each column represent a species.</p>\r\n</html>");
		lblInfoOrientation.setHorizontalAlignment(SwingConstants.RIGHT);
		frmTemplateSettings.getContentPane().add(lblInfoOrientation, "cell 0 0,alignx right");


	}

}
