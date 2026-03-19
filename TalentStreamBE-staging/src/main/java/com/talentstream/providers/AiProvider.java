package com.talentstream.providers;

import com.talentstream.config.AiConfig;

public interface AiProvider {

	   String generate(String prompt, AiConfig config);
}