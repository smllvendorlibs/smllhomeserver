package components;

import webx.Div;
import webx.WebXContainerElement;

/**
 *
 * @author hexaredecimal
 */
public class HomeView extends WebXContainerElement {

	@Override
	public String render() {
		return new Div()
			.addChildren(
				new NavigationBar(),
				new SearchSpace()
			).render();
	}
}
