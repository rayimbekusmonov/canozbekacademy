package uz.rayimbek.canozbekacademy.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateProgressRequest {

    @NotNull(message = "Video progress is required")
    @Min(value = 0, message = "Video progress must be positive")
    private Integer videoProgress; // seconds watched
}