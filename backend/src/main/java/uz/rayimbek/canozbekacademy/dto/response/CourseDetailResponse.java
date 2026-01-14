package uz.rayimbek.canozbekacademy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CourseDetailResponse extends CourseResponse {
    private List<CourseSectionResponse> sections = new ArrayList<>();
}