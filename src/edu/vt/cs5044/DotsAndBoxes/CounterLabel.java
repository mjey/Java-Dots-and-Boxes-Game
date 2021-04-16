package edu.vt.cs5044.DotsAndBoxes;

import javax.swing.JLabel;

public class CounterLabel extends JLabel {
	private static final long serialVersionUID = 1L;
	private int count;
	private String description;
	private void update() {
		setText(description + Integer.toString(this.count));
	}
	public CounterLabel(String desc) {
		super();
		this.description = desc;
		reset();
	}
        
        public int getPoint()
        {
            return this.count;
        }
	
	public void increment() {
		this.count++;
		update();
	}

	
	public void add(int n) {
		this.count += n;
		update();
	}
        public void setPosition(String str)
        {
            this.description =str;
            update();
        }
	
	public void reset() {
		this.count = 0;
		update();
	}

    void decrease() {
        this.count--;
        update();
    }
}