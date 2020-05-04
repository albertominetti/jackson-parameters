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

public class SingleParamConstructorTest {

    @Test
    void deserialize_with_single_param_constructor_fail() throws JsonProcessingException {
        // given
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .build();

        MyClassWithSingleParamConstructor original = new MyClassWithSingleParamConstructor("one");
        original.setField2("two");
        original.setField3("three");
        String serialized = mapper.writeValueAsString(original);

        // when
        Executable executable = () -> mapper.readValue(serialized, MyClassWithSingleParamConstructor.class);

        // then
        assertThrows(JsonMappingException.class, executable);
    }

    @Data
    private static class MyClassWithSingleParamConstructor {
        private final String field1;
        private String field2;
        private String field3;
    }

    @Test
    void deserialize_with_single_param_constructor_success() throws JsonProcessingException {
        // given
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
                // also the compiler --parameters option is required
                .build();

        MyClassWithSingleParamConstructorOk original = new MyClassWithSingleParamConstructorOk("one");
        original.setField2("two");
        original.setField3("three");
        String serialized = mapper.writeValueAsString(original);

        // when
        MyClassWithSingleParamConstructorOk deserialized = mapper.readValue(serialized, MyClassWithSingleParamConstructorOk.class);

        // then
        assertThat(deserialized, is(notNullValue()));
        assertThat(deserialized.getField1(), is("one"));
        assertThat(deserialized.getField2(), is("two"));
        assertThat(deserialized.getField3(), is("three"));
    }

    @Data
    private static class MyClassWithSingleParamConstructorOk {
        private final String field1;
        private String field2;
        private String field3;

        public MyClassWithSingleParamConstructorOk(@JsonProperty("field1") String field1) {
            this.field1 = field1;
        }
    }
}
