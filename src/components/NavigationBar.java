package components;

import webx.A;
import webx.Div;
import webx.H1;
import webx.Header;
import webx.I;
import webx.Img;
import webx.P;
import webx.WebXContainerElement;

/**
 *
 * @author ERC
 */
public class NavigationBar extends WebXContainerElement {

	@Override
	public String render() {
		return new Header()
			.addChildren(
				new Div()
					.addChildren(
						new Div()
							.addChildren(
								new A()
									.addChild(
										new H1("SMLL | Respository")
											.addChild(
												new I().className("brand-block brand-logo")
											).className("")
									),
								new P("Home for SMLL packages")
									.className("text-sm")
							)
							.className("flex flex-col flex-grow"),
						new Div()
							.addChildren().className("flex justify-center"),
						new Div()
							.addChildren(
								new P("SMLL"),
								new Div()
									.addChildren(
										new A()
											.addChild(
												new I()
													.className("nes-icon github")
													.attr("style", "color: #f7d51d")
											)
									)
							)
							.className("flex flex-col flex-grow items-end")
					)
					.className("flex flex-row flex-grow")
			)
			.attr("style", "color: #f7d51d")
			.className("fixed p-2 w-full border-b-4 border-solid border-zinc-400 z-0")
			.render();

	}
}
