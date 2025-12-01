package com.example.BlogAPI.tag;

import com.example.BlogAPI.tag.dto.TagRequest;
import com.example.BlogAPI.tag.dto.TagResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagsController {

    private final TagsService tagsService;

    @Autowired
    public TagsController(TagsService tagsService) {
        this.tagsService = tagsService;
    }

    @GetMapping()
    public ResponseEntity<List<TagResponse>> getAllTags() {
        List<TagResponse> tags = tagsService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getTagById(@PathVariable Long id) {
        TagResponse tagResponse = tagsService.getTagByIdWithPosts(id);
        return ResponseEntity.ok(tagResponse);
    }

    @PostMapping()
    public ResponseEntity<?> addTag(@RequestBody TagRequest tagRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        TagResponse createdTag = tagsService.addTag(tagRequest);
        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Long id) {
        tagsService.deleteTagById(id);
        return ResponseEntity.ok().build();
    }
}
