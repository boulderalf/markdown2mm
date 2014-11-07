package com.boulderalf.markdown2mm;

import generated.Html;
import generated.Map;
import generated.Richcontent;
import org.pegdown.ast.*;
import org.pegdown.ast.Node;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.parboiled.common.Preconditions.checkArgNotNull;

public class ToMmSerializer implements Visitor {
	protected Stack<MmNodeWrapper> stack = new Stack<MmNodeWrapper>();

	protected Map map = new Map();

	protected StringBuilder currentStringBuilder = null;
	protected StringBuilder mainStringBuilder = null;
	protected Stack<Integer> currentNumberOfTableColumns = new Stack<Integer>();

	public ToMmSerializer() {
		map.setVersion("freeplane 1.3.0");
		generated.Node node = createNode(0);
		node.setTEXT("");
		map.setNode(node);
	}

	public Map toMm(RootNode astRoot) {
		currentStringBuilder = mainStringBuilder = new StringBuilder();
		checkArgNotNull(astRoot, "astRoot");
		astRoot.accept(this);
		flushStringBuilderToCurrentNode();
		return map;
	}

	@Override
	public void visit(AbbreviationNode abbreviationNode) {
		visitChildren(abbreviationNode);
	}

	@Override
	public void visit(AutoLinkNode autoLinkNode) {
		currentStringBuilder.append(autoLinkNode.getText());
	}

	@Override
	public void visit(BlockQuoteNode blockQuoteNode) {
		visitChildren(blockQuoteNode);
	}

	@Override
	public void visit(BulletListNode bulletListNode) {
		visitChildren(bulletListNode);
	}

	@Override
	public void visit(CodeNode codeNode) {
		currentStringBuilder.append("`");
		currentStringBuilder.append(codeNode.getText());
		currentStringBuilder.append("`");
	}

	@Override
	public void visit(DefinitionListNode definitionListNode) {
		visitChildren(definitionListNode);
	}

	@Override
	public void visit(DefinitionNode definitionNode) {
		visitChildren(definitionNode);
	}

	@Override
	public void visit(DefinitionTermNode definitionTermNode) {
		visitChildren(definitionTermNode);
	}

	@Override
	public void visit(ExpImageNode expImageNode) {
		visitChildren(expImageNode);
	}

	@Override
	public void visit(ExpLinkNode expLinkNode) {
		visitChildren(expLinkNode);
	}

	@Override
	public void visit(HeaderNode headerNode) {
		StringBuilder sb = new StringBuilder();

		currentStringBuilder = sb;

		visitChildren(headerNode);

		currentStringBuilder = mainStringBuilder;

		generated.Node node = createNode(headerNode.getLevel());

		// strip off any existing leading heading numbers because we want to use Freeplane
		// to number them correctly
		String headingText = sb.toString();

		Pattern r = Pattern.compile("([0-9\\.]* *(.*))");
		Matcher m = r.matcher(headingText);

		if (m.find()) {
			headingText = m.group(2);
		}

		node.setTEXT(headingText);
	}

	@Override
	public void visit(HtmlBlockNode htmlBlockNode) {
		currentStringBuilder.append(htmlBlockNode.getText());
	}

	@Override
	public void visit(InlineHtmlNode inlineHtmlNode) {
		currentStringBuilder.append(inlineHtmlNode.getText());
	}

	@Override
	public void visit(ListItemNode listItemNode) {
		currentStringBuilder.append("* ");
		visitChildren(listItemNode);
		currentStringBuilder.append("\n");
	}

	@Override
	public void visit(MailLinkNode mailLinkNode) {
		currentStringBuilder.append(mailLinkNode.getText());
	}

	@Override
	public void visit(OrderedListNode orderedListNode) {
		visitChildren(orderedListNode);
	}

	@Override
	public void visit(ParaNode paraNode) {
		visitChildren(paraNode);
		currentStringBuilder.append("\n\n");
	}

	@Override
	public void visit(QuotedNode quotedNode) {
		switch (quotedNode.getType()) {
			case DoubleAngle:
			case Double:
				currentStringBuilder.append("\"");
				visitChildren(quotedNode);
				currentStringBuilder.append("\"");
				break;
			case Single:
				currentStringBuilder.append("'");
				visitChildren(quotedNode);
				currentStringBuilder.append("'");
				break;
		}
	}

