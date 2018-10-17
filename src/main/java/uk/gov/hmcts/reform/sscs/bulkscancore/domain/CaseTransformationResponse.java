package uk.gov.hmcts.reform.sscs.bulkscancore.domain;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaseTransformationResponse {
    @ApiModelProperty(value = "Error messages")
    private List<String> errors;
    @ApiModelProperty(value = "Transformed case")
    private Map<String, Object> transformedCase;
}