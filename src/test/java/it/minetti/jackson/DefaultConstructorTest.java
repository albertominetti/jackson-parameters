package it.minetti.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class DefaultConstructorTest {

    @Test
    void deserialize_using_default_constructor() throws JsonProcessingException {
        // given
        ObjectMapper mapper = JsonMapper.builder().build();
        MyClassWithDefaultConstructors original = new MyClassWithDefaultConstructors();
        original.setField1("one");
        original.setField2("two");
        original.setField3("three");

        String serialized = mapper.writeValueAsString(original);

        // when
        MyClassWithDefaultConstructors deserialized = mapper.readValue(serialized, MyClassWithDefaultConstructors.class);

        // then
        assertThat(deserialized, is(notNullValue()));
        assertThat(deserialized.getField1(), is("one"));
        assertThat(deserialized.getField2(), is("two"));
        assertThat(deserialized.getField3(), is("three"));
    }

    @Data
    private static class MyClassWithDefaultConstructors {
        private String field1;
        private String field2;
        private String field3;
        // Lombok generates a default constructor with no parameters
        // Lombok generates the setterd for the field1, field2 and field3
    }

}
