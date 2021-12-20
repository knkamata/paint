import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SimplePaint extends Application {

	private final Color[] palette = {
		Color.BLACK, Color.RED, Color.GREEN, Color.BLUE,
		Color.CYAN, Color.MAGENTA, Color.color(.95, .9, 0)
	};
	private int currentColorNum = 0;
	private double prevX, prevY;
	private boolean dragging;
	private Canvas canvas;
	private GraphicsContext g;
	public static void main(String[] args) {
		launch(args);
	}
	@Override
	public void start(Stage stage) throws Exception {

		// Create the canvas and draw its content for the first time.
		canvas = new Canvas(600, 400);
		g = canvas.getGraphicsContext2D();
		clearAndDrawPalette();

		// Respond to mouse events on the canvas, by calling methods in this class.
		canvas.setOnMousePressed(e -> mousePressed(e));
		canvas.setOnMouseDragged(e -> mouseDragged(e));
		canvas.setOnMouseReleased(e -> mouseReleased(e));

		Pane root = new Pane(canvas);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Simple Paint");
		stage.show();
	}

	public void clearAndDrawPalette() {
		int width = (int)canvas.getWidth();
		int height = (int)canvas.getHeight();

		g.setFill(Color.WHITE);
		g.fillRect(0, 0, width, height);

		// Draw a 3-pixel border around the canvas in gray.
		g.setStroke(Color.GRAY);
		g.setLineWidth(3);
		g.strokeRect(1.5, 1.5, width - 3, height - 3);

		// Draw a 56 gray rectangle along the right edge of the canvas.
		// The color palette and Clear button drawn on top of this.
		g.setFill(Color.GRAY);
		g.fillRect(width - 56, 0, 56, height);

		// Draw the Clear button as a 50-by-50 white rectangle
		// in the lower right corner of the canvas.
		g.setFill(Color.WHITE);
		g.fillRect(width - 53, height - 53, 50, 50);
		g.setFill(Color.BLACK);
		g.fillText("Clear", width - 44, height - 23);

		// (height - (3-pixel border + Clear button height)) / 7 = a height for a color palette
		int colorSpacing = (height - 56) / 7;

		// Draw 7 color palette
		for (int N = 0; N < 7; N++) {
			g.setFill(palette[N]);
			g.fillRect(width - 53, colorSpacing * N + 3, 50, colorSpacing - 3);
		}

		// Draw 2 pixel white border around the color rectangle of the current drawing color.
		g.setStroke(Color.WHITE);
		g.setLineWidth(2);
		g.strokeRect(width - 54, 2 + currentColorNum * colorSpacing, 52, colorSpacing - 1);
	}

	/**
	 * Change the drawing color after the user has clicked the mouse
	 * on the color palette at a point with y-coordinate y.
	 * @param y y-coordinate where the user has clicked on the color palette
	 */
	private void changeColor(int y) {
		int width = (int)canvas.getWidth();
		int height = (int)canvas.getHeight();
		int colorSpacing = (height - 56) / 7;
		int newColor = y / colorSpacing;

		if (newColor < 0 || 6 < newColor) return;

		// Remove the highlight from the current color by drawing over it in gray,
		// then change the current drawing color and draw a highlight around the new drawing color.
		g.setLineWidth(2);
		g.setStroke(Color.GRAY);
		g.strokeRect(width - 54, 2 + currentColorNum * colorSpacing, 52, colorSpacing - 1);
		currentColorNum = newColor;
		g.setStroke(Color.WHITE);
		g.strokeRect(width - 54, 2 + currentColorNum * colorSpacing, 52, colorSpacing - 1);
	}

	public void mousePressed(MouseEvent event) {
		if (dragging) return;

		int width = (int)canvas.getWidth();
		int height = (int)canvas.getHeight();

		int x = (int)event.getX();
		int y = (int)event.getY();

		if (x > width - 53) {
			// User clicked the color palette or the Clear button.
			if (y > height - 53)
				clearAndDrawPalette();
			else
				changeColor(y);
			// For debug
			// System.out.println("Clicked at (" + x + " ," + y + ") -> Color No. " + y / ((height - 56) / 7));
		}
		else if (3 < x && x < width - 56 && 3 < y && y < height - 3) {
			// User clicked the white drawing area.
			// Start drawing a curve a curve from the point (x,y).
			prevX = x;
			prevY = y;
			dragging = true;
			g.setLineWidth(2);
			g.setStroke(palette[currentColorNum]);
		}
	}

	public void mouseDragged(MouseEvent event) {
		if (!dragging) return;

		double x = event.getX();
		double y = event.getY();
		int widthOfAreaDrawing = (int)canvas.getWidth() - 57;
		int heightOfAreaDrawing = (int)canvas.getHeight() - 4;

		if (x < 3) x = 3;
		if (y < 3) y = 3;

		if (x > widthOfAreaDrawing)  x = widthOfAreaDrawing;
		if (y > heightOfAreaDrawing) y = heightOfAreaDrawing;

		g.strokeLine(prevX, prevY, x, y);
		prevX = x;
		prevY = y;
	}

	public void mouseReleased(MouseEvent event) {
		dragging = false;
	}
}
