package com.gorkem.livebettingapi.presentation.model.response;

import com.gorkem.livebettingapi.presentation.model.error.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    private T data;
    private ErrorResponse error;

    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setData(data);
        return response;
    }

    public static <T> Response<T> error(ErrorResponse error) {
        Response<T> response = new Response<>();
        response.setError(error);
        return response;
    }
}
