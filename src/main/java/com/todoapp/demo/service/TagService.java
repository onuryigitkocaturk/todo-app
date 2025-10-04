package com.todoapp.demo.service;

import com.todoapp.demo.domain.Tag;
import com.todoapp.demo.domain.User;
import com.todoapp.demo.repository.TagRepository;
import com.todoapp.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public TagService(TagRepository tagRepository, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    public Tag createTag(String tagName, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setUser(user);

        return tagRepository.save(tag);
    }

    public List<Tag> getTagsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return tagRepository.findByUser(user);
    }
}
