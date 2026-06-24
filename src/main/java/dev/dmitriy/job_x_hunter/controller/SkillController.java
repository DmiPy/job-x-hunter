package dev.dmitriy.job_x_hunter.controller;

import dev.dmitriy.job_x_hunter.dto.SkillResponseDTO;
import dev.dmitriy.job_x_hunter.service.SkillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService){
        this.skillService = skillService;
    }

    @GetMapping
    public List<SkillResponseDTO> getAllSkills(){
        return this.skillService.getAllSkills();
    }

    @GetMapping("/{skillId}")
    public SkillResponseDTO getSkillById(@PathVariable Long skillId){
        return this.skillService.getSkillById(skillId);
    }
}
