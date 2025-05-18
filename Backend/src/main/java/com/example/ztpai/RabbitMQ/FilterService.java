package com.example.ztpai.RabbitMQ;


import com.example.ztpai.DTO.MessageDTO;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;

@Service
public class FilterService {

    private static final Set<String> badWords = Set.of("chuj", "kurwa", "jebac", "jebać", "zjebać", "zjebał", "zjebała", "pierdole", "pierdol", "pierdolę", "pierdolą", "pierdolcie", "pierdolcie się", "pierdolenie", "pierdolenia", "pierdolenie się", "pierdolić", "pierdolił", "pierdoliła", "pierdoliłem", "pierdoliłam", "pierdolisz", "pierdolicie", "pierdolący", "pierdoląca");

    public MessageDTO filter(MessageDTO messageDTO) {
        String content = messageDTO.getContent();
        for (String word : badWords) {
            content = content.replaceAll("(?i)\\b" + Pattern.quote(word) + "\\b", mask(word.length()));
        }
        messageDTO.setContent(content);
        return messageDTO;
    }

    private String mask(int length) {
        return "*".repeat(length);
    }

}
