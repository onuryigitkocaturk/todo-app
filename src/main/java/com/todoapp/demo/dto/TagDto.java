package com.todoapp.demo.dto;

public class TagDto {
    private Long id;
    private String name;

    public TagDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