	@Override
	public void visit(ReferenceNode referenceNode) {
		visitChildren(referenceNode);
	}

	@Override
	public void visit(RefImageNode refImageNode) {
		visitChildren(refImageNode);
	}

	@Override
	public void visit(RefLinkNode refLinkNode) {
		visitChildren(refLinkNode);
	}

	@Override
	public void visit(RootNode rootNode) {
		visitChildren(rootNode);
	}

	@Override
	public void visit(SimpleNode simpleNode) {

		switch (simpleNode.getType()) {
			case Apostrophe:
				currentStringBuilder.append("'");
				break;
			case Ellipsis:
				currentStringBuilder.append("...");
				break;
			case Emdash:
				currentStringBuilder.append("-");
				break;
			case Endash:
				currentStringBuilder.append("-");
				break;
			case HRule:
				currentStringBuilder.append("-----------------------------");
				break;
			case Linebreak:
				currentStringBuilder.append("\n");
				break;
			case Nbsp:
				currentStringBuilder.append(" ");
				break;
			default:
				throw new IllegalStateException();
		}
	}

	@Override
	public void visit(SpecialTextNode specialTextNode) {
		currentStringBuilder.append(specialTextNode.getText());
	}

	@Override
	public void visit(StrikeNode strikeNode) {
		visitChildren(strikeNode);
	}

	@Override
	public void visit(StrongEmphSuperNode strongEmphSuperNode) {

		if(strongEmphSuperNode.isClosed()){
			if(strongEmphSuperNode.isStrong()) {
				currentStringBuilder.append("**");
				visitChildren(strongEmphSuperNode);
				currentStringBuilder.append("**");
			}
			else {
				currentStringBuilder.append("*");
				visitChildren(strongEmphSuperNode);
				currentStringBuilder.append("*");
			}
		} else {
			//sequence was not closed, treat open chars as ordinary chars
			currentStringBuilder.append(strongEmphSuperNode.getChars());
			visitChildren(strongEmphSuperNode);
		}
	}

	@Override
	public void visit(TableBodyNode tableBodyNode) {

		for (Node node : tableBodyNode.getChildren()) {
			visit((TableRowNode)node);
		}


	}

	@Override
	public void visit(TableCaptionNode tableCaptionNode) {
		visitChildren(tableCaptionNode);
	}

	@Override
	public void visit(TableCellNode tableCellNode) {
		visitChildren(tableCellNode);
	}

	@Override
	public void visit(TableColumnNode tableColumnNode) {

	}

	@Override
	public void visit(TableHeaderNode tableHeaderNode) {

		TableRowNode trn = (TableRowNode)tableHeaderNode.getChildren().get(0);

		visit(trn);

		// create the header divider
		boolean first = true;
		for (Node col : trn.getChildren()) {

			if (first) {
				first = false;
			}
			else {
				currentStringBuilder.append("|");
			}

			currentStringBuilder.append("-----");

		}

		currentStringBuilder.append("\n");
	}

	@Override
	public void visit(TableNode tableNode) {

		currentNumberOfTableColumns.push(tableNode.getColumns().size());
		visitChildren(tableNode);
		currentNumberOfTableColumns.pop();
		currentStringBuilder.append(" \n");
	}

	@Override
	public void visit(TableRowNode tableRowNode) {
		// create the header row
		boolean first = true;
		int columnCount = 0;
		for (Node node : tableRowNode.getChildren()) {
			columnCount += 1;
			TableCellNode tcn = (TableCellNode) node;

			if (first) {
				first = false;
			}
			else {
				currentStringBuilder.append("|");
			}
			visit(tcn);
		}

		// it is possible that not all table cells for this row have been populated.  In that case
		// we need to make sure we put extra separator characters at the end.
		for (int i = columnCount; i < currentNumberOfTableColumns.peek(); i++) {
			currentStringBuilder.append(" |");
		}
		currentStringBuilder.append("\n");
	}

