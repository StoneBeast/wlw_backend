package at.along.com.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result <T> implements Serializable {
    private int code;
    private String message;
    private T data;
}
