package com.example.BlogAPI.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagRequest {
    @NotBlank(message = "Tag cannot be a blank")
    @Size(min = 1, max = 20, message = "Tag must be between 1 and 20")
    private String name;
}
