package ru.olmi.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DictionaryImportResult {

    private int processed;
    private int added;
    private int updated;
    private int unchanged;
    private int errors;

    private List<String> errorMessages = new ArrayList<>();
}
