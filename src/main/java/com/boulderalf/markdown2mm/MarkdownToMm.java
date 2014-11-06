package com.boulderalf.markdown2mm;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import generated.Map;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.RootNode;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

public class MarkdownToMm {

	public static void main(String[] args) throws Exception {
		PegDownProcessor pegDownProcessor = new PegDownProcessor(Extensions.ALL);


		String text = Files.toString(new File("C:\\ifactor\\products\\notifi_2.7\\nve-notifi\\design_documents\\LargeCustomerAlerts\\Notifi-LargeCustomerAlerts-design.md"), Charsets.UTF_8);

		RootNode astRoot = pegDownProcessor.parseMarkdown(text.toCharArray());

		ToMmSerializer toMmSerializer = new ToMmSerializer();
		Map map = toMmSerializer.toMm(astRoot);

		String mm = getXML(map);

		File newFile = new File("C:\\ifactor\\products\\notifi_2.7\\nve-notifi\\design_documents\\LargeCustomerAlerts\\Notifi-LargeCustomerAlerts-design.mm");
		Files.write(mm.getBytes(), newFile);

	}

	public static String getXML(Object obj) throws JAXBException {

		// Omit off the XML Declaration because Freeplane does not like it.
		StringWriter stringWriter = new StringWriter();
		JAXBContext context = null;
		Marshaller marshaller = null;
		context = JAXBContext.newInstance(obj.getClass());
		marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.marshal(obj, stringWriter);
		String xmlString = stringWriter.getBuffer().toString();

		StringWriter writer = new StringWriter();

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(new StreamSource(new StringReader(xmlString)), new StreamResult(writer));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return writer.getBuffer().toString();
	}
}
