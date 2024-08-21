/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import webx.Button;
import webx.Div;
import webx.Img;
import webx.Input;
import webx.Label;
import webx.WebXContainerElement;

/**
 *
 * @author hexaredecimal
 */
public class SearchSpace extends WebXContainerElement {

	@Override
	public String render() {
		return new Div()
			.addChildren(
				new Div()
					.addChildren(
						new Div()
							.addChildren(
								new Div()
									.addChildren(
										new Img()
											.src("/images/brand.png")
											.className("w-24")
									).className("flex w-full justify-center items-center"),
								new Label("Find what your next project needs")
									.className("p-5"),
								new Input()
									.hxPost("/v1/search")
									.hxTrigger("keyup[keyCode==13]")
									.hxSwap("innerHTML")
									.hxTarget("#page")
									.hxVals("js:{\"term\" : search_field.value}")
									.className("p-2 nes-input is-warning w-11/12")
									.attr("type", "text")
									.id("search_field")
							)
							.className("nes-field p-2"),
						new Button("Search")
							.hxPost("/v1/search")
							.hxSwap("innerHTML")
							.hxTarget("#page")
							.hxVals("js:{\"term\" : search_field.value}")
							.className("nes-btn is-warning")
					)
					.className("flex flex-col resize w-full justify-center items-center")
			)
			.className("flex w-full m-auto min-h-screen justify-center items-center z-0")
			.render();
	}
}
