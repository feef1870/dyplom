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
    public PriceTagResponse createNewPriceTag(PriceTagRequest request) {
        PriceTag priceTag = new PriceTag();
        priceTag.setName(request.name());
        priceTag.setUnitPrice(request.unitPrice());
        return PriceTagResponse.from(priceTagRepository.save(priceTag));
    }
}
