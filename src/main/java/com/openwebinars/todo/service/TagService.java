package com.openwebinars.todo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openwebinars.todo.dto.TagRequestDto;
import com.openwebinars.todo.error.BusinessRuleException;
import com.openwebinars.todo.error.TagNotFoundException;
import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.repos.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Tag findById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
    }

    public Tag save(TagRequestDto dto) {
        if (dto.name() == null || dto.name().trim().isEmpty()) {
            throw new BusinessRuleException("El nombre de la etiqueta no puede estar vacío.");
        }

        Tag newTag = new Tag();
        newTag.setName(dto.name().trim());
        return tagRepository.save(newTag);
    }

    public Tag edit(Long id, TagRequestDto dto) {
        if (dto.name() == null || dto.name().trim().isEmpty()) {
            throw new BusinessRuleException("El nombre de la etiqueta no puede estar vacío.");
        }

        return tagRepository.findById(id)
                .map(tag -> {
                    tag.setName(dto.name().trim());
                    return tagRepository.save(tag);
                })
                .orElseThrow(() -> new TagNotFoundException(id));
    }

    public void delete(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new TagNotFoundException(id);
        }
        tagRepository.deleteById(id);
    }
}