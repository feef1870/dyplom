package org.zlenko.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.zlenko.backend.dto.PriceTagRequest;
import org.zlenko.backend.dto.PriceTagResponse;
import org.zlenko.backend.services.PriceTagService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/price-tags")
public class PriceTagController {
    private final PriceTagService priceTagService;

    @GetMapping
    public Page<PriceTagResponse> getPriceTags(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean active
    ) {
        return priceTagService.getPriceTags(search, page, size, active);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PriceTagResponse createPriceTag(
            @Valid @RequestBody PriceTagRequest request
    ) {
        return priceTagService.createPriceTag(request);
    }

    @PutMapping("/{id}")
    public PriceTagResponse updatePriceTag(
            @PathVariable Long id,
            @Valid @RequestBody PriceTagRequest request
    ) {
        return priceTagService.updatePriceTag(id, request);
    }

    @PostMapping("/{id}/activate")
    public PriceTagResponse activatePriceTag(@PathVariable Long id) {
        return priceTagService.activate(id);
    }

    @PostMapping("/{id}/deactivate")
    public PriceTagResponse deactivatePriceTag(@PathVariable Long id) {
        return priceTagService.deactivate(id);
    }
}
