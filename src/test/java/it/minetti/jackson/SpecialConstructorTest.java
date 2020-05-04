package it.minetti.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpecialConstructorTest {

    @Test
    void deserialize_with_special_constructor_fail() throws JsonProcessingException {
        // given
        ObjectMapper mapper = JsonMapper.builder().build();

        MyClassWithSpecialConstructor original = new MyClassWithSpecialConstructor("one", "two");
        original.setField3("three");
        String serialized = mapper.writeValueAsString(original);

        // when
        Executable executable = () -> mapper.readValue(serialized, MyClassWithSpecialConstructor.class);

        // then
        assertThrows(JsonMappingException.class, executable);
    }

    @Test
    void deserialize_with_special_constructor_success() throws JsonProcessingException {
        // given
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                // also the compiler --parameters option is required
                .build();

        MyClassWithSpecialConstructor original = new MyClassWithSpecialConstructor("one", "two");
        original.setField3("three");
        String serialized = mapper.writeValueAsString(original);

        // when
        MyClassWithSpecialConstructor deserialized = mapper.readValue(serialized, MyClassWithSpecialConstructor.class);

        // then
        assertThat(deserialized, is(notNullValue()));
        assertThat(deserialized.getField1(), is("one"));
        assertThat(deserialized.getField2(), is("two"));
        assertThat(deserialized.getField3(), is("three"));
    }

    @Data
    private static class MyClassWithSpecialConstructor {
        private final String field1;
        private final String field2;
        private String field3;
        // Lombok generates a constructor with parameters for field1 and field2
        // Lombok generates the setter for the field3
    }


    @Test
    void deserialize_with_special_constructor_annotations_success() throws JsonProcessingException {
        // given
        ObjectMapper mapper = JsonMapper.builder()
                .build();

        MyClassWithSpecialConstructorWithAnnotations original = new MyClassWithSpecialConstructorWithAnnotations("one", "two");
        original.setField3("three");
        String serialized = mapper.writeValueAsString(original);

        // when
        MyClassWithSpecialConstructorWithAnnotations deserialized = mapper.readValue(serialized, MyClassWithSpecialConstructorWithAnnotations.class);

        // then
        assertThat(deserialized, is(notNullValue()));
        assertThat(deserialized.getField1(), is("one"));
        assertThat(deserialized.getField2(), is("two"));
        assertThat(deserialized.getField3(), is("three"));
    }


    @Data
    private static class MyClassWithSpecialConstructorWithAnnotations {
        private final String field1;
        private final String field2;
        private String field3;
        // Lombok generates the setter for the field3

        @JsonCreator
        public MyClassWithSpecialConstructorWithAnnotations(@JsonProperty("field1") String field1, @JsonProperty("field2") String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }


}
