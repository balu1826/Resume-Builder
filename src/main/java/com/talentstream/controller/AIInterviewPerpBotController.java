package com.talentstream.controller;

import com.google.gson.JsonObject;
import com.talentstream.dto.AIInterviewPrepBotDTO;
import com.talentstream.service.AIInterviewPrepBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/aiPrepModel")
public class AIInterviewPerpBotController {

    @Autowired
    private AIInterviewPrepBotService aiInterviewPrepBotService;

    @PostMapping("/postQuery")
    public ResponseEntity<?> query(
            @Valid @RequestBody AIInterviewPrepBotDTO request,
            BindingResult bindingResult) {
        if (request == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Request body cannot be null\"}");
        }

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        JsonObject jsonResponse = aiInterviewPrepBotService.answer(request);
        System.out.println(jsonResponse.toString());

        return ResponseEntity.ok(jsonResponse.toString());
    }
}
