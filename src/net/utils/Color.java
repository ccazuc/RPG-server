package net.utils;

public class Color {
	
	public final static Color WHITE = new Color(1, 1, 1);
	public final static Color LIGHTGREY = new Color(.75f, .75f, .75f);
	//public final static Color GREY = new Color(.5f, .5f, .5f);
	public final static Color GREY = new Color(.5f, .5f, .5f);
	public final static Color DARKGREY = new Color(.25f, .25f, .25f);
	public final static Color BLACK = new Color(0, 0, 0);
	public final static Color LIGHTRED = new Color(1, .25f, .25f);
	public final static Color RED = new Color(1, 0, 0);
	public final static Color DARKRED = new Color(.5f, 0, 0);
	public final static Color LIGHTGREEN = new Color(.25f, 1, .25f);
	public final static Color GREEN = new Color(0, 1, 0);
	public final static Color DARKGREEN = new Color(0, .5f, 0);
	public final static Color LIGHTBLUE = new Color(.25f, 1, 1);
	public final static Color BLUE = new Color(0, 0, 1);
	public final static Color DARKBLUE = new Color(0, 0, .5f);
	public final static Color LIGHTYELLOW = new Color(1, 1, .33f);
	public final static Color YELLOW = decode("#FFD700");
	public final static Color DARKYELLOW = new Color(.5f, .5f, 0);
	public final static Color LIGHTORANGE = new Color(1, .75f, .25f);
	public final static Color ORANGE = new Color(1, .5f, 0);
	public final static Color DARKORANGE = new Color(.5f, .25f, 0);
	
	private float red;
	private float green;
	private float blue;
	private float alpha;
	
	public Color(final float red, final float green, final float blue, final float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public Color(final float red, final float green, final float blue) {
		this(red, green, blue, 1);
	}
	
	public Color(final float rgb, final float alpha) {
		this(rgb, rgb, rgb, alpha);
	}
	
	public Color(final float rgb) {
		this(rgb, rgb, rgb, 1);
	}
	
	public static Color decode(String color) throws NumberFormatException {
		int i = Integer.decode(color).intValue();
		return new Color(((i >> 16) & 0xFF)/255f, ((i >> 8) & 0xFF)/255f, (i & 0xFF)/255f);
	}
	
	@Override
	public final String toString() {
		return "Red: "+this.red+" Green: "+this.green+" Blue: "+this.blue+" Alpha: "+this.alpha;
	}
	
	public final float red() {
		return this.red;
	}
	
	public final float getRed() {
		return this.red;
	}
	
	public final void setRed(final float red) {
		this.red = red;
	}
	
	public final float green() {
		return this.green;
	}
	
	public final float getGreen() {
		return this.green;
	}
	
	public final void setGreen(final float green) {
		this.green = green;
	}
	
	public final float blue() {
		return this.blue;
	}
	
	public final float getBlue() {
		return this.blue;
	}
	
	public final void setBlue(final float blue) {
		this.blue = blue;
	}
	
	public final float alpha() {
		return this.alpha;
	}
	
	public final float getAlpha() {
		return this.alpha;
	}
	
	public final void setAlpha(final float alpha) {
		this.alpha = alpha;
	}
}
