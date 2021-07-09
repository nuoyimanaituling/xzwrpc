package io.xzw.xzwrpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Data
@Builder
public class ClassInfo implements Serializable {

    private String schoolName;

    private Integer grade;

    private Map<String, String> stuMap;

    private List<BigDecimal> nums;

    @Override
    public String toString() {
        return "ClassInfo{" +
                "schoolName='" + schoolName + '\'' +
                ", grade=" + grade +
                ", stuMap=" + stuMap +
                '}';
    }
}
