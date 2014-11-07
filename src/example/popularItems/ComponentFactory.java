package example.popularItems;

import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.JLabel;

public class ComponentFactory {

	public static JLabel createLabel(Rectangle rect, String text, String toolTipText){
		JLabel lbl = new JLabel();
		lbl.setFont(new Font("Arial", Font.BOLD, 12));
		lbl.setBounds(rect);
		lbl.setText(text);
		lbl.setToolTipText(toolTipText);
		return lbl;
	}
	
}
