/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import webx.A;
import webx.Div;
import webx.P;
import webx.WebXContainerElement;

/**
 *
 * @author hexaredecimal
 */
public class PackageGroup extends WebXContainerElement {

	private String name, author, authorurl, url, description; 
	public PackageGroup(String name, String author, String authorurl, String url, String description) {
		this.name = name; 
		this.author = author; 
		this.url = url;
		this.authorurl = authorurl; 
		this.description = description;
	}
	
	@Override
	public String render() {

		return new Div()
			.addChildren(
				new P(String.format("Package Name: %s", name)),
				new P("Package Author: ")
					.addChild(
						new A(author)
							.href(authorurl)
					),

				new P("Repo url: ")
					.addChild(
						new A(url)
							.href(url)
					),
				new P(description)
			)
			.className("flex flex-col border border-red-500 bg-red-100 p-5 my-4")
			.render();
	}
}
