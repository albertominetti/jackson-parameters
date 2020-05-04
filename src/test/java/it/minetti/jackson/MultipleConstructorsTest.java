package it.minetti.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MultipleConstructorsTest {

    @Test
    void deserialize_with_multiple_constructors_fail() throws JsonProcessingException {
        // given
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .build();

        MyClassWithMultipleConstructors original = new MyClassWithMultipleConstructors("one", "two");
        original.setField3("three");
        String serialized = mapper.writeValueAsString(original);

        // when
        Executable executable = () -> mapper.readValue(serialized, MyClassWithMultipleConstructors.class);

        // then
        assertThrows(JsonMappingException.class, executable);
    }

    @Data
    @RequiredArgsConstructor
    private static class MyClassWithMultipleConstructors {
        private final String field1;
        private final String field2;
        private String field3;
        // Lombok generates a constructor with parameters for field1 and field2
        // Lombok generates the setter for the field3

        public MyClassWithMultipleConstructors(String field1, String field2, String field3) {
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }
    }

    @Test
    void deserialize_with_multiple_constructors_success() throws JsonProcessingException {
        // given
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                // also the compiler --parameters option is required
                .build();

        MyClassWithMultipleConstructorsOk original = new MyClassWithMultipleConstructorsOk("one", "two");
        original.setField3("three");
        String serialized = mapper.writeValueAsString(original);

        // when
        MyClassWithMultipleConstructorsOk deserialized = mapper.readValue(serialized, MyClassWithMultipleConstructorsOk.class);

        // then
        assertThat(deserialized, is(notNullValue()));
        assertThat(deserialized.getField1(), is("one"));
        assertThat(deserialized.getField2(), is("two"));
        assertThat(deserialized.getField3(), is("three"));
    }

    @Data
    @RequiredArgsConstructor(onConstructor = @__(@JsonCreator()))
    private static class MyClassWithMultipleConstructorsOk {
        private final String field1;
        private final String field2;
        private String field3;
        // Lombok generates a constructor with a single parameters for the field1
        // Lombok generates the setter for the field2 and field3

        public MyClassWithMultipleConstructorsOk(String field1, String field2, String field3) {
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }
    }


    @Test
    void deserialize_with_multiple_constructors_no_lombok_success() throws JsonProcessingException {
        // given
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                // also the compiler --parameters option is required
                .build();

        MyClassWithMultipleConstructorsNoLombokOk original = new MyClassWithMultipleConstructorsNoLombokOk("one", "two");
        original.setField3("three");
        String serialized = mapper.writeValueAsString(original);

        // when
        MyClassWithMultipleConstructorsNoLombokOk deserialized = mapper.readValue(serialized, MyClassWithMultipleConstructorsNoLombokOk.class);

        // then
        assertThat(deserialized, is(notNullValue()));
        assertThat(deserialized.getField1(), is("one"));
        assertThat(deserialized.getField2(), is("two"));
        assertThat(deserialized.getField3(), is("three"));
    }

    @Data
    private static class MyClassWithMultipleConstructorsNoLombokOk {
        private final String field1;
        private final String field2;
        private String field3;
        // Lombok generates a constructor with a single parameters for the field1
        // Lombok generates the setter for the field2 and field3

        @JsonCreator
        public MyClassWithMultipleConstructorsNoLombokOk(String field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        public MyClassWithMultipleConstructorsNoLombokOk(String field1, String field2, String field3) {
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }
    }

}
