# markdown2mm

Converts a markdown file into a freeplane (.mm) file using `ToMmSerializer`.

Here is an example of how to convert a Markdown file. 

~~~java
package com.boulderalf.markdown2mm;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.RootNode;

import java.io.File;

public class MarkdownToMm {

	public static void main(String[] args) throws Exception {
		PegDownProcessor pegDownProcessor = new PegDownProcessor(Extensions.ALL);

		String text = Files.toString(new File("C:\\document.md"), Charsets.UTF_8);

		RootNode astRoot = pegDownProcessor.parseMarkdown(text.toCharArray());

		ToMmSerializer toMmSerializer = new ToMmSerializer();
		String mm = toMmSerializer.toMmXmlString(astRoot);

		File newFile = new File("C:\\freeplane-document.mm");
		Files.write(mm.getBytes(), newFile);
	}
}

~~~
