package com.siaskov.model;

import java.util.List;

/**
 * Created by EgorSiaskov on 02/08/16.
 */
public interface CourseIdeaDAO {
    boolean add(CourseIdea idea);

    List<CourseIdea> findAll();

    CourseIdea findBySlug(String slug);
}
