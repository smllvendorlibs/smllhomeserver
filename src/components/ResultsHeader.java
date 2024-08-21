/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import webx.Button;
import webx.Div;
import webx.H1;
import webx.I;
import webx.P;
import webx.WebXContainerElement;

/**
 *
 * @author hexaredecimal
 */
public class ResultsHeader extends WebXContainerElement {

	private String term;

	public ResultsHeader(String term) {
		this.term = term;
	}

	@Override
	public String render() {
		return new Div()
			.addChildren(
				new H1(String.format("Showing results for: %s", term))
					.className("p-5"),
				new Div()
					.addChildren(
						new P("Home"),
						new Div()
							.addChildren(
								new Button()
									.addChild(
										new I()
											.className("nes-icon close")
											.attr("style", "color: #f7d51d")
									)
									.hxPost("/v1/reload")
									.hxSwap("innerHTML")
									.hxTarget("#page")
							)
					)
					.className("flex flex-col flex-grow items-end")
			)
			.className("flex flex-row w-full border-b-4 border-solid border-zinc-400")
			.render();
	}
}
