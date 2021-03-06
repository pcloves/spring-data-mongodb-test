package org.gamedo.db;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Document("TestData")
@Data
public class TestData {

    private final List<Integer> synchronizedList =
            Collections.synchronizedList(IntStream.rangeClosed(1, 10000).boxed().collect(Collectors.toList()));
}
