import java.awt.Dimension;

public class Main {

	private static final Dimension WINDOW_SIZE = new Dimension(600, 400);

	public static void main(String[] args) {
		var gui = new GooglePlayPublisherGui();
		gui.setSize(WINDOW_SIZE);
		gui.setResizable(false);
		gui.setLocationRelativeTo(null);
		gui.setVisible(true);
	}
}
