package example.popularItems.gui.toolbar;

import java.awt.FlowLayout;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JToolBar;

public class ToolBarPanel extends JPanel {

	/**
	 * 
	 */
	private static ToolBarPanel toolBarPanel;
	private static final long serialVersionUID = 4649180127090628984L;
	private JToolBar browserToolBar;

	public static ToolBarPanel getInstance() throws IOException {
		if (toolBarPanel == null)
			return toolBarPanel = new ToolBarPanel();
		else
			return toolBarPanel;
	}

	private ToolBarPanel() throws IOException {
		setAlignmentX(LEFT_ALIGNMENT);
		setAlignmentY(CENTER_ALIGNMENT);
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		add(getBrowserToolBar());
	}

	private JToolBar getBrowserToolBar() throws IOException {
		if (browserToolBar == null) {
			return browserToolBar = BrowserToolBar.getInstance();
		}
		return browserToolBar;
	}
}
