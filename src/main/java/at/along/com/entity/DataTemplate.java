package at.along.com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataTemplate {
    private String dataTemplateId;
    private String productId;
    private String dataName;
    private String unitSymbols;
    private String unitName;
}
