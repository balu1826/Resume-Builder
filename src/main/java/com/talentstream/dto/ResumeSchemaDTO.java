package com.talentstream.dto;

import java.util.List;

public class ResumeSchemaDTO {

    private String header;
    private List<Section> sections;

    public String getHeader() { return header; }
    public void setHeader(String header) { this.header = header; }

    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }

    public static class Section {
        private String title;
        private List<String> lines;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public List<String> getLines() { return lines; }
        public void setLines(List<String> lines) { this.lines = lines; }
    }
}
