package nsu.library.service;

import java.io.*;
import java.util.zip.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class EpubMetadataReader {
    public static void main(String[] args) throws Exception {
        String epubPath = "hitman.epub";

        try (ZipFile zipFile = new ZipFile(epubPath)) {
            ZipEntry container = zipFile.getEntry("META-INF/container.xml");
            if (container == null) throw new FileNotFoundException("container.xml not found");

            // Step 1: Read container.xml to find the OPF file
            InputStream containerStream = zipFile.getInputStream(container);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document containerDoc = builder.parse(containerStream);
            String opfPath = containerDoc.getElementsByTagName("rootfile")
                    .item(0)
                    .getAttributes()
                    .getNamedItem("full-path")
                    .getTextContent();

            // Step 2: Open the OPF file and parse metadata
            ZipEntry opfEntry = zipFile.getEntry(opfPath);
            InputStream opfStream = zipFile.getInputStream(opfEntry);
            Document opfDoc = builder.parse(opfStream);

            NodeList metadata = opfDoc.getElementsByTagName("metadata");
            Node metaNode = metadata.item(0);

            if (metaNode != null) {
                NodeList children = metaNode.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        System.out.println(child.getNodeName() + " = " + child.getTextContent());
                    }
                }
            } else {
                System.out.println("No metadata found.");
            }
        }
    }
}

