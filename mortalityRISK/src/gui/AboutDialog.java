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

/**
 * Updated About Window for mortalityRISK v1.0.
 * Includes attributions for Apache, JFreeChart (LGPL), and MigLayout (BSD).
 */
public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	public AboutDialog(Frame owner) {
        super(owner, "About mortalityRISK", true);
        setIconImage(Toolkit.getDefaultToolkit().getImage(AboutDialog.class.getResource("/gui/Risky 16x16.png")));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main panel
        JPanel contentPanel = new JPanel(new MigLayout("wrap, fillx, insets 20", "[fill]"));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);

        //HEADER & TITLE ---
        JLabel titleLabel = new JLabel("mortalityRISK v1.0");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        contentPanel.add(titleLabel, "gapbottom 10, center");

        //PURPOSE & AUTHORS ---
        String description = "<html><body style='width: 350px; text-align: justify;'>" +
                "A simulation tool for estimating the impact of infrastructure-induced " +
                "mortality on wildlife populations.</body></html>";
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        contentPanel.add(descLabel, "gapbottom 5");

        JLabel authorsLabel = new JLabel("<html>\r\n<p>Please cite as:</p>\r\n<p>Neves T. & Grilo C. (2026) Citation to be defined</p>");
        authorsLabel.setFont(new Font("Tahoma", Font.ITALIC, 11));
        contentPanel.add(authorsLabel, "gapbottom 15, center");


        // --- SCIENTIFIC DISCLAIMER ---
        JTextPane disclaimerPane = new JTextPane();
        disclaimerPane.setContentType("text/html");
        disclaimerPane.setEditable(false);
        disclaimerPane.setBackground(new Color(245, 245, 245)); // Light gray background
        disclaimerPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        String disclaimerText = "<html><div style='width: 330px; padding: 8px; font-family: Tahoma; font-size: 10px;'>" +
                "<b>Legal Notice & Disclaimer:</b><br/>" +
                "This software is provided “as is” without warranty of any kind."+
                "Simulation results depend on model assumptions and input data and should therefore be interpreted with caution." +
                "The authors are not liable for consequences arising from its use.</div></html>";

        disclaimerPane.setText(disclaimerText);
        contentPanel.add(disclaimerPane, "gapbottom 15");


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
                "Full license texts are provided in the root directory of the software distribution.</body></html>";

        creditsPane.setText(creditsText);
        contentPanel.add(creditsPane, "gapbottom 20");


        // --- CLOSE BUTTON ---
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        contentPanel.add(closeButton, "tag ok, right");

        pack();
        setLocationRelativeTo(owner);
    }
}