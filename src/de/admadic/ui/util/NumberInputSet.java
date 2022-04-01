package de.admadic.ui.util;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * @author Rainer Schwarze
 *
 */
public class NumberInputSet {
	JLabel label;
	JSpinner spinner;
	SpinnerNumberModel spinnerModel;
	JLabel info;
	Integer initialVal;
	Integer minVal;
	Integer maxVal;
	Integer stepVal;

	Integer value;	// input result

	/**
	 * @param name
	 * @param info
	 * @param initial
	 * @param min
	 * @param max
	 * @param step
	 */
	public NumberInputSet(
			String name, String info, 
			Integer initial, Integer min, Integer max, Integer step) {
		this.label = new JLabel(name);
		if (info!=null) this.info = new JLabel(info);
		if (min==null) min = Integer.valueOf(Integer.MIN_VALUE);
		if (max==null) max = Integer.valueOf(Integer.MAX_VALUE);
		if (step==null) step = Integer.valueOf(1);
		if (initial==null) {
			if (min.intValue()<=0 && max.intValue()>=0) {
				initial = Integer.valueOf(0);
			} else if (min.intValue()>=0) {
				initial = min;
			} else {
				initial = min;
			}
		}
		this.spinnerModel = new SpinnerNumberModel(initial, min, max, step);
		this.spinner = new JSpinner(this.spinnerModel);
	}

	/**
	 * @param name
	 * @param info
	 * @param initial
	 */
	public NumberInputSet(String name, String info, int initial) {
		this(name, info, Integer.valueOf(initial), null, null, null);
	}

	/**
	 * @param name
	 * @param info
	 * @param initial
	 * @param min
	 * @param max
	 * @param step
	 */
	public NumberInputSet(
			String name, String info, 
			int initial, int min, int max, int step) {
		this(
				name, info, 
				Integer.valueOf(initial), 
				Integer.valueOf(min), Integer.valueOf(max), 
				Integer.valueOf(step));
	}

	/**
	 * @return Returns the value.
	 */
	public Integer getValue() {
		return value;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(Integer value) {
		this.value = value;
	}
}