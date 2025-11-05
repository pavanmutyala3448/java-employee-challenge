package com.reliaquest.api.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeInput {
    @NotBlank
    private String name;

    @NotNull @Min(1)
    private Integer salary;

    @NotNull @Min(16)
    private Integer age;

    @NotBlank
    private String title;
}
