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
    public Page<PriceTagResponse> getPriceTags(String search, int page, int size, Boolean active) {
        Pageable pageable = PageRequest.of(page, size);
        boolean hasSearch = search != null && !search.trim().isEmpty();

        if (hasSearch && active != null) {
            return priceTagRepository.findByNameContainingIgnoreCaseAndIsActive(search, active, pageable)
                    .map(PriceTagResponse::from);
        }
        if (hasSearch) {
            return priceTagRepository.findByNameContainingIgnoreCase(search, pageable)
                    .map(PriceTagResponse::from);
        }
        if (active != null) {
            return priceTagRepository.findByIsActive(active, pageable)
                    .map(PriceTagResponse::from);
        }
        return priceTagRepository.findAll(pageable)
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
    public PriceTagResponse updatePriceTag(Long id, PriceTagRequest request) {
        PriceTag priceTag = priceTagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Price tag", id));

        if (!request.name().equalsIgnoreCase(priceTag.getName())
                && priceTagRepository.existsByNameIgnoreCase(request.name())) {
            throw new ConflictException("Price tag with name '" + request.name() + "' already exists");
        }

        priceTag.setName(request.name());
        priceTag.setUnitPrice(request.unitPrice());

        return PriceTagResponse.from(priceTag);
    }

    @Transactional
    @PreAuthorize("hasRole('manager')")
    public PriceTagResponse activate(Long id) {
        return setActive(id, true);
    }

    @Transactional
    @PreAuthorize("hasRole('manager')")
    public PriceTagResponse deactivate(Long id) {
        return setActive(id, false);
    }

    private PriceTagResponse setActive(Long id, boolean active) {
        PriceTag priceTag = priceTagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Price tag", id));

        priceTag.setIsActive(active);

        return PriceTagResponse.from(priceTag);
    }
}
