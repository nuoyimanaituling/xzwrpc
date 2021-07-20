package io.xzw.xzwrpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


/**
 * @author xzw
 */
@Data
@Builder
public class User implements Serializable {

    private Long id;

    private String username;

    private Integer age;

    private io.xzw.xzwrpc.entity.ClassInfo info;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