	@Override
	public void visit(VerbatimNode verbatimNode) {
		currentStringBuilder.append(String.format("~~~%s\n",verbatimNode.getType()));

		String text = verbatimNode.getText();

		if (verbatimNode.getType().equalsIgnoreCase("xml")) {
			text = prettyFormatXml(text,2);
			text = text.replace("><", ">\n<");
		}

		currentStringBuilder.append(text);
		currentStringBuilder.append("~~~\n\n");
	}

	@Override
	public void visit(WikiLinkNode wikiLinkNode) {
		currentStringBuilder.append(wikiLinkNode.getText());
	}

	@Override
	public void visit(TextNode textNode) {
		currentStringBuilder.append(textNode.getText());
	}

	@Override
	public void visit(SuperNode superNode) {
		visitChildren(superNode);
	}

	@Override
	public void visit(Node node) {
	}

	// helpers

	protected void visitChildren(SuperNode node) {
		for (Node child : node.getChildren()) {
			child.accept(this);
		}
	}

	private void visitChildren(SimpleNode node) {
		for (Node child : node.getChildren()) {
			child.accept(this);
		}
	}


	private generated.Node getCurrentNode() {
		return stack.peek().getNode();
	}

	/**
	 * Creates a new node at level (level=0 is the root)
	 * @param level
	 * @return
	 */
	private generated.Node createNode(int level) {
		// every time we encounter a header we also need to flush the contents of
		// currentStringBuilder into a richContent

		if (level > 0) {
			flushStringBuilderToCurrentNode();
		}

		// first, adjust the stack so we get a suitable parent node
		while (!stack.isEmpty() && (level <= stack.peek().getLevel())) {
			stack.pop();
		}

		// then create a new node and make it a child of the currentNode()...
		generated.Node newNode = new generated.Node();
		if (level > 0) {
			getCurrentNode().getArrowlinkOrCloudOrEdge().add(newNode);
		}

		// finally push the newNode onto the stack.
		stack.push(new MmNodeWrapper(newNode, level));

		return newNode;
	}

	/**
	 * flushes the contents of currentStringBuilder into currentNode()
	 */
	private void flushStringBuilderToCurrentNode() {
		Html html = new Html();
		html.getAny().add(buildHtmlForNode(currentStringBuilder.toString()));
		mainStringBuilder = currentStringBuilder = new StringBuilder();
		Richcontent richcontent = new Richcontent();
		richcontent.setHtml(html);
		richcontent.setTYPE("DETAILS");
		richcontent.setHIDDEN("true");
		getCurrentNode().getArrowlinkOrCloudOrEdge().add(richcontent);
	}


	/**
	 * builds an Element that contains text as a paragraph element
	 * @param text
	 */
	private Element buildHtmlForNode(String text) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Element body = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			// Create from whole cloth
			body = (Element) document.createElement("body");
			document.appendChild(body);

			for (String line : text.split("\n")) {
				Element p = (Element) body.appendChild(document.createElement("p") );
				p.appendChild(document.createTextNode(line));
			}
		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();
		}

		return body;
	}

	public void addEntityReferences(org.w3c.dom.Node node) {

		int type = node.getNodeType();
		if (type == org.w3c.dom.Node.TEXT_NODE) {
			// the only type with attributes
			Text text = (Text) node;
			String s = text.getNodeValue();
			int nbsp = s.indexOf('\u00A0'); // finds the first A0
			if (nbsp != -1) {
				Text middle = text.splitText(nbsp);
				Text end = middle.splitText(1);
				org.w3c.dom.Node parent = text.getParentNode();
				Document factory = text.getOwnerDocument();
				EntityReference ref = factory.createEntityReference("nbsp");
				parent.replaceChild(ref, middle);
				addEntityReferences(end); // finds any subsequent A0s
//				System.out.println("Added");
			}
		} // end if

		else if (node.hasChildNodes()) {
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				org.w3c.dom.Node child = children.item(i);
				addEntityReferences(child);
			} // end for
		} // end if

	}  // end addEntityReferences()

	public String prettyFormatXml(String input, int indent) {
		try {
			Source xmlInput = new StreamSource(new StringReader(input));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", indent);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlInput, xmlOutput);
			return xmlOutput.getWriter().toString();
		} catch (Exception e) {
			throw new RuntimeException(e); // simple exception handling, please review it
		}
	}

}
