import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents a group of {@link JRadioButton}s in which only one can be selected.
 *
 * @author Collin Alpert
 * @see javax.swing.JRadioButton
 * @see javax.swing.JPanel
 */
public class JRadioButtonGroup extends JPanel {

	private final List<JRadioButton> list;

	/**
	 * Constructor for adding a variable amount of radio buttons to the group.
	 *
	 * @param rbs The radio buttons to be added.
	 */
	public JRadioButtonGroup(JRadioButton... rbs) {
		this();
		for (JRadioButton b : rbs) this.add(b);
		list.addAll(Arrays.asList(rbs));
	}

	/**
	 * Default constructor.
	 */
	public JRadioButtonGroup() {
		list = new ArrayList<>();
	}

	/**
	 * Adds a {@link Component} to this group. This {@link Component} <i>has</i> to be a {@link JRadioButton}.
	 *
	 * @param comp The component to add.
	 * @return the component which was added.
	 * @throws IllegalArgumentException if the component is not a {@link JRadioButton}
	 */
	@Override
	public Component add(Component comp) {
		if (!(comp instanceof JRadioButton)) throw new IllegalArgumentException("Component is not a JRadioButton");
		((JRadioButton) comp).addActionListener(e -> action((JRadioButton) comp));
		super.add(comp);
		list.add((JRadioButton) comp);
		return comp;
	}

	/**
	 * Removes a radio button from the group.
	 *
	 * @param index The index of the checkbox to remove.
	 */
	@Override
	public void remove(int index) {
		super.remove(index);
		list.remove(index);
	}

	/**
	 * Adds a variable amount of radio buttons to the group
	 *
	 * @param radioButtons The radio buttons to be added.
	 */
	public void add(JRadioButton... radioButtons) {
		list.addAll(Arrays.asList(radioButtons));
		Arrays.stream(radioButtons).forEach(radioButton -> {
			radioButton.addActionListener(e -> action(radioButton));
			super.add(radioButton);
		});
	}

	/**
	 * Adds a list of radio buttons to the group.
	 *
	 * @param radioButtons The list of radio buttons to add.
	 */
	public void add(List<JRadioButton> radioButtons) {
		list.addAll(radioButtons);
		radioButtons.forEach(radioButton -> {
			radioButton.addActionListener(e -> action(radioButton));
			super.add(radioButton);
		});
	}

	/**
	 * @return the selected {@link JRadioButton}
	 */
	public JRadioButton getSelected() {
		return list.stream().filter(JRadioButton::isSelected).findFirst().orElseThrow(NoSuchElementException::new);
	}

	/**
	 * Makes sure that only one radio button can be selected.
	 *
	 * @param rb The newly selected radio button.
	 */
	private void action(JRadioButton rb) {
		JRadioButton[] rbs = Arrays.stream(this.getComponents()).map(e -> (JRadioButton) e).toArray(JRadioButton[]::new);
		for (JRadioButton ele : rbs) {
			if (ele == rb) continue;
			ele.setSelected(false);
		}
	}
}
