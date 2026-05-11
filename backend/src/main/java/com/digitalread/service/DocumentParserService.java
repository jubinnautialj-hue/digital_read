package com.digitalread.service;

import com.digitalread.dto.StructuredNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentParserService {

    private static final Set<String> SUPPORTED_FORMATS = Set.of(
            "pdf", "doc", "docx", "txt", "html", "htm", "md", "markdown"
    );

    private static final Pattern HEADING_PATTERN = Pattern.compile("^(#{1,6})\\s+(.+)$");
    private static final Pattern LIST_PATTERN = Pattern.compile("^([*\\-+]|\\d+\\.)\\s+(.+)$");

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isSupported(String filename) {
        if (filename == null) return false;
        String ext = getFileExtension(filename).toLowerCase();
        return SUPPORTED_FORMATS.contains(ext);
    }

    public String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf('.');
        return lastDot == -1 ? "" : filename.substring(lastDot + 1);
    }

    public StructuredNode parseDocument(MultipartFile file) throws IOException {
        String ext = getFileExtension(file.getOriginalFilename()).toLowerCase();
        
        return switch (ext) {
            case "pdf" -> parsePdf(file.getInputStream());
            case "doc" -> parseDoc(file.getInputStream());
            case "docx" -> parseDocx(file.getInputStream());
            case "txt" -> parseTxt(file.getInputStream());
            case "html", "htm" -> parseHtml(file.getInputStream());
            case "md", "markdown" -> parseMarkdown(file.getInputStream());
            default -> throw new IllegalArgumentException("不支持的文件格式: " + ext);
        };
    }

    public StructuredNode parseContent(String content, String fileType) {
        if (content == null || content.isEmpty()) {
            return createEmptyNode();
        }

        String ext = fileType != null ? fileType.toLowerCase() : "txt";
        
        return switch (ext) {
            case "html", "htm" -> parseHtmlContent(content);
            case "md", "markdown" -> parseMarkdownContent(content);
            default -> parsePlainText(content);
        };
    }

    private StructuredNode parsePdf(InputStream is) throws IOException {
        StructuredNode root = new StructuredNode("document", null);
        root.setTagName("article");
        root.setAriaRole("article");
        
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(is))) {
            for (int pageNum = 1; pageNum <= pdfDoc.getNumberOfPages(); pageNum++) {
                String pageText = PdfTextExtractor.getTextFromPage(
                        pdfDoc.getPage(pageNum),
                        new LocationTextExtractionStrategy()
                );
                
                if (!pageText.trim().isEmpty()) {
                    StructuredNode sectionNode = new StructuredNode("section", null);
                    sectionNode.setTagName("section");
                    sectionNode.setAriaLabel("第 " + pageNum + " 页");
                    
                    parseTextIntoStructure(pageText, sectionNode);
                    root.addChild(sectionNode);
                }
            }
        }
        
        assignAriaAttributes(root);
        return root;
    }

    private StructuredNode parseDoc(InputStream is) throws IOException {
        StructuredNode root = new StructuredNode("document", null);
        root.setTagName("article");
        root.setAriaRole("article");
        
        try (HWPFDocument doc = new HWPFDocument(is);
             WordExtractor extractor = new WordExtractor(doc)) {
            
            String[] paragraphs = extractor.getParagraphText();
            for (String paragraph : paragraphs) {
                if (paragraph.trim().isEmpty()) continue;
                
                StructuredNode paraNode = new StructuredNode("paragraph", paragraph.trim());
                paraNode.setTagName("p");
                root.addChild(paraNode);
            }
        }
        
        assignAriaAttributes(root);
        return root;
    }

    private StructuredNode parseDocx(InputStream is) throws IOException {
        StructuredNode root = new StructuredNode("document", null);
        root.setTagName("article");
        root.setAriaRole("article");
        
        try (XWPFDocument doc = new XWPFDocument(is);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            StructuredNode currentSection = null;
            
            for (XWPFParagraph para : paragraphs) {
                String text = para.getText().trim();
                if (text.isEmpty()) continue;
                
                String style = para.getStyle();
                int headingLevel = getHeadingLevel(style);
                
                if (headingLevel > 0) {
                    currentSection = new StructuredNode("heading", text);
                    currentSection.setTagName("h" + headingLevel);
                    currentSection.setLevel(headingLevel);
                    currentSection.setAriaRole("heading");
                    currentSection.setAriaLabel("标题 " + headingLevel + ": " + text);
                    root.addChild(currentSection);
                } else if (para.getNumID() != null) {
                    StructuredNode listItem = new StructuredNode("listitem", text);
                    listItem.setTagName("li");
                    listItem.setAriaRole("listitem");
                    
                    if (currentSection != null) {
                        if (currentSection.getChildren().isEmpty() || 
                            !"list".equals(currentSection.getChildren().get(currentSection.getChildren().size() - 1).getType())) {
                            StructuredNode listNode = new StructuredNode("list", null);
                            listNode.setTagName("ul");
                            listNode.setAriaRole("list");
                            currentSection.addChild(listNode);
                        }
                        StructuredNode listNode = currentSection.getChildren().get(currentSection.getChildren().size() - 1);
                        listNode.addChild(listItem);
                    } else {
                        root.addChild(listItem);
                    }
                } else {
                    StructuredNode paraNode = new StructuredNode("paragraph", text);
                    paraNode.setTagName("p");
                    
                    if (currentSection != null) {
                        currentSection.addChild(paraNode);
                    } else {
                        root.addChild(paraNode);
                    }
                }
            }
        }
        
        assignAriaAttributes(root);
        return root;
    }

    private int getHeadingLevel(String style) {
        if (style == null) return 0;
        String lower = style.toLowerCase();
        if (lower.startsWith("heading")) {
            try {
                return Integer.parseInt(lower.replaceAll("\\D", ""));
            } catch (Exception e) {
                return 1;
            }
        }
        return 0;
    }

    private StructuredNode parseTxt(InputStream is) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return parsePlainText(content.toString());
    }

    private StructuredNode parsePlainText(String content) {
        StructuredNode root = new StructuredNode("document", null);
        root.setTagName("article");
        root.setAriaRole("article");
        
        parseTextIntoStructure(content, root);
        assignAriaAttributes(root);
        return root;
    }

    private void parseTextIntoStructure(String content, StructuredNode parent) {
        String[] lines = content.split("\\n");
        StructuredNode currentPara = null;
        StringBuilder paraContent = new StringBuilder();
        
        for (String line : lines) {
            String trimmed = line.trim();
            
            if (trimmed.isEmpty()) {
                if (paraContent.length() > 0) {
                    currentPara = new StructuredNode("paragraph", paraContent.toString().trim());
                    currentPara.setTagName("p");
                    parent.addChild(currentPara);
                    paraContent = new StringBuilder();
                }
                continue;
            }
            
            if (looksLikeHeading(trimmed)) {
                if (paraContent.length() > 0) {
                    currentPara = new StructuredNode("paragraph", paraContent.toString().trim());
                    currentPara.setTagName("p");
                    parent.addChild(currentPara);
                    paraContent = new StringBuilder();
                }
                
                int level = determineHeadingLevel(trimmed);
                StructuredNode headingNode = new StructuredNode("heading", trimmed);
                headingNode.setTagName("h" + level);
                headingNode.setLevel(level);
                headingNode.setAriaRole("heading");
                parent.addChild(headingNode);
            } else if (looksLikeListItem(trimmed)) {
                if (paraContent.length() > 0) {
                    currentPara = new StructuredNode("paragraph", paraContent.toString().trim());
                    currentPara.setTagName("p");
                    parent.addChild(currentPara);
                    paraContent = new StringBuilder();
                }
                
                StructuredNode listItem = new StructuredNode("listitem", trimmed);
                listItem.setTagName("li");
                listItem.setAriaRole("listitem");
                
                if (parent.getChildren().isEmpty() || 
                    !"list".equals(parent.getChildren().get(parent.getChildren().size() - 1).getType())) {
                    StructuredNode listNode = new StructuredNode("list", null);
                    listNode.setTagName("ul");
                    listNode.setAriaRole("list");
                    parent.addChild(listNode);
                }
                StructuredNode listNode = parent.getChildren().get(parent.getChildren().size() - 1);
                listNode.addChild(listItem);
            } else {
                if (paraContent.length() > 0) {
                    paraContent.append(" ");
                }
                paraContent.append(trimmed);
            }
        }
        
        if (paraContent.length() > 0) {
            currentPara = new StructuredNode("paragraph", paraContent.toString().trim());
            currentPara.setTagName("p");
            parent.addChild(currentPara);
        }
    }

    private boolean looksLikeHeading(String line) {
        if (line.length() > 100) return false;
        if (line.endsWith("。") || line.endsWith("，")) return false;
        if (line.length() < 2) return false;
        
        int chineseCount = 0;
        for (char c : line.toCharArray()) {
            if (Character.isIdeographic(c)) chineseCount++;
        }
        
        return chineseCount > 0 && line.length() < 80;
    }

    private int determineHeadingLevel(String line) {
        int len = line.length();
        if (len < 10) return 1;
        if (len < 20) return 2;
        return 3;
    }

    private boolean looksLikeListItem(String line) {
        return line.startsWith("●") || line.startsWith("○") || 
               line.startsWith("■") || line.startsWith("□") ||
               line.startsWith("*") || line.startsWith("-") ||
               line.matches("^\\d+[.、]\\s*.+");
    }

    private StructuredNode parseHtml(InputStream is) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return parseHtmlContent(content.toString());
    }

    private StructuredNode parseHtmlContent(String html) {
        StructuredNode root = new StructuredNode("document", null);
        root.setTagName("article");
        root.setAriaRole("article");
        
        Pattern pattern = Pattern.compile("<(h[1-6]|p|li|ul|ol|div|section|article|header|footer|nav|aside)[^>]*>(.*?)</\\1>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        
        while (matcher.find()) {
            String tag = matcher.group(1).toLowerCase();
            String inner = matcher.group(2);
            
            String plainText = stripHtmlTags(inner).trim();
            if (plainText.isEmpty()) continue;
            
            StructuredNode node = new StructuredNode();
            node.setTagName(tag);
            node.setContent(plainText);
            
            switch (tag) {
                case "h1", "h2", "h3", "h4", "h5", "h6" -> {
                    node.setType("heading");
                    node.setLevel(Integer.parseInt(tag.substring(1)));
                    node.setAriaRole("heading");
                    node.setAriaLabel("标题: " + plainText);
                }
                case "p" -> {
                    node.setType("paragraph");
                }
                case "li" -> {
                    node.setType("listitem");
                    node.setAriaRole("listitem");
                }
                case "ul", "ol" -> {
                    node.setType("list");
                    node.setAriaRole("list");
                }
                case "section", "article", "div" -> {
                    node.setType("section");
                    node.setAriaRole("region");
                }
                default -> node.setType("paragraph");
            }
            
            root.addChild(node);
        }
        
        if (root.getChildren().isEmpty()) {
            parseTextIntoStructure(stripHtmlTags(html), root);
        }
        
        assignAriaAttributes(root);
        return root;
    }

    private String stripHtmlTags(String html) {
        if (html == null) return "";
        return html.replaceAll("<[^>]+>", "")
                   .replaceAll("&nbsp;", " ")
                   .replaceAll("&amp;", "&")
                   .replaceAll("&lt;", "<")
                   .replaceAll("&gt;", ">")
                   .replaceAll("&quot;", "\"")
                   .replaceAll("\\s+", " ")
                   .trim();
    }

    private StructuredNode parseMarkdown(InputStream is) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return parseMarkdownContent(content.toString());
    }

    private StructuredNode parseMarkdownContent(String markdown) {
        StructuredNode root = new StructuredNode("document", null);
        root.setTagName("article");
        root.setAriaRole("article");
        
        String[] lines = markdown.split("\\n");
        StructuredNode currentSection = null;
        StructuredNode currentList = null;
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                currentList = null;
                continue;
            }
            
            Matcher headingMatcher = HEADING_PATTERN.matcher(trimmed);
            if (headingMatcher.matches()) {
                int level = headingMatcher.group(1).length();
                String text = headingMatcher.group(2);
                
                currentSection = new StructuredNode("heading", text);
                currentSection.setTagName("h" + level);
                currentSection.setLevel(level);
                currentSection.setAriaRole("heading");
                currentSection.setAriaLabel("标题 " + level + ": " + text);
                root.addChild(currentSection);
                currentList = null;
                continue;
            }
            
            Matcher listMatcher = LIST_PATTERN.matcher(trimmed);
            if (listMatcher.matches()) {
                String text = listMatcher.group(2);
                
                StructuredNode listItem = new StructuredNode("listitem", text);
                listItem.setTagName("li");
                listItem.setAriaRole("listitem");
                
                if (currentList == null) {
                    currentList = new StructuredNode("list", null);
                    currentList.setTagName("ul");
                    currentList.setAriaRole("list");
                    
                    if (currentSection != null) {
                        currentSection.addChild(currentList);
                    } else {
                        root.addChild(currentList);
                    }
                }
                
                currentList.addChild(listItem);
                continue;
            }
            
            StructuredNode paraNode = new StructuredNode("paragraph", trimmed);
            paraNode.setTagName("p");
            
            if (currentSection != null) {
                currentSection.addChild(paraNode);
            } else {
                root.addChild(paraNode);
            }
            currentList = null;
        }
        
        assignAriaAttributes(root);
        return root;
    }

    private void assignAriaAttributes(StructuredNode node) {
        if (node.getId() == null) {
            node.setId(UUID.randomUUID().toString().substring(0, 8));
        }
        
        if (node.getAriaRole() == null) {
            if ("heading".equals(node.getType())) {
                node.setAriaRole("heading");
            } else if ("paragraph".equals(node.getType())) {
                node.setAriaRole("paragraph");
            }
        }
        
        for (StructuredNode child : node.getChildren()) {
            assignAriaAttributes(child);
        }
    }

    private StructuredNode createEmptyNode() {
        StructuredNode root = new StructuredNode("document", null);
        root.setTagName("article");
        root.setAriaRole("article");
        return root;
    }

    public String nodeToJson(StructuredNode node) throws IOException {
        return objectMapper.writeValueAsString(node);
    }

    public StructuredNode jsonToNode(String json) throws IOException {
        return objectMapper.readValue(json, StructuredNode.class);
    }

    public List<String> extractHeadings(StructuredNode node) {
        List<String> headings = new ArrayList<>();
        extractHeadingsRecursive(node, headings);
        return headings;
    }

    private void extractHeadingsRecursive(StructuredNode node, List<String> headings) {
        if ("heading".equals(node.getType()) && node.getContent() != null) {
            headings.add(node.getContent());
        }
        for (StructuredNode child : node.getChildren()) {
            extractHeadingsRecursive(child, headings);
        }
    }

    public List<String> getAllParagraphs(StructuredNode node) {
        List<String> paragraphs = new ArrayList<>();
        getAllParagraphsRecursive(node, paragraphs);
        return paragraphs;
    }

    private void getAllParagraphsRecursive(StructuredNode node, List<String> paragraphs) {
        if (("paragraph".equals(node.getType()) || "heading".equals(node.getType()) || "listitem".equals(node.getType())) 
            && node.getContent() != null && !node.getContent().isEmpty()) {
            paragraphs.add(node.getContent());
        }
        for (StructuredNode child : node.getChildren()) {
            getAllParagraphsRecursive(child, paragraphs);
        }
    }
}
