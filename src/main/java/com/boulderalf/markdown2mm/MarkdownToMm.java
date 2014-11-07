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


		String text = Files.toString(new File("C:\\ifactor\\products\\notifi_2.7\\nve-notifi\\design_documents\\LargeCustomerAlerts\\Notifi-LargeCustomerAlerts-design.md"), Charsets.UTF_8);

		RootNode astRoot = pegDownProcessor.parseMarkdown(text.toCharArray());

		ToMmSerializer toMmSerializer = new ToMmSerializer();
		String mm = toMmSerializer.toMmXmlString(astRoot);

		File newFile = new File("C:\\ifactor\\products\\notifi_2.7\\nve-notifi\\design_documents\\LargeCustomerAlerts\\Notifi-LargeCustomerAlerts-design.mm");
		Files.write(mm.getBytes(), newFile);

	}

}
