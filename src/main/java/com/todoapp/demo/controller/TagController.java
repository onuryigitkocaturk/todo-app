package com.todoapp.demo.controller;

import com.todoapp.demo.domain.Tag;
import com.todoapp.demo.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag, Principal principal) {
        Tag created = tagService.createTag(tag.getName(), principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getTags(Principal principal) {
        List<Tag> tags = tagService.getTagsByUser(principal.getName());
        return ResponseEntity.ok(tags);
    }
}
