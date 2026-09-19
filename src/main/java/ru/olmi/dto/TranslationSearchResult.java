package ru.olmi.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TranslationSearchResult {

    private List<TranslationResult> results = new ArrayList<>();
}
