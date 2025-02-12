package com.club_board.club_board_server.response;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public class BooleanTypeIdResolver implements TypeIdResolver {
    private JavaType baseType;

    @Override
    public void init(JavaType baseType) {
        this.baseType = baseType;
    }

    // 직렬화 시, 객체에 따라 "true" 또는 "false" 문자열을 반환합니다.
    @Override
    public String idFromValue(Object value) {
        if (value instanceof SuccessResponseBody) {
            return "true";
        } else if (value instanceof FailedResponseBody) {
            return "false";
        }
        throw new IllegalStateException("Unexpected value: " + value);
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    @Override
    public String idFromBaseType() {
        return "";
    }

    // 역직렬화 시, id 문자열에 따라 올바른 JavaType을 반환합니다.
    @Override
    public JavaType typeFromId(DatabindContext context, String id){
        if ("true".equals(id)) {
            return context.constructType(SuccessResponseBody.class);
        } else if ("false".equals(id)) {
            return context.constructType(FailedResponseBody.class);
        }
        throw new IllegalStateException("Unknown type id: " + id);
    }

    @Override
    public String getDescForKnownTypeIds() {
        return "";
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
