package com.digitalread.dto;

import java.util.ArrayList;
import java.util.List;

public class StructuredNode {
    private String id;
    private String type;
    private String tagName;
    private String content;
    private Integer level;
    private List<StructuredNode> children = new ArrayList<>();
    private String ariaRole;
    private String ariaLabel;
    private Integer startIndex;
    private Integer endIndex;

    public StructuredNode() {}

    public StructuredNode(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<StructuredNode> getChildren() {
        return children;
    }

    public void setChildren(List<StructuredNode> children) {
        this.children = children;
    }

    public String getAriaRole() {
        return ariaRole;
    }

    public void setAriaRole(String ariaRole) {
        this.ariaRole = ariaRole;
    }

    public String getAriaLabel() {
        return ariaLabel;
    }

    public void setAriaLabel(String ariaLabel) {
        this.ariaLabel = ariaLabel;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public void addChild(StructuredNode child) {
        this.children.add(child);
    }

    public String toPlainText() {
        StringBuilder sb = new StringBuilder();
        if (this.content != null && !this.content.isEmpty()) {
            sb.append(this.content).append("\n");
        }
        for (StructuredNode child : children) {
            sb.append(child.toPlainText());
        }
        return sb.toString();
    }
}
