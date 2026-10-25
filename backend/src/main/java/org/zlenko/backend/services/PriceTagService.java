package org.zlenko.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zlenko.backend.dto.PriceTagRequest;
import org.zlenko.backend.dto.PriceTagResponse;
import org.zlenko.backend.dto.UpdatePriceTagRequest;
import org.zlenko.backend.entities.PriceTag;
import org.zlenko.backend.exceptions.ConflictException;
import org.zlenko.backend.exceptions.NotFoundException;
import org.zlenko.backend.repositories.PriceTagRepository;

@Service
@RequiredArgsConstructor
public class PriceTagService {
    private final PriceTagRepository priceTagRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('manager')")
    public Page<PriceTagResponse> getPriceTags(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (search == null || search.trim().isEmpty()) {
            return priceTagRepository.findAll(pageable)
                    .map(PriceTagResponse::from);
        }

        return priceTagRepository.findByNameContainingIgnoreCase(search, pageable)
                .map(PriceTagResponse::from);
    }

    @Transactional
    @PreAuthorize("hasRole('manager')")
    public PriceTagResponse createPriceTag(PriceTagRequest request) {
        if (priceTagRepository.existsByNameIgnoreCase(request.name())) {
            throw new ConflictException("Price tag with name '" + request.name() + "' already exists");
        }

        PriceTag priceTag = new PriceTag();
        priceTag.setName(request.name());
        priceTag.setUnitPrice(request.unitPrice());
        priceTag.setIsActive(true);
        return PriceTagResponse.from(priceTagRepository.save(priceTag));
    }

    @Transactional
    @PreAuthorize("hasRole('manager')")
    public PriceTagResponse updatePriceTag(Long id, UpdatePriceTagRequest request) {
        PriceTag priceTag = priceTagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Price tag", id));

        if (request.name() != null && !request.name().equalsIgnoreCase(priceTag.getName()) && priceTagRepository.existsByNameIgnoreCase(request.name())) {
            throw new ConflictException("Price tag with name '" + request.name() + "' already exists");
        }

        if (request.name() != null) {
            priceTag.setName(request.name());
        }
        if (request.unitPrice() != null) {
            priceTag.setUnitPrice(request.unitPrice());
        }
        if (request.isActive() != null) {
            priceTag.setIsActive(request.isActive());
        }

        return PriceTagResponse.from(priceTag);
    }

    @Transactional
    @PreAuthorize("hasRole('manager')")
    public void deletePriceTag(Long id) {
        PriceTag priceTag = priceTagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Price tag", id));

        priceTag.setIsActive(false);
    }
}
