package com.talentstream.ats;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ResumeHtmlRendererResolver {

    private final Map<Integer, ResumeHtmlRenderer> rendererMap = new HashMap<>();

    public ResumeHtmlRendererResolver(HtmlResumeRendererV1 v1,
            HtmlResumeRendererV2 v2,
            HtmlResumeRendererV3 v3,
            HtmlResumeRendererV4 v4) {

        rendererMap.put(1, v1); // existing
        rendererMap.put(2, v2); // NEW template
        rendererMap.put(3, v3);
        rendererMap.put(4, v4); // Dark Sidebar
    }

    public ResumeHtmlRenderer resolve(int templateType) {
        ResumeHtmlRenderer renderer = rendererMap.get(templateType);
        if (renderer == null) {
            throw new IllegalArgumentException("Invalid template type: " + templateType);
        }
        return renderer;
    }
}
