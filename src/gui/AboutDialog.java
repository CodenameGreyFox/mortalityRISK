package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

/**
 * About Window for mortalityRISK.
 */
public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	public AboutDialog(Frame owner, String version) {
        super(owner, "About mortalityRISK", true);
        setIconImage(Toolkit.getDefaultToolkit().getImage(AboutDialog.class.getResource("/resources/Risky 16x16.png")));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main panel
        JPanel contentPanel = new JPanel(new MigLayout("wrap, fillx, insets 20", "[199.00,fill][]", "[][][][][][][][]"));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);

        //HEADER & TITLE ---
        JLabel titleLabel = new JLabel("mortalityRISK "+ version);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        contentPanel.add(titleLabel, "cell 0 0 2 1,alignx center,gapbottom 10");

        //PURPOSE & AUTHORS ---
        String description = "<html><body style='width: 380px; text-align: justify;'>" +
                "A simulation tool for estimating the impact of infrastructure-induced " +
                "mortality on wildlife populations.</body></html>";
        JLabel descLabel = new JLabel("<html><body style='width: 400px; text-align: justify;'>A simulation tool for estimating the impact of infrastructure-induced mortality on wildlife populations.</body></html>");
        descLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        contentPanel.add(descLabel, "cell 0 1 2 1,gapbottom 5");

        JLabel authorsLabel = new JLabel("<html>\r\n<p>Please cite as:</p>\r\n<p>Presently anonymised for reviewing purposes (2026) Citation to be defined</p>");
        authorsLabel.setFont(new Font("Tahoma", Font.ITALIC, 11));
        contentPanel.add(authorsLabel, "cell 0 2 2 1,alignx center,gapbottom 15");


        // --- SCIENTIFIC DISCLAIMER ---
        JTextPane disclaimerPane = new JTextPane();
        disclaimerPane.setContentType("text/html");
        disclaimerPane.setEditable(false);
        disclaimerPane.setBackground(new Color(245, 245, 245)); // Light gray background
        disclaimerPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        String disclaimerText = "<html><div style='width: 400px; padding: 8px; font-family: Tahoma; font-size: 10px;'>" +
                "<b>Legal Notice & Disclaimer:</b><br/>" +
                "This software is provided \"as is\" without warranty of any kind. "+
                "Simulation results depend on model assumptions and input data and should therefore be interpreted with caution." +
                "The authors are not liable for consequences arising from its use.</div></html>";

        disclaimerPane.setText(disclaimerText);
        contentPanel.add(disclaimerPane, "cell 0 3 2 1,gapbottom 15");


        // --- THIRD-PARTY CREDITS ---
        JTextPane creditsPane = new JTextPane();
        creditsPane.setContentType("text/html");
        creditsPane.setEditable(false);
        creditsPane.setBackground(contentPanel.getBackground());
        creditsPane.setBorder(null);

        String creditsText = "<html><body style='width: 350px; font-family: Tahoma; font-size: 10px;'>" +
                "<b>Third-Party Libraries:</b>" +
                "<ul>" +
                "<li><b>Apache Commons, Log4j2, POI, XMLBeans:</b> Licensed under the Apache License 2.0.</li>" +
                "<li><b>MigLayout:</b> Copyright (c) 2004, Mikael Grev. Licensed under the BSD 3-Clause License.</li>" +
                "<li><b>JFreeChart & JCommon:</b> Licensed under the GNU Lesser General Public License (LGPL) v2.1. " +
                "Source code available at <a href='http://www.jfree.org/'>jfree.org</a>.</li>" +
                "</ul>" +
                "</body></html>";

        creditsPane.setText(creditsText);
        contentPanel.add(creditsPane, "cell 0 4 2 1,gapbottom 20");
        
        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon(AboutDialog.class.getResource("/resources/oscar-european-union-mini.png")));
        contentPanel.add(lblNewLabel, "flowy,cell 0 5");
        
        JLabel lblNewLabel_1 = new JLabel("");
        lblNewLabel_1.setIcon(new ImageIcon(AboutDialog.class.getResource("/resources/Biopolis Logo Mini.png")));
        contentPanel.add(lblNewLabel_1, "flowx,cell 1 5,alignx center");
                
                JLabel lblNewLabel_2 = new JLabel("");
                lblNewLabel_2.setIcon(new ImageIcon(AboutDialog.class.getResource("/resources/cibio mini.png")));
                contentPanel.add(lblNewLabel_2, "cell 1 5");
                
                
                        // --- CLOSE BUTTON ---
                        JButton closeButton = new JButton("Close");
                        closeButton.addActionListener(e -> dispose());
                        contentPanel.add(closeButton, "tag ok,cell 0 7 2 1,alignx center");

        pack();
        setLocationRelativeTo(owner);
    }
}