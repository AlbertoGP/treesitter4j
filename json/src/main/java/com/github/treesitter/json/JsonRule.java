package com.github.treesitter.json;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Alias.class, name = "ALIAS"),
        @JsonSubTypes.Type(value = Blank.class, name = "BLANK"),
        @JsonSubTypes.Type(value = Choice.class, name = "CHOICE"),
        @JsonSubTypes.Type(value = DynamicPrecedence.class, name = "PREC_DYNAMIC"),
        @JsonSubTypes.Type(value = ImmediateToken.class, name = "IMMEDIATE_TOKEN"),
        @JsonSubTypes.Type(value = LeftPrecedence.class, name = "PREC_LEFT"),
        @JsonSubTypes.Type(value = Pattern.class, name = "PATTERN"),
        @JsonSubTypes.Type(value = Precedence.class, name = "PREC"),
        @JsonSubTypes.Type(value = RightPrecedence.class, name = "PREC_RIGHT"),
        @JsonSubTypes.Type(value = Sequence.class, name = "SEQ"),
        @JsonSubTypes.Type(value = StringValue.class, name = "STRING"),
        @JsonSubTypes.Type(value = Symbol.class, name = "SYMBOL"),
        @JsonSubTypes.Type(value = Token.class, name = "TOKEN"),
})
public class JsonRule<T> {
}
